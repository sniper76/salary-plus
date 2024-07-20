package ag.act.module.solidarity.election.assigner.predicate;

import ag.act.module.solidarity.election.ApplicantVote;
import org.springframework.stereotype.Component;

@Component
public class ElectedOver25PercentPredicate implements ElectedPredicate {
    @Override
    public boolean test(Long totalStockQuantity, ApplicantVote applicantVote) {
        return isOver25PercentApproval(totalStockQuantity, applicantVote.approvalQuantity())
            && isApprovalIsMoreThanRejection(applicantVote);
    }

    private boolean isApprovalIsMoreThanRejection(ApplicantVote applicantVote) {
        return applicantVote.approvalQuantity() > applicantVote.rejectionQuantity();
    }

    private boolean isOver25PercentApproval(long totalStockQuantity, Long approvalQuantity) {
        return approvalQuantity >= (totalStockQuantity / ONE_FOURTH_DIVISOR);
    }
}
