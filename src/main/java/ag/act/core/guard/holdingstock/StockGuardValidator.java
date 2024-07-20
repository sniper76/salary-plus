package ag.act.core.guard.holdingstock;

import ag.act.configuration.security.ActUserProvider;
import ag.act.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(50)
@RequiredArgsConstructor
@Component
public class StockGuardValidator implements HoldingStockGuardValidator {
    private final UserHoldingStockAndDigitalDocumentValidator userHoldingStockAndDigitalDocumentValidator;

    @Override
    public boolean validate(HoldingStockGuardParameter parameter) {
        final String stockCode = parameter.stockCode();
        final User user = ActUserProvider.getNoneNull();

        if (stockCode != null) {
            return userHoldingStockAndDigitalDocumentValidator.validateUserHoldingStock(user, stockCode);
        }

        return false;
    }
}
