package ag.act.facade.election;

import ag.act.entity.Poll;
import ag.act.entity.Post;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.enums.BoardGroup;
import ag.act.model.PollAnswerDataArrayResponse;
import ag.act.model.PostPollAnswerRequest;
import ag.act.service.post.PostService;
import ag.act.service.solidarity.election.SolidarityLeaderElectionService;
import ag.act.service.stockboardgrouppost.StockBoardGroupPostPollAnswerService;
import ag.act.validator.solidarity.SolidarityLeaderElectionPostPollValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Transactional
public class SolidarityLeaderElectionPostPollAnswerFacade {
    private final StockBoardGroupPostPollAnswerService stockBoardGroupPostPollAnswerService;
    private final PostService postService;
    private final SolidarityLeaderElectionPostPollValidator solidarityLeaderElectionPostPollValidator;
    private final SolidarityLeaderElectionService solidarityLeaderElectionService;

    public PollAnswerDataArrayResponse createElectionPostPollAnswer(
        String stockCode, Long solidarityLeaderElectionId,
        PostPollAnswerRequest postPollAnswerRequest
    ) {
        BoardGroupPostPollAnswerParam result = getBoardGroupPostPollAnswer(solidarityLeaderElectionId, postPollAnswerRequest);

        return stockBoardGroupPostPollAnswerService.createBoardGroupPostPollAnswer(
            stockCode, result.boardGroup().name(), result.post().getId(), result.poll().getId(), postPollAnswerRequest
        );
    }

    public PollAnswerDataArrayResponse updateElectionPostPollAnswer(
        String stockCode, Long solidarityLeaderElectionId,
        PostPollAnswerRequest postPollAnswerRequest
    ) {
        BoardGroupPostPollAnswerParam result = getBoardGroupPostPollAnswer(solidarityLeaderElectionId, postPollAnswerRequest);

        return stockBoardGroupPostPollAnswerService.updateBoardGroupPostPollAnswer(
            stockCode, result.boardGroup().name(), result.post().getId(), result.poll().getId(), postPollAnswerRequest
        );
    }

    @NotNull
    private BoardGroupPostPollAnswerParam getBoardGroupPostPollAnswer(
        Long solidarityLeaderElectionId, PostPollAnswerRequest postPollAnswerRequest
    ) {
        final SolidarityLeaderElection solidarityLeaderElection = getSolidarityLeaderElection(solidarityLeaderElectionId);
        final Post post = postService.getPostNotDeleted(solidarityLeaderElection.getPostId());
        final Poll poll = post.getFirstPoll();
        final BoardGroup boardGroup = post.getBoard().getGroup();

        validateElectionPollItems(postPollAnswerRequest, solidarityLeaderElection);

        return new BoardGroupPostPollAnswerParam(post, poll, boardGroup);
    }

    private void validateElectionPollItems(PostPollAnswerRequest postPollAnswerRequest, SolidarityLeaderElection solidarityLeaderElection) {
        solidarityLeaderElectionPostPollValidator
            .validateElectionPollItems(solidarityLeaderElection, postPollAnswerRequest.getPollAnswer());
    }

    private SolidarityLeaderElection getSolidarityLeaderElection(Long solidarityLeaderElectionId) {
        return solidarityLeaderElectionService.getSolidarityLeaderElection(solidarityLeaderElectionId);
    }

    private record BoardGroupPostPollAnswerParam(Post post, Poll poll, BoardGroup boardGroup) {

    }
}
