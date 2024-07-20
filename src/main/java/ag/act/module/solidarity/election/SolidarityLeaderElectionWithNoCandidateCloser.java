package ag.act.module.solidarity.election;

import ag.act.dto.solidarity.election.SolidarityLeaderElectionStatusGroup;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Transactional
@Component
public class SolidarityLeaderElectionWithNoCandidateCloser {

    private static final int ZERO_CANDIDATE_COUNT = 0;

    public void closeIfApplicable(SolidarityLeaderElection solidarityLeaderElection) {
        if (solidarityLeaderElection.isFinishedElection()) {
            return;
        }

        if (canCloseElectionWithNoCandidate(solidarityLeaderElection)) {
            solidarityLeaderElection.setElectionStatusGroup(SolidarityLeaderElectionStatusGroup.FINISHED_BY_NO_CANDIDATE_STATUS_GROUP);
        }
    }

    private boolean canCloseElectionWithNoCandidate(SolidarityLeaderElection solidarityLeaderElection) {
        return solidarityLeaderElection.getCandidateCount() <= ZERO_CANDIDATE_COUNT
            && solidarityLeaderElection.isVotePeriod()
            && solidarityLeaderElection.isActiveElection();
    }
}
