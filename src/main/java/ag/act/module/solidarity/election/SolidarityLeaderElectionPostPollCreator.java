package ag.act.module.solidarity.election;

import ag.act.entity.Post;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.service.stockboardgrouppost.SolidarityLeaderElectionPostPollService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Transactional
@Component
public class SolidarityLeaderElectionPostPollCreator {
    private final SolidarityLeaderElectionPostPollService solidarityLeaderElectionPostPollService;
    private final SolidarityLeaderElectionPushRegister solidarityLeaderElectionPushRegister;

    public void createIfApplicable(SolidarityLeaderElection solidarityLeaderElection) {
        if (solidarityLeaderElection.getPostId() == null && solidarityLeaderElection.isCandidateRegistrationPeriod()) {
            final Post post = solidarityLeaderElectionPostPollService.createBoardGroupPost(solidarityLeaderElection);
            solidarityLeaderElection.setPostId(post.getId());
            registerPushIfApplicable(solidarityLeaderElection);
        }

        createPollIfApplicable(solidarityLeaderElection);
    }

    private void createPollIfApplicable(SolidarityLeaderElection solidarityLeaderElection) {
        if (solidarityLeaderElection != null && solidarityLeaderElection.isVotePeriod()) {
            solidarityLeaderElectionPostPollService.createPolls(solidarityLeaderElection);
        }
    }

    private void registerPushIfApplicable(SolidarityLeaderElection solidarityLeaderElection) {
        solidarityLeaderElectionPushRegister.register(solidarityLeaderElection);
    }
}
