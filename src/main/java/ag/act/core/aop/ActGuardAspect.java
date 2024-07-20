package ag.act.core.aop;

import ag.act.core.guard.ActGuard;
import ag.act.core.guard.UseGuards;
import ag.act.util.AspectParameterUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
public class ActGuardAspect {

    private final Map<String, ActGuard> actGuardMap;

    @Autowired
    public ActGuardAspect(Set<ActGuard> actGuards) {
        actGuardMap = actGuards.stream().collect(Collectors.toMap(actGuard -> actGuard.getClass().getName(), Function.identity()));
    }

    @Before("@within(ag.act.core.guard.UseGuards)")
    public void canActivateOnClasses(JoinPoint joinPoint) {
        applyAnnotation(joinPoint, findClassAnnotation(joinPoint));
    }

    @Before("@annotation(ag.act.core.guard.UseGuards)")
    public void canActivateOnMethods(JoinPoint joinPoint) {
        applyAnnotation(joinPoint, findMethodAnnotation(joinPoint));
    }

    private void applyAnnotation(JoinPoint joinPoint, UseGuards useGuards) {
        final Map<String, Object> parameterMap = AspectParameterUtil.findParameters(joinPoint);
        for (Class<? extends ActGuard> guard : useGuards.value()) {
            invokeGuard(guard, parameterMap);
        }
    }

    private UseGuards findMethodAnnotation(JoinPoint joinPoint) {

        final Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        final UseGuards useGuards = AnnotationUtils.findAnnotation(method, UseGuards.class);
        Objects.requireNonNull(useGuards, "Can not find UseGuards annotation on method");

        return useGuards;
    }

    private UseGuards findClassAnnotation(JoinPoint joinPoint) {

        final Class<?> aClass = joinPoint.getTarget().getClass();
        final UseGuards useGuards = AnnotationUtils.findAnnotation(aClass, UseGuards.class);
        Objects.requireNonNull(useGuards, "Can not find UseGuards annotation on class");

        return useGuards;
    }

    private void invokeGuard(Class<? extends ActGuard> guard, Map<String, Object> parameterMap) {
        actGuardMap.get(guard.getName()).canActivate(parameterMap);
    }
}
