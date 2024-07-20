package ag.act.handler.stockboardgrouppost;

import ag.act.api.StockBoardGroupPostPollAnswerApiDelegate;
import ag.act.core.guard.IsActiveUserGuard;
import ag.act.core.guard.UseGuards;
import ag.act.model.PollAnswerDataArrayResponse;
import ag.act.model.PostPollAnswerRequest;
import ag.act.service.stockboardgrouppost.StockBoardGroupPostPollAnswerService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@UseGuards({IsActiveUserGuard.class})
public class StockBoardGroupPostPollAnswerApiDelegateImpl implements StockBoardGroupPostPollAnswerApiDelegate {
    private final StockBoardGroupPostPollAnswerService stockBoardGroupPostPollAnswerService;

    public StockBoardGroupPostPollAnswerApiDelegateImpl(
        StockBoardGroupPostPollAnswerService stockBoardGroupPostPollAnswerService
    ) {
        this.stockBoardGroupPostPollAnswerService = stockBoardGroupPostPollAnswerService;
    }

    @Override
    public ResponseEntity<PollAnswerDataArrayResponse> createBoardGroupPostPollAnswer(
        String stockCode, String boardGroupName, Long postId, Long pollId, PostPollAnswerRequest postPollAnswerRequest
    ) {
        return ResponseEntity.ok(
            stockBoardGroupPostPollAnswerService.createBoardGroupPostPollAnswer(
                stockCode, boardGroupName, postId, pollId, postPollAnswerRequest
            )
        );
    }

    @Override
    public ResponseEntity<PollAnswerDataArrayResponse> updateBoardGroupPostPollAnswer(
        String stockCode, String boardGroupName, Long postId, Long pollId, PostPollAnswerRequest postPollAnswerRequest
    ) {
        return ResponseEntity.ok(
            stockBoardGroupPostPollAnswerService.updateBoardGroupPostPollAnswer(
                stockCode, boardGroupName, postId, pollId, postPollAnswerRequest
            )
        );
    }
}
