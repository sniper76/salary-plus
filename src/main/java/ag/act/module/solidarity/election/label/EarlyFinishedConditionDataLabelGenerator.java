package ag.act.module.solidarity.election.label;

import ag.act.dto.election.SolidarityLeaderElectionApplicantDataLabel;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;

@Component
public class EarlyFinishedConditionDataLabelGenerator implements SolidarityLeaderElectionConditionalLabelDataGenerator {

    public SolidarityLeaderElectionApplicantDataLabel generate(long totalStockQuantity, long totalApprovalVoteStockQuantity) {
        final long finishedEarlyStockQuantity = getFinishedEarlyStockQuantity(totalStockQuantity);

        return new SolidarityLeaderElectionApplicantDataLabel(
            finishedEarlyStockQuantity,
            EARLY_FINISHED_LABEL,
            UNIT,
            getValueText(finishedEarlyStockQuantity),
            getTextColor(finishedEarlyStockQuantity, totalApprovalVoteStockQuantity)
        );
    }

    private String getValueText(long finishedEarlyStockQuantity) {
        return "%s %s%s".formatted(
            EARLY_FINISHED_LABEL,
            NumberFormat.getInstance().format(finishedEarlyStockQuantity),
            UNIT
        );
    }
}
