package tw.com.blockchain.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import tw.com.blockchain.bean.PathBasedAuthorizationManager;
import tw.com.blockchain.filter.JwtAuthenticationFilter;

import java.util.Collections;
import java.util.List;

/**
 * @author Kyle
 * @since 2025/2/25
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${security.ignore-security-check-uri-list}")
    private String[] AUTH_WHITELIST;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final PathBasedAuthorizationManager pathBasedAuthorizationManager;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                // 無狀態不需要session
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(AUTH_WHITELIST).permitAll()
                        .anyRequest().access(pathBasedAuthorizationManager))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden")))
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 1.允許任何來源
        configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
        // 2.允許任何請求頭
        configuration.addAllowedHeader(CorsConfiguration.ALL);
        // 3.允許任何方法
        configuration.addAllowedMethod(CorsConfiguration.ALL);
        // 4.允許憑證
        configuration.setAllowCredentials(true);
        // 5.允許對外曝光的請求頭
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
