package ag.act.module.solidarity.election;

import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.service.poll.PollAnswerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Transactional
@Component
public class TotalVoteStockQuantityModifier {
    private final PollAnswerService pollAnswerService;

    public void set(SolidarityLeaderElection solidarityLeaderElection) {
        final Long postId = solidarityLeaderElection.getPostId();
        final long totalVoteStockQuantity = pollAnswerService.getTotalVoteStockQuantityByPostId(postId);

        solidarityLeaderElection.setTotalVoteStockQuantity(totalVoteStockQuantity);
    }
}
