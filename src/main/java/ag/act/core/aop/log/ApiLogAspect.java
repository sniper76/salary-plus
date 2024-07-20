package ag.act.core.aop.log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

public abstract class ApiLogAspect {

    protected String getUri(ProceedingJoinPoint joinPoint) {
        return joinPoint.getArgs()[0].toString();
    }

    protected String getMethodName(ProceedingJoinPoint joinPoint) {
        return ((MethodSignature) joinPoint.getSignature()).getMethod().getName();
    }
}
