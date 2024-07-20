package ag.act.core.guard;


import ag.act.configuration.security.ActUserProvider;
import ag.act.core.configuration.GlobalBoardManager;
import ag.act.core.guard.holdingstock.UserHoldingStockAndDigitalDocumentValidator;
import ag.act.entity.User;
import ag.act.exception.ActRuntimeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@RequiredArgsConstructor
@Component
public class ContentCreateGuard implements ActGuard {
    private final UserHoldingStockAndDigitalDocumentValidator userHoldingStockAndDigitalDocumentValidator;
    private final GlobalBoardManager globalBoardManager;

    public void canActivate(Map<String, Object> parameterMap) throws ActRuntimeException {
        final User user = ActUserProvider.getNoneNull();
        final String stockCode = (String) parameterMap.get("stockCode");
        if (globalBoardManager.isGlobalStockCode(stockCode)) {
            return;
        }
        if (user.isAdmin()) {
            return;
        }
        userHoldingStockAndDigitalDocumentValidator.validateUserHoldingStock(user, stockCode);
    }
}
