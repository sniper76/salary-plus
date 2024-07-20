package ag.act.core.aop;

import ag.act.core.holder.RequestContextHolder;
import ag.act.util.AspectParameterUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Order(2)
@Aspect
@Component
public class RequestContextHolderAspect {

    @Before("execution(* ag.act.handler.*ApiDelegateImpl.*(..))")
    public void setRequestParameterMap(JoinPoint joinPoint) {
        RequestContextHolder.setRequestParameterMap(AspectParameterUtil.findParameters(joinPoint));
    }
}
