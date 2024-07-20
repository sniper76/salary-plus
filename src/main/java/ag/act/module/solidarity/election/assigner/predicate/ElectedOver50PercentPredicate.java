package ag.act.module.solidarity.election.assigner.predicate;

import ag.act.module.solidarity.election.ApplicantVote;
import org.springframework.stereotype.Component;

@Component
public class ElectedOver50PercentPredicate implements ElectedPredicate {
    @Override
    public boolean test(Long totalStockQuantity, ApplicantVote applicantVote) {
        return applicantVote.approvalQuantity() >= (totalStockQuantity / EARLY_ELECTION_DIVISOR);
    }
}
