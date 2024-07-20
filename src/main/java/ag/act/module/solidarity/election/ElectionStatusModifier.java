package ag.act.module.solidarity.election;

import ag.act.dto.solidarity.election.SolidarityLeaderElectionStatusGroup;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Transactional
@Component
public class ElectionStatusModifier {
    private final SolidarityLeaderElectionSlackNotifier solidarityLeaderElectionSlackNotifier;
    private final SolidarityLeaderElectionPushRegister solidarityLeaderElectionPushRegister;

    public void changeToVoteStatusIfApplicable(SolidarityLeaderElection solidarityLeaderElection) {

        if (solidarityLeaderElection.getCandidateCount() <= 0) {
            return;
        }

        if (solidarityLeaderElection.isFromCandidateRegistrationToVotePeriod()) {
            solidarityLeaderElection.setElectionStatusGroup(SolidarityLeaderElectionStatusGroup.VOTE_STATUS_GROUP);
            notifySlackIfApplicable(solidarityLeaderElection);
            registerPushIfApplicable(solidarityLeaderElection);
        }
    }

    private void registerPushIfApplicable(SolidarityLeaderElection solidarityLeaderElection) {
        solidarityLeaderElectionPushRegister.register(solidarityLeaderElection);
    }

    private void notifySlackIfApplicable(SolidarityLeaderElection solidarityLeaderElection) {
        solidarityLeaderElectionSlackNotifier.notifyIfApplicable(solidarityLeaderElection);
    }
}
