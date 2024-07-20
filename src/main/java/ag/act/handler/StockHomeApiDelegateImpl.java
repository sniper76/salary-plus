package ag.act.handler;

import ag.act.api.StockHomeApiDelegate;
import ag.act.core.guard.IsActiveSolidarityGuard;
import ag.act.core.guard.IsActiveUserGuard;
import ag.act.core.guard.PinNumberVerificationGuard;
import ag.act.core.guard.UseGuards;
import ag.act.facade.stock.home.StockHomeFacade;
import ag.act.model.StockHomeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@UseGuards({PinNumberVerificationGuard.class, IsActiveUserGuard.class, IsActiveSolidarityGuard.class})
public class StockHomeApiDelegateImpl implements StockHomeApiDelegate {
    private final StockHomeFacade stockHomeFacade;

    @Override
    public ResponseEntity<StockHomeResponse> getStockHome(String stockCode) {
        return ResponseEntity.ok(stockHomeFacade.getStockHome(stockCode));
    }
}
