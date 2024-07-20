package ag.act.core.guard.holdingstock;

import ag.act.core.configuration.GlobalBoardManager;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(10)
@RequiredArgsConstructor
@Component
public class GlobalStockGuardValidator implements HoldingStockGuardValidator {
    private final GlobalBoardManager globalBoardManager;

    @Override
    public boolean validate(HoldingStockGuardParameter parameter) {
        final String stockCode = parameter.stockCode();

        return globalBoardManager.isGlobalStockCode(stockCode);
    }
}
