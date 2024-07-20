package ag.act.handler.election;

import ag.act.api.SolidarityLeaderElectionPostPollApiDelegate;
import ag.act.core.guard.HoldingStockGuard;
import ag.act.core.guard.IsActiveUserGuard;
import ag.act.core.guard.UseGuards;
import ag.act.facade.election.SolidarityLeaderElectionPostPollFacade;
import ag.act.model.SolidarityLeaderElectionDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@UseGuards({IsActiveUserGuard.class, HoldingStockGuard.class})
public class SolidarityLeaderElectionPostPollApiDelegateImpl implements SolidarityLeaderElectionPostPollApiDelegate {
    private final SolidarityLeaderElectionPostPollFacade solidarityLeaderElectionPostPollFacade;

    @Override
    public ResponseEntity<SolidarityLeaderElectionDetailResponse> getSolidarityLeaderElectionVoteItems(
        String stockCode, Long solidarityLeaderElectionId
    ) {
        return ResponseEntity.ok(
            solidarityLeaderElectionPostPollFacade.getSolidarityLeaderElectionVoteItems(solidarityLeaderElectionId)
        );
    }
}
