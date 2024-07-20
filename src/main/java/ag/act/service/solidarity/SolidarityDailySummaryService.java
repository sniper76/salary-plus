package ag.act.service.solidarity;

import ag.act.converter.dashboard.DashboardItemResponseConverter;
import ag.act.dto.TopTwoMostRecentDailySummaryDto;
import ag.act.entity.CalculateStake;
import ag.act.entity.Solidarity;
import ag.act.entity.SolidarityDailySummary;
import ag.act.entity.Stock;
import ag.act.entity.dashboard.item.DashboardItem;
import ag.act.entity.dashboard.item.StockMarketValueDashboardItem;
import ag.act.entity.dashboard.item.StockMemeberCountDashboardItem;
import ag.act.entity.dashboard.item.StockQuantityDashboardItem;
import ag.act.entity.dashboard.item.StockStakeDashboardItem;
import ag.act.exception.NotFoundException;
import ag.act.repository.SolidarityDailySummaryRepository;
import ag.act.service.user.UserHoldingStockService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SolidarityDailySummaryService {
    private final SolidarityDailySummaryRepository solidarityDailySummaryRepository;
    private final DashboardItemResponseConverter dashboardItemResponseConverter;
    private final UserHoldingStockService userHoldingStockService;
    private final SolidarityService solidarityService;

    public SolidarityDailySummaryService(
        SolidarityDailySummaryRepository solidarityDailySummaryRepository,
        DashboardItemResponseConverter dashboardItemResponseConverter,
        UserHoldingStockService userHoldingStockService,
        SolidarityService solidarityService
    ) {
        this.solidarityDailySummaryRepository = solidarityDailySummaryRepository;
        this.dashboardItemResponseConverter = dashboardItemResponseConverter;
        this.userHoldingStockService = userHoldingStockService;
        this.solidarityService = solidarityService;
    }

    public TopTwoMostRecentDailySummaryDto getTopTwoMostRecentDailySummaries(String stockCode) {
        return getTopTwoMostRecentDailySummaries(solidarityService.getSolidarityByStockCode(stockCode));
    }

    public TopTwoMostRecentDailySummaryDto getTopTwoMostRecentDailySummaries(Solidarity solidarity) {
        return TopTwoMostRecentDailySummaryDto.builder()
            .mostRecentSummary(solidarity.getMostRecentDailySummary())
            .secondMostRecentSummary(solidarity.getSecondMostRecentDailySummary())
            .build();
    }

    public List<ag.act.model.DashboardItemResponse> generateZeroFilledDashboard() {
        final List<DashboardItem> dashboardItems = List.of(
            new StockQuantityDashboardItem(0L),
            new StockStakeDashboardItem(0.00),
            new StockMarketValueDashboardItem(0L),
            new StockMemeberCountDashboardItem(0)
        );

        return dashboardItems.stream()
            .map(dashboardItemResponseConverter::convert)
            .toList();
    }

    public List<ag.act.model.DashboardItemResponse> generateDashboardItemsBySingleSolidarityDailySummary(
        SolidarityDailySummary solidarityDailySummary
    ) {
        final List<DashboardItem> dashboardItems = List.of(
            new StockQuantityDashboardItem(solidarityDailySummary.getStockQuantity()),
            new StockStakeDashboardItem(solidarityDailySummary.getStake()),
            new StockMarketValueDashboardItem(solidarityDailySummary.getMarketValue()),
            new StockMemeberCountDashboardItem(solidarityDailySummary.getMemberCount())
        );

        return dashboardItems.stream()
            .map(dashboardItemResponseConverter::convert)
            .toList();
    }

    public List<ag.act.model.DashboardItemResponse> generateDashboardItemsWithVariation(
        TopTwoMostRecentDailySummaryDto topTwoMostRecentDailySummary
    ) {
        final SolidarityDailySummary mostRecentSummary = topTwoMostRecentDailySummary.getMostRecentSummary();
        final SolidarityDailySummary secondMostRecentSummary = topTwoMostRecentDailySummary.getSecondMostRecentSummary();

        final List<DashboardItem> dashboardItems = List.of(
            new StockQuantityDashboardItem(mostRecentSummary.getStockQuantity(), secondMostRecentSummary.getStockQuantity()),
            new StockStakeDashboardItem(mostRecentSummary.getStake(), secondMostRecentSummary.getStake()),
            new StockMarketValueDashboardItem(mostRecentSummary.getMarketValue(), secondMostRecentSummary.getMarketValue()),
            new StockMemeberCountDashboardItem(mostRecentSummary.getMemberCount(), secondMostRecentSummary.getMemberCount())
        );

        return dashboardItems.stream()
            .map(dashboardItemResponseConverter::convert)
            .toList();
    }

    public SolidarityDailySummary createSolidarityDailySummary(Solidarity solidarity) {
        final Stock stock = solidarity.getStock();
        final Long totalIssuedQuantity = stock.getTotalIssuedQuantity();
        final Long memberTotalStockQuantity = userHoldingStockService.sumStockQuantityOfSolidarityMembers(solidarity);

        SolidarityDailySummary solidarityDailySummary = SolidarityDailySummary.builder()
            .stockQuantity(memberTotalStockQuantity)
            .stake(calculateStake(
                CalculateStake.builder()
                    .totalIssuedStockQuantity(totalIssuedQuantity)
                    .totalSolidarityMemberStockQuantity(memberTotalStockQuantity)
                    .build()
            ))
            .marketValue(memberTotalStockQuantity * solidarity.getStock().getClosingPrice())
            .memberCount(userHoldingStockService.getActiveCountByStockCode(solidarity.getStockCode()))
            .build();

        return solidarityDailySummaryRepository.save(solidarityDailySummary);
    }

    public SolidarityDailySummary updateSolidarityDailySummary(Solidarity solidarity) {
        final Stock stock = solidarity.getStock();
        final Long totalIssuedQuantity = stock.getTotalIssuedQuantity();
        final Long memberTotalStockQuantity = userHoldingStockService.sumStockQuantityOfSolidarityMembers(solidarity);

        SolidarityDailySummary mostRecentDailySummary = solidarity.getMostRecentDailySummary();

        mostRecentDailySummary.setStockQuantity(memberTotalStockQuantity);
        mostRecentDailySummary.setStake(
            CalculateStake.builder()
                .totalIssuedStockQuantity(totalIssuedQuantity)
                .totalSolidarityMemberStockQuantity(memberTotalStockQuantity)
                .build()
                .calculate()
        );
        mostRecentDailySummary.setMarketValue(memberTotalStockQuantity * solidarity.getStock().getClosingPrice());
        mostRecentDailySummary.setMemberCount(userHoldingStockService.getActiveCountByStockCode(solidarity.getStockCode()));
        mostRecentDailySummary.setUpdatedAt(LocalDateTime.now());

        return solidarityDailySummaryRepository.save(mostRecentDailySummary);
    }

    private Double calculateStake(CalculateStake dto) {
        return (dto.getTotalSolidarityMemberStockQuantity() * 100.0) / dto.getTotalIssuedStockQuantity();
    }

    public SolidarityDailySummary getMostRecentSolidarityDailySummary(String stockCode) {
        final Solidarity solidarity = solidarityService.getSolidarityByStockCode(stockCode);

        return Optional.of(solidarity.getMostRecentDailySummary())
            .orElseThrow(() -> new NotFoundException("연대에 집계된 정보가 없습니다."));
    }
}
