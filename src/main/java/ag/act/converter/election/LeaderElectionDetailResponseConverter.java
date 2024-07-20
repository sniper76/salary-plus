package ag.act.converter.election;

import ag.act.converter.DateTimeConverter;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.model.LeaderElectionDetailResponse;
import ag.act.model.LeaderElectionProcessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class LeaderElectionDetailResponseConverter {
    private final LeaderElectionProcessResponseConverter leaderElectionProcessResponseConverter;
    private final LeaderElectionWinnerResponseConverter leaderElectionWinnerResponseConverter;

    public LeaderElectionDetailResponse convert(SolidarityLeaderElection solidarityLeaderElection) {
        if (isNotDisplayable(solidarityLeaderElection)) {
            return null;
        }

        return new LeaderElectionDetailResponse()
            .solidarityLeaderElectionId(solidarityLeaderElection.getId())
            .electionStatus(solidarityLeaderElection.getElectionStatus().name())
            .winner(leaderElectionWinnerResponseConverter.convert(solidarityLeaderElection))
            .startDate(getStartDate(solidarityLeaderElection))
            .endDate(getEndDate(solidarityLeaderElection))
            .electionProcesses(getElectionProcesses(solidarityLeaderElection));
    }

    private boolean isNotDisplayable(SolidarityLeaderElection solidarityLeaderElection) {
        return solidarityLeaderElection.isPendingElection()
            || solidarityLeaderElection.isFinishedNoCandidateElection();
    }

    private Instant getStartDate(SolidarityLeaderElection solidarityLeaderElection) {
        return DateTimeConverter.convert(solidarityLeaderElection.getCandidateRegistrationStartDateTime());
    }

    private Instant getEndDate(SolidarityLeaderElection solidarityLeaderElection) {
        return DateTimeConverter.convert(
            Optional.ofNullable(solidarityLeaderElection.getVoteClosingDateTime())
                .orElse(solidarityLeaderElection.getVoteEndDateTime().minusSeconds(1))
        );
    }

    private List<LeaderElectionProcessResponse> getElectionProcesses(SolidarityLeaderElection solidarityLeaderElection) {
        return leaderElectionProcessResponseConverter.convert(solidarityLeaderElection);
    }
}
