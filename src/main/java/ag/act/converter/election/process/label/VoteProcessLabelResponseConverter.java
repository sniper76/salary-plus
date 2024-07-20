package ag.act.converter.election.process.label;

import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionStatus;
import ag.act.model.LeaderElectionProcessLabelResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class VoteProcessLabelResponseConverter implements LeaderElectionProcessLabelResponseConverter {

    @Override
    public boolean supports(SolidarityLeaderElectionStatus status) {
        return status.isVotePeriodStatus();
    }

    @Override
    public LeaderElectionProcessLabelResponse convert(SolidarityLeaderElection leaderElection) {
        return getProcessStepLabelResponse(
            leaderElection.getVoteEndDateTime(),
            leaderElection.isVotePeriod(),
            leaderElection.isFinishedElection()
        );
    }
}
