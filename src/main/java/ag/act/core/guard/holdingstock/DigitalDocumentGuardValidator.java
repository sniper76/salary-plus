package ag.act.core.guard.holdingstock;

import ag.act.configuration.security.ActUserProvider;
import ag.act.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(60)
@RequiredArgsConstructor
@Component
public class DigitalDocumentGuardValidator implements HoldingStockGuardValidator {
    private final UserHoldingStockAndDigitalDocumentValidator userHoldingStockAndDigitalDocumentValidator;

    @Override
    public boolean validate(HoldingStockGuardParameter parameter) {
        final String stockCode = parameter.stockCode();
        final Long digitalDocumentId = parameter.digitalDocumentId();
        final User user = ActUserProvider.getNoneNull();

        if (stockCode == null && digitalDocumentId != null) {
            return userHoldingStockAndDigitalDocumentValidator.validateUserHoldingStockByDigitalDocument(user, digitalDocumentId);
        }

        return false;
    }
}
