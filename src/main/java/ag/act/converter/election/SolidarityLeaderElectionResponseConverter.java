package ag.act.converter.election;

import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.facade.election.SolidarityLeaderElectionPostPollFacade;
import ag.act.facade.solidarity.leader.SolidarityLeaderElectionFacade;
import ag.act.model.LeaderElectionWinnerResponse;
import ag.act.model.SolidarityLeaderApplicantResponse;
import ag.act.model.SolidarityLeaderElectionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
@SuppressWarnings("MemberName")
public class SolidarityLeaderElectionResponseConverter {
    private final SolidarityLeaderElectionPostPollFacade solidarityLeaderElectionPostPollFacade;
    private final SolidarityLeaderElectionFacade solidarityLeaderElectionFacade;
    private final LeaderElectionWinnerResponseConverter leaderElectionWinnerResponseConverter;

    public SolidarityLeaderElectionResponse convert(
        String stockCode,
        SolidarityLeaderElection solidarityLeaderElection
    ) {
        final Long solidarityLeaderElectionId = solidarityLeaderElection.getId();

        return new SolidarityLeaderElectionResponse()
            .winner(getWinnerResponse(solidarityLeaderElection))
            .applicants(getSolidarityLeaderApplicantResponses(stockCode, solidarityLeaderElectionId))
            .electionDetail(solidarityLeaderElectionPostPollFacade.getSolidarityLeaderElectionVoteItems(solidarityLeaderElectionId));
    }

    private LeaderElectionWinnerResponse getWinnerResponse(SolidarityLeaderElection solidarityLeaderElection) {
        final LeaderElectionWinnerResponse winnerResponse = leaderElectionWinnerResponseConverter.convert(solidarityLeaderElection);
        if (winnerResponse == null) {
            return null;
        }
        winnerResponse.setProfileImageUrl(null);
        return winnerResponse;
    }

    private List<SolidarityLeaderApplicantResponse> getSolidarityLeaderApplicantResponses(String stockCode, Long solidarityLeaderElectionId) {
        return solidarityLeaderElectionFacade.getSolidarityLeaderApplicants(stockCode, solidarityLeaderElectionId)
            .getData()
            .stream()
            .peek(response -> response.setCommentsForStockHolder(null))
            .toList();
    }
}
