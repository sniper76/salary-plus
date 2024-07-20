package ag.act.core.guard.holdingstock;

import ag.act.configuration.security.ActUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(20)
@RequiredArgsConstructor
@Component
public class IsAdminGuardValidator implements HoldingStockGuardValidator {

    @Override
    public boolean validate(HoldingStockGuardParameter parameter) {
        return ActUserProvider.getNoneNull().isAdmin();
    }
}
