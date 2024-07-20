package ag.act.core.aop.log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExternalApiLogAspect extends ApiLogAspect {

    private final ApiLogHelper apiLogHelper;

    public ExternalApiLogAspect(ApiLogHelper apiLogHelper) {
        this.apiLogHelper = apiLogHelper;
    }

    @Around("execution(* ag.act.external.http.HttpClientUtil.*(..))")
    public Object aroundExternalCalls(ProceedingJoinPoint joinPoint) throws Throwable {
        return apiLogHelper.callAndLog(
            joinPoint,
            "ExternalApiTime",
            () -> ApiLogHelper.ApiInfo.of(getUri(joinPoint), getMethodName(joinPoint).toUpperCase())
        );
    }

    @Around("execution(* ag.act.module.markany.dna.MarkAnyDNAClient.makeDna(..))")
    public Object aroundCallMarkAny(ProceedingJoinPoint joinPoint) throws Throwable {
        return apiLogHelper.callAndLog(
            joinPoint,
            "ExternalApiTime",
            () -> ApiLogHelper.ApiInfo.of(getMethodName(joinPoint), "MarkAnyDNA")
        );
    }

    @Around("execution(* ag.act.module.okcert.OkCertClient.*(..))")
    public Object aroundCallOkCert(ProceedingJoinPoint joinPoint) throws Throwable {
        return apiLogHelper.callAndLog(
            joinPoint,
            "ExternalApiTime",
            () -> ApiLogHelper.ApiInfo.of(getMethodName(joinPoint), "OkCert")
        );
    }
}
