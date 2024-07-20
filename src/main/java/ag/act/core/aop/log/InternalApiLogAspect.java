package ag.act.core.aop.log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(1)
@Aspect
@Component
public class InternalApiLogAspect extends ApiLogAspect {

    private static final String LOG_PREFIX = "InternalApiTime";
    private final ApiLogHelper apiLogHelper;

    public InternalApiLogAspect(ApiLogHelper apiLogHelper) {
        this.apiLogHelper = apiLogHelper;
    }

    @Around("execution(* ag.act.handler.*ApiDelegateImpl.*(..))")
    public Object aroundInternalCalls(ProceedingJoinPoint joinPoint) throws Throwable {
        return apiLogHelper.callAndLog(joinPoint, LOG_PREFIX);
    }

    @Around("execution(* ag.act.handler.UserApiDelegateImpl.updateMyData(..))")
    public Object aroundCallUpdateMyData(ProceedingJoinPoint joinPoint) throws Throwable {
        return apiLogHelper.callAndLog(
            joinPoint,
            LOG_PREFIX,
            () -> ApiLogHelper.ApiInfo.of(getMethodName(joinPoint), "UpdateMyData", 5000)
        );
    }
}
