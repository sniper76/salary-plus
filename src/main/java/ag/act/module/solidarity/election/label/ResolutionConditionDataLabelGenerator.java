package ag.act.module.solidarity.election.label;

import ag.act.dto.election.SolidarityLeaderElectionApplicantDataLabel;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;

@Component
public class ResolutionConditionDataLabelGenerator implements SolidarityLeaderElectionConditionalLabelDataGenerator {

    public SolidarityLeaderElectionApplicantDataLabel generate(long totalStockQuantity, long totalApprovalVoteStockQuantity) {
        final long resolutionConditionStockQuantity = getResolutionConditionStockQuantity(totalStockQuantity);

        return new SolidarityLeaderElectionApplicantDataLabel(
            REQUIRED_STOCK_QUANTITY_RATIO,
            resolutionConditionStockQuantity,
            RESOLUTION_CONDITION_LABEL,
            UNIT,
            getValueText(resolutionConditionStockQuantity),
            getTextColor(resolutionConditionStockQuantity, totalApprovalVoteStockQuantity)
        );
    }

    private String getValueText(long resolutionConditionStockQuantity) {
        return "%s %s%s".formatted(
            RESOLUTION_CONDITION_LABEL,
            NumberFormat.getInstance().format(resolutionConditionStockQuantity),
            UNIT
        );
    }
}
