package ag.act.core.aop.log;

import ag.act.configuration.security.ActUserProvider;
import ag.act.core.holder.RequestContextHolder;
import ag.act.entity.User;
import ag.act.util.AspectParameterUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("ClassEscapesDefinedScope")
@Slf4j
@Component
public class ApiLogHelper {

    private static final int THRESHOLD_MAX_TIME_IN_MS = 1000;

    public Object callAndLog(ProceedingJoinPoint joinPoint, String logPrefix) throws Throwable {
        return callAndLog(joinPoint, logPrefix, () -> getApiInfo(joinPoint));
    }

    public Object callAndLog(ProceedingJoinPoint joinPoint, String logPrefix, Supplier<ApiInfo> apiUriSupplier) throws Throwable {
        final long startTime = System.currentTimeMillis();

        try {
            return joinPoint.proceed();
        } finally {
            final long executionTime = System.currentTimeMillis() - startTime;

            logIfNeeded(logPrefix, apiUriSupplier, executionTime);
        }
    }

    private void logIfNeeded(String logPrefix, Supplier<ApiInfo> apiUriSupplier, long executionTime) {
        if (executionTime < THRESHOLD_MAX_TIME_IN_MS) {
            return;
        }

        final ApiInfo apiInfo = apiUriSupplier.get();
        if (executionTime < apiInfo.getThresholdMaxTimeInMs()) {
            return;
        }

        log.info(
            "[{}] {} {} userId:{} took {} s to execute.",
            logPrefix,
            apiInfo.getMethod(),
            apiInfo.getUri(),
            getUserId(),
            (executionTime / 1000.0)
        );
    }

    public ApiInfo getApiInfo(ProceedingJoinPoint joinPoint) {
        try {
            final String apiInterfaceName = getOpenApiControllerInterfaceName(joinPoint);
            final Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
            final Method interfaceMethod = Class.forName(apiInterfaceName).getDeclaredMethod(method.getName(), method.getParameterTypes());
            final RequestMapping requestMappingAnnotation = AnnotationUtils.findAnnotation(interfaceMethod, RequestMapping.class);

            if (requestMappingAnnotation != null) {
                return ApiInfo.of(
                    getUri(joinPoint, requestMappingAnnotation.value()[0]),
                    requestMappingAnnotation.method()[0].name()
                );
            }
        } catch (Exception e) {
            log.warn("Failed to get api uri", e);
        }

        return ApiInfo.of(getClassAndMethodName(joinPoint), "");
    }

    private String getUri(ProceedingJoinPoint joinPoint, String uri) {

        final String apiUri = getApiUri();
        if (apiUri != null) {
            return apiUri;
        }

        return getUriWithValues(joinPoint, uri);
    }

    private static String getUriWithValues(ProceedingJoinPoint joinPoint, String uri) {
        final Map<String, Object> parameters = AspectParameterUtil.findParameters(joinPoint);

        String tempUri = uri;

        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            String paramName = entry.getKey();
            Object paramValue = entry.getValue();

            if (paramValue != null) {
                tempUri = tempUri.replace("{" + paramName + "}", paramValue.toString());
            }
        }

        return tempUri;
    }

    private String getApiUri() {
        return RequestContextHolder.getRequestURI();
    }

    private String getOpenApiControllerInterfaceName(ProceedingJoinPoint joinPoint) {
        return joinPoint.getTarget().getClass().getName()
            .replace("DelegateImpl", "")
            .replace("ag.act.handler", "ag.act.api");
    }

    private String getClassAndMethodName(ProceedingJoinPoint joinPoint) {
        final String className = joinPoint.getTarget().getClass().getSimpleName();
        final String methodName = joinPoint.getSignature().getName();

        return "%s.%s()".formatted(className, methodName);
    }

    private Long getUserId() {
        try {
            return ActUserProvider.get()
                .map(User::getId)
                .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    static class ApiInfo {
        private String uri;
        private String method;
        private int thresholdMaxTimeInMs;

        public static ApiInfo of(String uri) {
            return of(uri, "");
        }

        public static ApiInfo of(String uri, String method) {
            return of(uri, method, THRESHOLD_MAX_TIME_IN_MS);
        }

        public static ApiInfo of(String uri, String method, int thresholdMaxTimeInMs) {
            return new ApiInfo(uri, method, thresholdMaxTimeInMs);
        }
    }
}
