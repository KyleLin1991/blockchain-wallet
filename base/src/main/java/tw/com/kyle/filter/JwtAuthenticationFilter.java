package tw.com.kyle.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tw.com.kyle.util.JwtTokenUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Kyle
 * @since 2025/2/26
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String TOKEN_HEADER = "Authorization";
        String TOKEN_PREFIX = "Bearer ";

        String authHeader = request.getHeader(TOKEN_HEADER);

        if (authHeader != null && authHeader.startsWith(TOKEN_PREFIX)) {
            String token = authHeader.replace(TOKEN_PREFIX, "");

            Optional<Map<String, Object>> opt = jwtTokenUtil.parseToken(token);

            opt.ifPresent(map -> {
                String account = (String) map.get("sub");
                // 解析 roles
                List<String> roles = ((List<?>) map.get("roles")).stream()
                        .map(obj -> {
                            if (obj instanceof Map) {
                                return (String) ((Map<?, ?>) obj).get("roleCode"); // 提取 roleCode
                            }
                            return obj.toString();
                        })
                        .toList();
                // 解析 privileges
                List<String> privileges = ((List<?>) map.get("privileges")).stream()
                        .map(obj -> {
                            if (obj instanceof Map) {
                                return (String) ((Map<?, ?>) obj).get("pid"); // 提取 pid
                            }
                            return obj.toString();
                        })
                        .toList();

                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.addAll(roles.stream().map(SimpleGrantedAuthority::new).toList());
                authorities.addAll(privileges.stream().map(SimpleGrantedAuthority::new).toList());

                var authentication = new UsernamePasswordAuthenticationToken(
                        account,
                        null,
                        authorities
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            });
        }
        filterChain.doFilter(request, response);
    }
}
