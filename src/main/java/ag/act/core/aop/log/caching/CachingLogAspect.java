package ag.act.core.aop.log.caching;

import ag.act.configuration.initial.caching.Caching;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class CachingLogAspect {

    //    @Before("execution(* ag.act.configuration.initial.caching.CachingScheduler.run(..))")
    public void aroundCachingRunMethod(JoinPoint joinPoint) {
        try {
            logStart((Caching) joinPoint.getThis());
        } catch (Throwable throwable) {
            log.error("[CACHING] Error occurred while loading cache", throwable);
            throw throwable;
        }
    }

    //    @Around("execution(* ag.act.configuration.initial.caching.CachingLoader.load(..))")
    public Object aroundCachingLoadMethod(ProceedingJoinPoint joinPoint) throws Throwable {

        long startTime = System.currentTimeMillis();

        try {
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            log.error("[CACHING] Error occurred while loading cache", throwable);
            throw throwable;
        } finally {
            logFinish((Caching) joinPoint.getThis(), startTime);
        }
    }

    private void logStart(Caching caching) {
        log.info("[CACHING] All {} are [cleared] from {} cache", caching.getLogName(), caching.getCacheName());
    }

    private void logFinish(Caching caching, long startTime) {
        long executionTime = System.currentTimeMillis() - startTime;
        log.info("[CACHING] All {} are [loaded] to {} cache in {} ms", caching.getLogName(), caching.getCacheName(), executionTime);
    }
}
