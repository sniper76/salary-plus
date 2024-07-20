package ag.act.service.digitaldocument.aop;

import ag.act.util.AspectParameterUtil;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Map;

@Aspect
@Component
@RequiredArgsConstructor
public class DigitalDocumentAspect {
    private final DigitalDocumentTransactionRequestNewService digitalDocumentTransactionRequestNew;

    @Before("execution(* ag.act.service.digitaldocument.DigitalDocumentUserService.createUserDigitalDocumentWithImage(..))")
    public void deleteExistingDigitalDocumentUserWhenCreateUserDigitalDocument(JoinPoint joinPoint) {
        digitalDocumentTransactionRequestNew.deleteDigitalDocumentUser(
            getDigitalDocumentId(joinPoint)
        );
    }

    private Long getDigitalDocumentId(JoinPoint joinPoint) {
        return (Long) getParameters(joinPoint).get("digitalDocumentId");
    }

    @NotNull
    private Map<String, Object> getParameters(JoinPoint joinPoint) {
        return AspectParameterUtil.findParameters(joinPoint);
    }
}
