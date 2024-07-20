package ag.act.converter.election.process.label;

import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionStatus;
import ag.act.model.LeaderElectionProcessLabelResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class FinishProcessLabelResponseConverter implements LeaderElectionProcessLabelResponseConverter {

    @Override
    public boolean supports(SolidarityLeaderElectionStatus status) {
        return status.isFinishedStatus();
    }

    @Override
    public LeaderElectionProcessLabelResponse convert(SolidarityLeaderElection leaderElection) {
        return Optional.ofNullable(leaderElection.getVoteClosingDateTime())
            .map(it -> toResponse(leaderElection.getElectionFinishedLabel(), COLOR_FOR_COMPLETE))
            .orElse(null);
    }
}
