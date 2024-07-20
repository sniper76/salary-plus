package ag.act.module.solidarity.election;

import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.model.Status;
import ag.act.service.post.PostService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Transactional
@Component
public class SolidarityLeaderElectionPostStatusUpdater {

    private final PostService postService;

    public void updateIfApplicable(SolidarityLeaderElection solidarityLeaderElection) {
        if (solidarityLeaderElection.isFinishedNoCandidateElection()) {
            deletePost(solidarityLeaderElection);
        }
    }

    private void deletePost(SolidarityLeaderElection solidarityLeaderElection) {
        postService.deletePost(
            solidarityLeaderElection.getPostId(),
            Status.DELETED,
            LocalDateTime.now()
        );
    }
}
