package ag.act.converter.election.process.label;

import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionStatus;
import ag.act.model.LeaderElectionProcessLabelResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class NullProcessLabelResponseConverter implements LeaderElectionProcessLabelResponseConverter {

    @Override
    public boolean supports(SolidarityLeaderElectionStatus status) {
        return false;
    }

    @Override
    public LeaderElectionProcessLabelResponse convert(SolidarityLeaderElection leaderElection) {
        return null;
    }
}
