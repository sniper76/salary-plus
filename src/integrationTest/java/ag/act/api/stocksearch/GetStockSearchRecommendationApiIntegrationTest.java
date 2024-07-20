package ag.act.api.stocksearch;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.configuration.initial.caching.stocksearchrecommendation.StockSearchRecommendationCachingScheduler;
import ag.act.entity.Stock;
import ag.act.entity.StockSearchTrend;
import ag.act.entity.User;
import ag.act.model.SimpleStockResponse;
import ag.act.model.StockSearchRecommendationSectionDataResponse;
import ag.act.model.StockSearchRecommendationSectionResponse;
import ag.act.module.stocksearchrecommendation.StockSearchRecommendationSectionType;
import ag.act.util.KoreanDateTimeUtil;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static ag.act.TestUtil.assertTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

@SuppressWarnings({"AbbreviationAsWordInName"})
class GetStockSearchRecommendationApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/stocks/search-recommendations";

    @Autowired
    private StockSearchRecommendationCachingScheduler stockSearchRecommendationCachingScheduler;
    private String jwt;
    private User currentUser;
    private String stockTrendBaseDateTime;
    private String stockRankingBaseDateTime;
    private Stock stockForSearch1;
    private Stock stockForSearch2;
    private Stock stockForSearch3;
    private Stock stockForSearch4;
    private Stock stockForSearch5;
    private Stock stockForRanking1;
    private Stock stockForRanking2;
    private Stock stockForRanking3;
    private Stock stockForRanking4;
    private Stock stockForRanking5;

    @BeforeEach
    void setUp() {
        itUtil.init();
        itUtil.cleanUpAllStockRanking();
        itUtil.cleanUpAllStockSearchTrends();

        currentUser = itUtil.createUser();
        jwt = itUtil.createJwt(currentUser.getId());

        stockTrendBaseDateTime = someAlphanumericString(10);
        stockRankingBaseDateTime = someAlphanumericString(10);

        Mockito.clearInvocations(stockSearchRecommendationBaseDateTimeProvider);
        given(stockSearchRecommendationBaseDateTimeProvider.getBaseDateTimeForSearchTrend()).willReturn(stockTrendBaseDateTime);
        given(stockSearchRecommendationBaseDateTimeProvider.getBaseDateTimeForStockRanking(anyList())).willReturn(stockRankingBaseDateTime);
    }

    @Nested
    class WhenStockSearchTrendsWithinOneHour {

        @BeforeEach
        void setUp() {
            prepareStockSearchTrendsWithinOneHour();
            prepareStockRankings();
            stockSearchRecommendationCachingScheduler.run();
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final MvcResult response = callApi();
            final var result = itUtil.getResult(response, StockSearchRecommendationSectionDataResponse.class);

            assertStockTrendSectionResponse(result.getData().get(0));
            assertStockRankingSectionResponse(result.getData().get(1));
        }

        private void prepareStockSearchTrendsWithinOneHour() {
            final Stock stockForSearch0 = itUtil.createStock("STOCK_SEARCH_0000__ONE_HOUR__" + someAlphanumericString(10));
            stockForSearch1 = itUtil.createStock("STOCK_SEARCH_0001__ONE_HOUR__" + someAlphanumericString(10));
            stockForSearch2 = itUtil.createStock("STOCK_SEARCH_0002__ONE_HOUR__" + someAlphanumericString(10));
            stockForSearch3 = itUtil.createStock("STOCK_SEARCH_0003__ONE_HOUR__" + someAlphanumericString(10));
            stockForSearch4 = itUtil.createStock("STOCK_SEARCH_0004__ONE_HOUR__" + someAlphanumericString(10));
            stockForSearch5 = itUtil.createStock("STOCK_SEARCH_0005__ONE_HOUR__" + someAlphanumericString(10));
            final Stock stockForSearch6 = itUtil.createStock("STOCK_SEARCH_0006__ONE_HOUR__" + someAlphanumericString(10));
            final Stock stockForSearch7 = itUtil.createStock("STOCK_SEARCH_0007__ONE_HOUR__" + someAlphanumericString(10));
            final Stock stockForSearch8 = itUtil.createStock("STOCK_SEARCH_0008__ONE_HOUR__" + someAlphanumericString(10));
            final Stock stockForSearch9 = itUtil.createStock("STOCK_SEARCH_0009__ONE_HOUR__" + someAlphanumericString(10));

            // 최근 한시간 이전의 검색 트렌드 // 오래된 데이터라는 의미
            createStockSearchTrendBeforeOneHour(stockForSearch0);
            createStockSearchTrendBeforeOneHour(stockForSearch0);
            createStockSearchTrendBeforeOneHour(stockForSearch0);
            createStockSearchTrendBeforeOneHour(stockForSearch0);
            createStockSearchTrendBeforeOneHour(stockForSearch0);
            createStockSearchTrendBeforeOneHour(stockForSearch4);
            createStockSearchTrendBeforeOneHour(stockForSearch5);
            createStockSearchTrendBeforeOneHour(stockForSearch5);
            createStockSearchTrendBeforeOneHour(stockForSearch6);
            createStockSearchTrendBeforeOneHour(stockForSearch6);
            createStockSearchTrendBeforeOneHour(stockForSearch6);
            createStockSearchTrendBeforeOneHour(stockForSearch6);
            createStockSearchTrendBeforeOneHour(stockForSearch6);
            createStockSearchTrendBeforeOneHour(stockForSearch7);
            createStockSearchTrendBeforeOneHour(stockForSearch7);
            createStockSearchTrendBeforeOneHour(stockForSearch8);
            createStockSearchTrendBeforeOneHour(stockForSearch8);
            createStockSearchTrendBeforeOneHour(stockForSearch8);
            createStockSearchTrendBeforeOneHour(stockForSearch9);
            createStockSearchTrendBeforeOneHour(stockForSearch9);
            createStockSearchTrendBeforeOneHour(stockForSearch9);
            createStockSearchTrendBeforeOneHour(stockForSearch9);

            // 최근 한시간 이내의 검색 트렌드
            createStockSearchTrendWithinOneHour(stockForSearch1);
            createStockSearchTrendWithinOneHour(stockForSearch1);
            createStockSearchTrendWithinOneHour(stockForSearch1);
            createStockSearchTrendWithinOneHour(stockForSearch1);
            createStockSearchTrendWithinOneHour(stockForSearch1);

            createStockSearchTrendWithinOneHour(stockForSearch2);
            createStockSearchTrendWithinOneHour(stockForSearch2);
            createStockSearchTrendWithinOneHour(stockForSearch2);
            createStockSearchTrendWithinOneHour(stockForSearch2);

            createStockSearchTrendWithinOneHour(stockForSearch3);
            createStockSearchTrendWithinOneHour(stockForSearch3);
            createStockSearchTrendWithinOneHour(stockForSearch3);

            createStockSearchTrendWithinOneHour(stockForSearch4);
            createStockSearchTrendWithinOneHour(stockForSearch4);

            createStockSearchTrendWithinOneHour(stockForSearch5);
            createStockSearchTrendWithinOneHour(stockForSearch5);
            createStockSearchTrendWithinOneHour(stockForSearch5);
            createStockSearchTrendWithinOneHour(stockForSearch5);
            createStockSearchTrendWithinOneHour(stockForSearch5);
            createStockSearchTrendWithinOneHour(stockForSearch5);
            createStockSearchTrendWithinOneHour(stockForSearch5);

            createStockSearchTrendWithinOneHour(stockForSearch6);
            createStockSearchTrendWithinOneHour(stockForSearch7);
            createStockSearchTrendWithinOneHour(stockForSearch8);
            createStockSearchTrendWithinOneHour(stockForSearch9);
        }

        private StockSearchTrend createStockSearchTrendBeforeOneHour(Stock stock) {
            return itUtil.createStockSearchTrend(stock, currentUser, LocalDateTime.now().minusMinutes(someIntegerBetween(70, 200)));
        }

        private StockSearchTrend createStockSearchTrendWithinOneHour(Stock stock) {
            return itUtil.createStockSearchTrend(stock, currentUser, LocalDateTime.now().minusMinutes(someIntegerBetween(1, 50)));
        }
    }

    @Nested
    class WhenStockSearchTrendsWithinOneDay {

        @BeforeEach
        void setUp() {
            prepareStockSearchTrendsWithinOneDay();
            prepareStockRankings();
            stockSearchRecommendationCachingScheduler.run();
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final MvcResult response = callApi();
            final var result = itUtil.getResult(response, StockSearchRecommendationSectionDataResponse.class);

            assertStockTrendSectionResponse(result.getData().get(0));
            assertStockRankingSectionResponse(result.getData().get(1));
        }

        private void prepareStockSearchTrendsWithinOneDay() {
            final Stock stockForSearch0 = itUtil.createStock("STOCK_SEARCH_0000__ONE_DAY__" + someAlphanumericString(10));
            stockForSearch1 = itUtil.createStock("STOCK_SEARCH_0001__ONE_DAY__" + someAlphanumericString(10));
            stockForSearch2 = itUtil.createStock("STOCK_SEARCH_0002__ONE_DAY__" + someAlphanumericString(10));
            stockForSearch3 = itUtil.createStock("STOCK_SEARCH_0003__ONE_DAY__" + someAlphanumericString(10));
            stockForSearch4 = itUtil.createStock("STOCK_SEARCH_0004__ONE_DAY__" + someAlphanumericString(10));
            stockForSearch5 = itUtil.createStock("STOCK_SEARCH_0005__ONE_DAY__" + someAlphanumericString(10));
            final Stock stockForSearch6 = itUtil.createStock("STOCK_SEARCH_0006__ONE_DAY__" + someAlphanumericString(10));
            final Stock stockForSearch7 = itUtil.createStock("STOCK_SEARCH_0007__ONE_DAY__" + someAlphanumericString(10));
            final Stock stockForSearch8 = itUtil.createStock("STOCK_SEARCH_0008__ONE_DAY__" + someAlphanumericString(10));
            final Stock stockForSearch9 = itUtil.createStock("STOCK_SEARCH_0009__ONE_DAY__" + someAlphanumericString(10));

            // 최근 하루 이전의 검색 트렌드 // 오래된 데이터라는 의미
            createStockSearchTrendBeforeOneDay(stockForSearch0);
            createStockSearchTrendBeforeOneDay(stockForSearch0);
            createStockSearchTrendBeforeOneDay(stockForSearch0);
            createStockSearchTrendBeforeOneDay(stockForSearch0);
            createStockSearchTrendBeforeOneDay(stockForSearch0);
            createStockSearchTrendBeforeOneDay(stockForSearch4);
            createStockSearchTrendBeforeOneDay(stockForSearch5);
            createStockSearchTrendBeforeOneDay(stockForSearch5);
            createStockSearchTrendBeforeOneDay(stockForSearch6);
            createStockSearchTrendBeforeOneDay(stockForSearch6);
            createStockSearchTrendBeforeOneDay(stockForSearch6);
            createStockSearchTrendBeforeOneDay(stockForSearch6);
            createStockSearchTrendBeforeOneDay(stockForSearch6);
            createStockSearchTrendBeforeOneDay(stockForSearch7);
            createStockSearchTrendBeforeOneDay(stockForSearch7);
            createStockSearchTrendBeforeOneDay(stockForSearch8);
            createStockSearchTrendBeforeOneDay(stockForSearch8);
            createStockSearchTrendBeforeOneDay(stockForSearch8);
            createStockSearchTrendBeforeOneDay(stockForSearch9);
            createStockSearchTrendBeforeOneDay(stockForSearch9);
            createStockSearchTrendBeforeOneDay(stockForSearch9);
            createStockSearchTrendBeforeOneDay(stockForSearch9);

            // 최근 하루 이내의 검색 트렌드
            createStockSearchTrendWithinOneDay(stockForSearch1);
            createStockSearchTrendWithinOneDay(stockForSearch1);
            createStockSearchTrendWithinOneDay(stockForSearch1);
            createStockSearchTrendWithinOneDay(stockForSearch1);
            createStockSearchTrendWithinOneDay(stockForSearch1);

            createStockSearchTrendWithinOneDay(stockForSearch2);
            createStockSearchTrendWithinOneDay(stockForSearch2);
            createStockSearchTrendWithinOneDay(stockForSearch2);
            createStockSearchTrendWithinOneDay(stockForSearch2);

            createStockSearchTrendWithinOneDay(stockForSearch3);
            createStockSearchTrendWithinOneDay(stockForSearch3);
            createStockSearchTrendWithinOneDay(stockForSearch3);

            createStockSearchTrendWithinOneDay(stockForSearch4);
            createStockSearchTrendWithinOneDay(stockForSearch4);

            createStockSearchTrendWithinOneDay(stockForSearch5);
            createStockSearchTrendWithinOneDay(stockForSearch5);
            createStockSearchTrendWithinOneDay(stockForSearch5);
            createStockSearchTrendWithinOneDay(stockForSearch5);
            createStockSearchTrendWithinOneDay(stockForSearch5);
            createStockSearchTrendWithinOneDay(stockForSearch5);
            createStockSearchTrendWithinOneDay(stockForSearch5);

            createStockSearchTrendWithinOneDay(stockForSearch6);
            createStockSearchTrendWithinOneDay(stockForSearch7);
            createStockSearchTrendWithinOneDay(stockForSearch8);
            createStockSearchTrendWithinOneDay(stockForSearch9);
        }

        private StockSearchTrend createStockSearchTrendBeforeOneDay(Stock stock) {
            return itUtil.createStockSearchTrend(stock, currentUser, LocalDateTime.now().minusHours(someIntegerBetween(25, 200)));
        }

        private StockSearchTrend createStockSearchTrendWithinOneDay(Stock stock) {
            return itUtil.createStockSearchTrend(stock, currentUser, LocalDateTime.now().minusHours(someIntegerBetween(0, 23)));
        }
    }

    private void prepareStockRankings() {
        final Stock stockForRanking0 = itUtil.createStock();
        stockForRanking1 = itUtil.createStock();
        stockForRanking2 = itUtil.createStock();
        stockForRanking3 = itUtil.createStock();
        stockForRanking4 = itUtil.createStock();
        stockForRanking5 = itUtil.createStock();
        final Stock stockForRanking6 = itUtil.createStock();
        final Stock stockForRanking7 = itUtil.createStock();
        final Stock stockForRanking8 = itUtil.createStock();
        final Stock stockForRanking9 = itUtil.createStock();

        final LocalDate yesterday = KoreanDateTimeUtil.getYesterdayLocalDate();

        itUtil.createStockRanking(stockForRanking0, yesterday, 10, 10);
        itUtil.createStockRanking(stockForRanking1, yesterday, 1, 1);
        itUtil.createStockRanking(stockForRanking2, yesterday, 2, 2);
        itUtil.createStockRanking(stockForRanking3, yesterday, 3, 3);
        itUtil.createStockRanking(stockForRanking4, yesterday, 4, 4);
        itUtil.createStockRanking(stockForRanking5, yesterday, 5, 5);
        itUtil.createStockRanking(stockForRanking6, yesterday, 6, 6);
        itUtil.createStockRanking(stockForRanking7, yesterday, 7, 7);
        itUtil.createStockRanking(stockForRanking8, yesterday, 8, 8);
        itUtil.createStockRanking(stockForRanking9, yesterday, 9, 9);
    }

    private void assertStockRankingSectionResponse(StockSearchRecommendationSectionResponse stockRankingSectionResponse) {
        assertThat(stockRankingSectionResponse.getTitle(), is(StockSearchRecommendationSectionType.STAKE_RANKING_STOCK.getTitle()));
        assertThat(stockRankingSectionResponse.getType(), is(StockSearchRecommendationSectionType.STAKE_RANKING_STOCK.name()));
        assertThat(stockRankingSectionResponse.getBaseDateTime(), is(stockRankingBaseDateTime));

        final List<SimpleStockResponse> stocks = stockRankingSectionResponse.getStocks();
        assertThat(stocks.size(), is(5));
        assertStock(stocks.get(0), stockForRanking1);
        assertStock(stocks.get(1), stockForRanking2);
        assertStock(stocks.get(2), stockForRanking3);
        assertStock(stocks.get(3), stockForRanking4);
        assertStock(stocks.get(4), stockForRanking5);
    }

    private void assertStockTrendSectionResponse(StockSearchRecommendationSectionResponse stockTrendSectionResponse) {
        assertThat(stockTrendSectionResponse.getTitle(), is(StockSearchRecommendationSectionType.SEARCH_TREND_STOCK.getTitle()));
        assertThat(stockTrendSectionResponse.getType(), is(StockSearchRecommendationSectionType.SEARCH_TREND_STOCK.name()));
        assertThat(stockTrendSectionResponse.getBaseDateTime(), is(stockTrendBaseDateTime));

        final List<SimpleStockResponse> stocks = stockTrendSectionResponse.getStocks();
        assertThat(stocks.size(), is(5));
        assertStock(stocks.get(0), stockForSearch5);
        assertStock(stocks.get(1), stockForSearch1);
        assertStock(stocks.get(2), stockForSearch2);
        assertStock(stocks.get(3), stockForSearch3);
        assertStock(stocks.get(4), stockForSearch4);
    }

    private void assertStock(SimpleStockResponse simpleStockResponse, Stock stock) {
        assertTime(simpleStockResponse.getCode(), is(stock.getCode()));
        assertTime(simpleStockResponse.getName(), is(stock.getName()));
    }

    @NotNull
    private MvcResult callApi() throws Exception {
        return mockMvc
            .perform(
                get(TARGET_API)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, "Bearer " + jwt)
            )
            .andExpect(status().isOk())
            .andReturn();
    }
}
