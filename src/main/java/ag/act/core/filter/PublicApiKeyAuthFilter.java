package ag.act.core.filter;

import ag.act.core.filter.response.ErrorResponseResolver;
import ag.act.core.holder.RequestContextHolder;
import ag.act.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class PublicApiKeyAuthFilter extends AbstractApiKeyAuthFilter {
    private static final AntPathRequestMatcher PUBLIC_API_PATH_MATCHER = new AntPathRequestMatcher("/public-api/**");
    private final ErrorResponseResolver errorResponseResolver;
    private final String publicApiKey;

    public PublicApiKeyAuthFilter(
        @Value("${act.public.api-key:publicApiKey}") String publicApiKey,
        ErrorResponseResolver errorResponseResolver
    ) {
        this.errorResponseResolver = errorResponseResolver;
        this.publicApiKey = publicApiKey;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, PreAuthenticatedAuthenticationToken authentication) {
        RequestContextHolder.setIsPublicApi(Boolean.TRUE);
    }

    @Override
    protected void setErrorResponse(HttpServletResponse response, Exception ex) throws IOException {
        errorResponseResolver.setErrorResponse(response, new UnauthorizedException(ERROR_MESSAGE, ex));
    }

    @Override
    protected boolean isMatchedUrlOf(HttpServletRequest request) {
        return PUBLIC_API_PATH_MATCHER.matches(request);
    }

    @Override
    protected String getApiKey() {
        return publicApiKey;
    }
}
