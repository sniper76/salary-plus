package ag.act.handler;

import ag.act.api.StockSolidarityApiDelegate;
import ag.act.core.guard.HoldingStockGuard;
import ag.act.core.guard.IsActiveUserGuard;
import ag.act.core.guard.UseGuards;
import ag.act.model.SimpleStringResponse;
import ag.act.service.stock.StockSolidarityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@UseGuards({IsActiveUserGuard.class})
public class StockSolidarityApiDelegateImpl implements StockSolidarityApiDelegate {
    private final StockSolidarityService stockSolidarityService;

    @Override
    public ResponseEntity<ag.act.model.SolidarityDataResponse> getSolidarity(String stockCode) {
        return ResponseEntity.ok(stockSolidarityService.getSolidarity(stockCode));
    }

    @UseGuards({HoldingStockGuard.class})
    @Override
    public ResponseEntity<SimpleStringResponse> applySolidarityLeader(String stockCode) {
        return ResponseEntity.ok(stockSolidarityService.applySolidarityLeader(stockCode));
    }

    @Override
    public ResponseEntity<SimpleStringResponse> cancelSolidarityLeaderApplication(String stockCode) {
        return ResponseEntity.ok(stockSolidarityService.cancelSolidarityLeaderApplication(stockCode));
    }
}
