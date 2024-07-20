package ag.act.core.request;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("DataFlowIssue")
@Component
@Profile("!test-persistence")
public class RequestMappingHandlerChecker {
    private final Set<PathPattern> pathPatterns;
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    public RequestMappingHandlerChecker(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();

        pathPatterns = handlerMethods
            .keySet()
            .stream()
            .flatMap(requestMappingInfo -> requestMappingInfo.getPathPatternsCondition().getPatterns().stream())
            .collect(Collectors.toSet());
    }

    private boolean isUrlPatternMatch(String requestUri, String urlPattern) {
        return pathMatcher.match(urlPattern, requestUri);
    }

    public boolean hasHandlerForUrl(HttpServletRequest request) {
        return pathPatterns.stream().anyMatch(pattern -> isUrlPatternMatch(request.getRequestURI(), pattern.getPatternString()));
    }
}
