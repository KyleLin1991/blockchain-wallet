package tw.com.kyle.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tw.com.kyle.misc.SafetyHttpRequest;

import java.io.IOException;

/**
 * 保護 Web 元件免受注入駭客攻擊Filter
 * @author Kyle
 * @since 2025/2/26
 */
@Component
public class SafetyHttpRequestFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        filterChain.doFilter(new SafetyHttpRequest(request), response);
    }
}
