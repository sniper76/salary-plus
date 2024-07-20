package ag.act.core.guard;

import ag.act.core.annotation.AnnotationOrderSorter;
import ag.act.core.guard.holdingstock.HoldingStockGuardParameter;
import ag.act.core.guard.holdingstock.HoldingStockGuardValidator;
import ag.act.exception.ActRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * You can use this Guard on the method above.
 * <p>
 * Usage:
 * <pre>{@code
 * @UseGuards({ HoldingStockGuard.class, PinNumberVerificationGuard.class })
 * public ResponseEntity<CheckEmailResponse> checkEmail(CheckEmailRequest checkEmailRequest) {
 * // method body
 * }
 * }</pre>
 */
@Component
public class HoldingStockGuard implements ActGuard {
    private final List<HoldingStockGuardValidator> holdingStockGuardValidators;

    @Autowired
    public HoldingStockGuard(
        AnnotationOrderSorter annotationOrderSorter,
        List<HoldingStockGuardValidator> holdingStockGuardValidators
    ) {
        this.holdingStockGuardValidators = annotationOrderSorter.sort(holdingStockGuardValidators);
    }

    public void canActivate(Map<String, Object> parameterMap) throws ActRuntimeException {
        isAnyValidationSucceeded(parameterMap);
    }

    @SuppressWarnings("UnusedReturnValue")
    private boolean isAnyValidationSucceeded(Map<String, Object> parameterMap) {
        var holdingStockGuardParameter = new HoldingStockGuardParameter(
            (String) parameterMap.get("stockCode"),
            (Long) parameterMap.get("postId"),
            (Long) parameterMap.get("digitalDocumentId")
        );

        return holdingStockGuardValidators.stream().anyMatch(validator -> validator.validate(holdingStockGuardParameter));
    }
}
