package ag.act.service.solidarity;

import ag.act.converter.dashboard.DashboardResponseConverter;
import ag.act.dto.TopTwoMostRecentDailySummaryDto;
import ag.act.entity.SolidarityDailySummary;
import ag.act.model.DashboardResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Transactional
@Service
public class SolidarityDashboardService {
    private final SolidarityDailySummaryService solidarityDailySummaryService;
    private final DashboardResponseConverter dashboardResponseConverter;

    public DashboardResponse getDashboard(String stockCode) {
        final TopTwoMostRecentDailySummaryDto topTwoMostRecentDailySummary = getTopTwoMostRecentDailySummaries(stockCode);

        if (topTwoMostRecentDailySummary.getMostRecentSummary() == null) {
            return generateZeroFilledDashboardResponse();
        } else {
            return generateFilledDashboardResponse(topTwoMostRecentDailySummary);
        }
    }

    private TopTwoMostRecentDailySummaryDto getTopTwoMostRecentDailySummaries(String stockCode) {
        return solidarityDailySummaryService.getTopTwoMostRecentDailySummaries(stockCode);
    }

    private DashboardResponse generateZeroFilledDashboardResponse() {
        return dashboardResponseConverter.convert(
            solidarityDailySummaryService.generateZeroFilledDashboard(),
            LocalDateTime.now()
        );
    }

    private DashboardResponse generateFilledDashboardResponse(TopTwoMostRecentDailySummaryDto topTwoMostRecentDailySummary) {
        if (topTwoMostRecentDailySummary.getSecondMostRecentSummary() == null) {
            return generatedMostRecentFilledDashboardResponse(topTwoMostRecentDailySummary.getMostRecentSummary());
        } else {
            return generateVariationFilledDashboardResponse(topTwoMostRecentDailySummary);
        }
    }

    private DashboardResponse generatedMostRecentFilledDashboardResponse(SolidarityDailySummary mostRecentSummary) {
        return dashboardResponseConverter.convert(
            solidarityDailySummaryService.generateDashboardItemsBySingleSolidarityDailySummary(mostRecentSummary),
            mostRecentSummary.getUpdatedAt()
        );
    }

    private DashboardResponse generateVariationFilledDashboardResponse(
        TopTwoMostRecentDailySummaryDto topTwoMostRecentDailySummary
    ) {
        return dashboardResponseConverter.convert(
            solidarityDailySummaryService.generateDashboardItemsWithVariation(topTwoMostRecentDailySummary),
            topTwoMostRecentDailySummary.getMostRecentSummary().getUpdatedAt()
        );
    }
}
