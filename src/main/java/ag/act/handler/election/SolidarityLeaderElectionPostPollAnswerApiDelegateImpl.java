package ag.act.handler.election;

import ag.act.api.SolidarityLeaderElectionPostPollAnswerApiDelegate;
import ag.act.core.guard.HoldingStockGuard;
import ag.act.core.guard.IsActiveUserGuard;
import ag.act.core.guard.UseGuards;
import ag.act.facade.election.SolidarityLeaderElectionPostPollAnswerFacade;
import ag.act.model.PollAnswerDataArrayResponse;
import ag.act.model.PostPollAnswerRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@UseGuards({IsActiveUserGuard.class, HoldingStockGuard.class})
public class SolidarityLeaderElectionPostPollAnswerApiDelegateImpl implements SolidarityLeaderElectionPostPollAnswerApiDelegate {
    private final SolidarityLeaderElectionPostPollAnswerFacade solidarityLeaderElectionPostPollAnswerFacade;

    @Override
    public ResponseEntity<PollAnswerDataArrayResponse> createElectionPostPollAnswer(
        String stockCode, Long solidarityLeaderElectionId, PostPollAnswerRequest postPollAnswerRequest
    ) {
        return ResponseEntity.ok(
            solidarityLeaderElectionPostPollAnswerFacade.createElectionPostPollAnswer(
                stockCode, solidarityLeaderElectionId, postPollAnswerRequest
            )
        );
    }

    @Override
    public ResponseEntity<PollAnswerDataArrayResponse> updateElectionPostPollAnswer(
        String stockCode, Long solidarityLeaderElectionId, PostPollAnswerRequest postPollAnswerRequest
    ) {
        return ResponseEntity.ok(
            solidarityLeaderElectionPostPollAnswerFacade.updateElectionPostPollAnswer(
                stockCode, solidarityLeaderElectionId, postPollAnswerRequest
            )
        );
    }
}
