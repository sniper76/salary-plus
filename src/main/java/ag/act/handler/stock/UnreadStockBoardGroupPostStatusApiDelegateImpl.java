package ag.act.handler.stock;

import ag.act.api.UnreadStockBoardGroupPostStatusApiDelegate;
import ag.act.core.guard.IsActiveSolidarityGuard;
import ag.act.core.guard.IsActiveUserGuard;
import ag.act.core.guard.PinNumberVerificationGuard;
import ag.act.core.guard.UseGuards;
import ag.act.model.UnreadStockBoardGroupPostStatusDataResponse;
import ag.act.service.post.UnreadPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@UseGuards({PinNumberVerificationGuard.class, IsActiveUserGuard.class, IsActiveSolidarityGuard.class})
public class UnreadStockBoardGroupPostStatusApiDelegateImpl implements UnreadStockBoardGroupPostStatusApiDelegate {
    private final UnreadPostService unreadPostService;

    @Override
    public ResponseEntity<UnreadStockBoardGroupPostStatusDataResponse> getUnreadStockBoardGroupPostStatus(String stockCode) {
        return ResponseEntity.ok(
            new UnreadStockBoardGroupPostStatusDataResponse().unreadStockBoardGroupPostStatus(
                unreadPostService.getUnreadStockBoardGroupPostStatusResponse(stockCode)
            )
        );
    }
}
