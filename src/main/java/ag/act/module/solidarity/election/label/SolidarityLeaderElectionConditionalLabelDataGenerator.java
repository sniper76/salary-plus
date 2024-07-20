package ag.act.module.solidarity.election.label;

import ag.act.module.solidarity.election.ISolidarityLeaderElection;

public interface SolidarityLeaderElectionConditionalLabelDataGenerator extends ISolidarityLeaderElection.ElectionCondition {

    default String getTextColor(long baseStockQuantity, long totalApprovalVoteStockQuantity) {
        return isSatisfiedCondition(baseStockQuantity, totalApprovalVoteStockQuantity)
            ? COLOR_FOR_SATISFIED_CONDITION
            : COLOR_FOR_UNSATISFIED_CONDITION;
    }

    private boolean isSatisfiedCondition(long baseStockQuantity, long totalApprovalVoteStockQuantity) {
        return baseStockQuantity <= totalApprovalVoteStockQuantity;
    }

    default long getResolutionConditionStockQuantity(long stockQuantity) {
        return stockQuantity / ONE_FOURTH_DIVISOR;
    }

    default long getFinishedEarlyStockQuantity(long stockQuantity) {
        return stockQuantity / EARLY_ELECTION_DIVISOR;
    }
}
