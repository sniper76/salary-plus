package ag.act.facade.election;

import ag.act.converter.election.SolidarityLeaderElectionVoteItemsResponseConverter;
import ag.act.dto.SolidarityLeaderApplicantDto;
import ag.act.dto.election.ApplicantPollAnswerData;
import ag.act.entity.Poll;
import ag.act.entity.Post;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.model.SolidarityLeaderElectionDetailResponse;
import ag.act.service.election.StockBoardGroupPostPollService;
import ag.act.service.post.PostService;
import ag.act.service.solidarity.election.SolidarityLeaderApplicantService;
import ag.act.service.solidarity.election.SolidarityLeaderElectionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Transactional
public class SolidarityLeaderElectionPostPollFacade {
    private final StockBoardGroupPostPollService stockBoardGroupPostPollService;
    private final PostService postService;
    private final SolidarityLeaderElectionService solidarityLeaderElectionService;
    private final SolidarityLeaderElectionVoteItemsResponseConverter solidarityLeaderElectionVoteItemsResponseConverter;
    private final SolidarityLeaderApplicantService solidarityLeaderApplicantService;

    public SolidarityLeaderElectionDetailResponse getSolidarityLeaderElectionVoteItems(Long solidarityLeaderElectionId) {
        final SolidarityLeaderElection solidarityLeaderElection = getSolidarityLeaderElection(solidarityLeaderElectionId);
        final Map<Long, ApplicantPollAnswerData> applicantPollAnswerDataMap = getApplicantPollAnswerDataMap(solidarityLeaderElection);

        return solidarityLeaderElectionVoteItemsResponseConverter.convert(
            solidarityLeaderElection,
            applicantPollAnswerDataMap
        );
    }

    public Map<Long, ApplicantPollAnswerData> getApplicantPollAnswerDataMap(SolidarityLeaderElection solidarityLeaderElection) {
        final Post post = postService.getPostNotDeleted(solidarityLeaderElection.getPostId());
        final Long solidarityId = post.getBoard().getStock().getSolidarityId();
        final Poll poll = post.getFirstPoll();

        if (poll == null) {
            return Map.of();
        }

        final List<SolidarityLeaderApplicantDto> solidarityLeaderApplicants = solidarityLeaderApplicantService.getSolidarityLeaderApplicants(
            solidarityId, solidarityLeaderElection.getId()
        );

        return stockBoardGroupPostPollService.getElectionPollItemWithAnswers(
            solidarityLeaderElection, post.getId(), poll.getId(), solidarityLeaderApplicants
        );
    }

    private SolidarityLeaderElection getSolidarityLeaderElection(Long solidarityLeaderElectionId) {
        return solidarityLeaderElectionService.getSolidarityLeaderElection(solidarityLeaderElectionId);
    }
}
