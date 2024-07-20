package ag.act.core.filter;

import ag.act.core.filter.response.ErrorResponseResolver;
import ag.act.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class MonitoringApiKeyAuthFilter extends AbstractApiKeyAuthFilter {
    private static final AntPathRequestMatcher MONITORING_PATH_MATCHER = new AntPathRequestMatcher("/actuator/**");
    private final ErrorResponseResolver errorResponseResolver;
    private final String monitoringApiKey;

    public MonitoringApiKeyAuthFilter(
        @Value("${act.monitoring.api-key:monitoringApiKey}") String monitoringApiKey,
        ErrorResponseResolver errorResponseResolver
    ) {
        this.errorResponseResolver = errorResponseResolver;
        this.monitoringApiKey = monitoringApiKey;
    }

    @Override
    protected void setErrorResponse(HttpServletResponse response, Exception ex) throws IOException {
        errorResponseResolver.setErrorResponse(response, new UnauthorizedException(ERROR_MESSAGE, ex));
    }

    @Override
    protected boolean isMatchedUrlOf(HttpServletRequest request) {
        return MONITORING_PATH_MATCHER.matches(request);
    }

    @Override
    protected String getApiKey() {
        return monitoringApiKey;
    }
}
