package tw.com.kyle.bean;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * @author Kyle
 * @since 2025/3/6
 */
@Component
public class PathBasedAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext context) {
        Authentication auth = authentication.get();

        if (auth == null || !auth.isAuthenticated()) {
            return new AuthorizationDecision(false);
        }

        String requestPath = context.getRequest().getServletPath();
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();

        boolean hasPermission = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("/"))
                .anyMatch(authority -> antPathMatcher.match(authority, requestPath));

        return new AuthorizationDecision(hasPermission);
    }
}
