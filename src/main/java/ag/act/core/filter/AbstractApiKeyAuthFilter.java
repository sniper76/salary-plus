package ag.act.core.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Slf4j
public abstract class AbstractApiKeyAuthFilter extends OncePerRequestFilter implements ApiKeyAuthFilter {
    protected final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
    protected final AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();

    @SuppressWarnings("NullableProblems")
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            final boolean iaMatchedUrl = isMatchedUrlOf(request);
            final String principal = getAuthenticatedPrincipal(request);

            if (iaMatchedUrl && Objects.equals(getApiKey(), principal)) {
                authenticate(request, principal);
            }
        } catch (Exception ex) {
            log.error("RequestURI: {} : {}", request.getRequestURI(), ex.getMessage());
            setErrorResponse(response, ex);
            return;
        }

        filterChain.doFilter(request, response);
    }

    protected void authenticate(HttpServletRequest request, String principal) {
        final PreAuthenticatedAuthenticationToken authentication = createAuthentication(request, principal);
        final SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        securityContextHolderStrategy.setContext(context);
        successfulAuthentication(request, authentication);
    }

    protected void successfulAuthentication(HttpServletRequest request, PreAuthenticatedAuthenticationToken authentication) {
        // doing nothing
    }

    protected PreAuthenticatedAuthenticationToken createAuthentication(HttpServletRequest request, String principal) {
        PreAuthenticatedAuthenticationToken authentication = new PreAuthenticatedAuthenticationToken(principal, NA_CREDENTIALS);
        authentication.setAuthenticated(true);
        authentication.setDetails(authenticationDetailsSource.buildDetails(request));
        return authentication;
    }

    protected String getAuthenticatedPrincipal(HttpServletRequest request) {
        return request.getHeader(getHeaderKey());
    }

    protected String getHeaderKey() {
        return DEFAULT_X_API_HEADER_KEY;
    }

    protected abstract String getApiKey();

    protected abstract void setErrorResponse(HttpServletResponse response, Exception ex) throws IOException;

    protected abstract boolean isMatchedUrlOf(HttpServletRequest request);
}
