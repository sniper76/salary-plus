package ag.act.module.solidarity.election;

import ag.act.entity.SolidarityDailySummary;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.service.solidarity.SolidarityDailySummaryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Transactional
@Component
public class TotalStockQuantityModifier {

    private static final int ZERO_STOCK_QUANTITY = 0;
    private final SolidarityDailySummaryService solidarityDailySummaryService;

    public void setIfApplicable(SolidarityLeaderElection solidarityLeaderElection) {
        if (isTotalStockQuantityUpdateNeeded(solidarityLeaderElection)) {
            final Long solidarityStockQuantity = getSolidarityStockQuantity(solidarityLeaderElection);

            solidarityLeaderElection.setTotalStockQuantity(solidarityStockQuantity);
        }
    }

    private Long getSolidarityStockQuantity(SolidarityLeaderElection solidarityLeaderElection) {
        final SolidarityDailySummary mostRecentSolidarityDailySummary =
            solidarityDailySummaryService.getMostRecentSolidarityDailySummary(solidarityLeaderElection.getStockCode());

        return mostRecentSolidarityDailySummary.getStockQuantity();
    }

    private boolean isTotalStockQuantityUpdateNeeded(SolidarityLeaderElection solidarityLeaderElection) {
        return isEmptyTotalStockQuantity(solidarityLeaderElection) && solidarityLeaderElection.isVotePeriod();
    }

    private boolean isEmptyTotalStockQuantity(SolidarityLeaderElection solidarityLeaderElection) {
        return solidarityLeaderElection.getTotalStockQuantity() <= ZERO_STOCK_QUANTITY;
    }
}
