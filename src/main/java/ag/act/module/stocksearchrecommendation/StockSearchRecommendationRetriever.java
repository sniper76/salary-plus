package ag.act.module.stocksearchrecommendation;

import ag.act.configuration.initial.caching.CachingType;
import ag.act.entity.admin.StockRanking;
import ag.act.model.StockSearchRecommendationSectionResponse;
import ag.act.repository.interfaces.SimpleStock;
import ag.act.service.admin.stock.StockRankingService;
import ag.act.service.stocksearchtrend.StockSearchTrendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;


@Slf4j
@Component
@RequiredArgsConstructor
public class StockSearchRecommendationRetriever {

    private static final int REQUIRED_STOCK_COUNT = 5;
    private static final int FIRST_PAGE_NUMBER = 0;
    private final StockSearchRecommendationBaseDateTimeProvider stockSearchRecommendationBaseDateTimeProvider;
    private final StockSearchTrendService stockSearchTrendService;
    private final StockRankingService stockRankingService;
    private final StockSearchRecommendationSectionResponseConverter sectionResponseConverter;

    @Cacheable(CachingType.Fields.STOCK_SEARCH_RECOMMENDATION_SECTIONS)
    public List<StockSearchRecommendationSectionResponse> getRecommendationSections() {
        final StockSearchRecommendationSectionResponse stockSearchTrendSectionResponse = getTop5SearchTrendsStocks();
        final StockSearchRecommendationSectionResponse stakeStockRankingSectionResponse = getTop5StakeRankingStocks();

        return Stream.of(
                stockSearchTrendSectionResponse,
                stakeStockRankingSectionResponse
            )
            .toList();
    }

    private StockSearchRecommendationSectionResponse getTop5SearchTrendsStocks() {
        final List<SimpleStock> simpleStocks = stockSearchTrendService.getTop5SimpleStocksBasedOnTrendsWithinLastHour();

        if (simpleStocks.size() == REQUIRED_STOCK_COUNT) {
            return map(simpleStocks);
        }

        return map(stockSearchTrendService.getTop5SimpleStocksBasedOnTrendsWithinLastDay());
    }

    private StockSearchRecommendationSectionResponse map(List<SimpleStock> simpleStocks) {
        final String baseDateTime = stockSearchRecommendationBaseDateTimeProvider.getBaseDateTimeForSearchTrend();

        return sectionResponseConverter.convert(
            StockSearchRecommendationSectionType.SEARCH_TREND_STOCK,
            simpleStocks,
            baseDateTime
        );
    }

    private StockSearchRecommendationSectionResponse getTop5StakeRankingStocks() {
        final PageRequest pageRequest = getPageRequestForRankingStocks();
        final List<StockRanking> topNStockRankings = stockRankingService.findTopNStockRankings(pageRequest);
        final String baseDateTime = stockSearchRecommendationBaseDateTimeProvider.getBaseDateTimeForStockRanking(topNStockRankings);
        final List<SimpleStock> simpleStocks = toSimpleStocks(topNStockRankings);

        return sectionResponseConverter.convert(
            StockSearchRecommendationSectionType.STAKE_RANKING_STOCK,
            simpleStocks,
            baseDateTime
        );
    }

    @NotNull
    private List<SimpleStock> toSimpleStocks(List<StockRanking> topNStockRankings) {
        return topNStockRankings.stream()
            .map(SimpleStock.class::cast)
            .toList();
    }

    private PageRequest getPageRequestForRankingStocks() {
        return PageRequest.of(
            FIRST_PAGE_NUMBER,
            REQUIRED_STOCK_COUNT,
            Sort.by(Sort.Direction.ASC, "stakeRank")
        );
    }

}
