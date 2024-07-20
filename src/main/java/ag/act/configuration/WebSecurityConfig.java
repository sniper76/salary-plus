package ag.act.configuration;

import ag.act.configuration.security.JwtAuthenticationFilter;
import ag.act.configuration.urlmatcher.PermitAllRequestMatcher;
import ag.act.core.filter.MonitoringApiKeyAuthFilter;
import ag.act.core.filter.RequestContextFilter;
import ag.act.core.filter.response.ErrorResponseResolver;
import ag.act.core.request.RequestMappingHandlerChecker;
import ag.act.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;
import org.springframework.security.web.session.DisableEncodeUrlFilter;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Profile("!test-persistence")
public class WebSecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ErrorResponseResolver errorResponseResolver;
    private final RequestMappingHandlerChecker requestMappingHandlerChecker;
    private final PermitAllRequestMatcher permitAllRequestMatcher;
    private final RequestContextFilter requestContextFilter;
    private final MonitoringApiKeyAuthFilter monitoringApiKeyAuthFilter;

    @SuppressWarnings("removal")
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors() // WebMvcConfig에서 이미 설정했으므로 기본 cors 설정.
            .and()
            .csrf().disable().httpBasic().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeHttpRequests(requests -> requests
                .requestMatchers(permitAllRequestMatcher).permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling()
            .authenticationEntryPoint(getAuthenticationEntryPoint());

        http.addFilterBefore(requestContextFilter, DisableEncodeUrlFilter.class);
        http.addFilterAfter(monitoringApiKeyAuthFilter, RequestHeaderAuthenticationFilter.class);
        http.addFilterAfter(jwtAuthenticationFilter, CorsFilter.class);

        return http.build();
    }

    private AuthenticationEntryPoint getAuthenticationEntryPoint() {
        return (request, response, authException) -> {

            if (!requestMappingHandlerChecker.hasHandlerForUrl(request)) {
                errorResponseResolver.setErrorResponse(response, createNoHandlerFoundException(request));
                return;
            }

            errorResponseResolver.setErrorResponse(response, createUnauthorizedException(authException));
        };
    }

    private UnauthorizedException createUnauthorizedException(AuthenticationException authException) {
        return new UnauthorizedException("인가되지 않은 접근입니다.", authException);
    }

    private NoHandlerFoundException createNoHandlerFoundException(HttpServletRequest request) {
        return new NoHandlerFoundException(request.getMethod(), request.getRequestURI(), new ServletServerHttpRequest(request).getHeaders());
    }
}
