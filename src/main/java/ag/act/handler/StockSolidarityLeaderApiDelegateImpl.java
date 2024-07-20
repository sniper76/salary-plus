package ag.act.handler;

import ag.act.api.StockSolidarityLeaderApiDelegate;
import ag.act.core.guard.HoldingStockGuard;
import ag.act.core.guard.UseGuards;
import ag.act.model.UpdateSolidarityLeaderMessageRequest;
import ag.act.service.solidarity.election.SolidarityLeaderService;
import ag.act.util.SimpleStringResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockSolidarityLeaderApiDelegateImpl implements StockSolidarityLeaderApiDelegate {

    private final SolidarityLeaderService solidarityLeaderService;

    @Override
    @UseGuards(HoldingStockGuard.class)
    public ResponseEntity<ag.act.model.SimpleStringResponse> updateSolidarityLeaderMessage(
        String stockCode,
        Long solidarityId,
        UpdateSolidarityLeaderMessageRequest updateSolidarityLeaderMessageRequest
    ) {
        solidarityLeaderService.updateSolidarityLeaderMessage(solidarityId, updateSolidarityLeaderMessageRequest);
        return SimpleStringResponseUtil.okResponse();
    }
}
