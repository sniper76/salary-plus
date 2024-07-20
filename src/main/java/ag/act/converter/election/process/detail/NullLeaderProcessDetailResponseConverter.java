package ag.act.converter.election.process.detail;

import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionStatus;
import ag.act.model.LeaderElectionProcessDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class NullLeaderProcessDetailResponseConverter extends LeaderElectionProcessDetailResponseConverter {

    @Override
    public boolean supports(SolidarityLeaderElectionStatus status) {
        return false;
    }

    @Override
    public LeaderElectionProcessDetailResponse convert(SolidarityLeaderElection leaderElection) {
        return null;
    }
}
