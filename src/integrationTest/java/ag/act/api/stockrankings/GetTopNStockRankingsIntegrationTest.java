package ag.act.api.stockrankings;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.admin.StockRanking;
import ag.act.itutil.holder.StocksTestHolder;
import ag.act.model.StockRankingDataResponse;
import ag.act.model.StockRankingResponse;
import ag.act.repository.admin.StockRankingRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static ag.act.TestUtil.toMultiValueMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GetTopNStockRankingsIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/stock-rankings";
    private static final LocalDate STOCK_RANKING_CREATE_DATE = LocalDate.now();
    private static final int PREPARED_STOCK_RANK_LIST_SIZE = 10;
    private static final int REQUEST_STOCK_RANK_LIST_SIZE = 5;
    private final StocksTestHolder stocksTestHolder = new StocksTestHolder();

    @Autowired
    private StockRankingRepository stockRankingRepository;

    private String jwt;
    private Map<String, Object> params;

    @BeforeEach
    void setUp() {
        itUtil.init();
        dbCleaner.clean();

        User user = itUtil.createUser();
        stocksTestHolder.initialize(itUtil.findAllStocks());

        mockStockRankings();

        jwt = itUtil.createJwt(user.getId());
    }

    @AfterEach
    void tearDown() {
        dbCleaner.clean();
    }

    private void mockStockRankings() {
        List<Stock> stocks = new ArrayList<>(stocksTestHolder.getItems());

        createStocks(stocks);
        createStockRanking(stocks);
    }

    private void createStocks(List<Stock> stocks) {
        if (stocks.size() < PREPARED_STOCK_RANK_LIST_SIZE) {
            int size = stocks.size();
            for (int i = 0; i < PREPARED_STOCK_RANK_LIST_SIZE - size; i++) {
                Stock stock = itUtil.createStock();
                stocks.add(stock);
                stocksTestHolder.addOrSet(stock);
            }
        }
    }

    private void createStockRanking(List<Stock> stocks) {
        for (int i = 1; i <= stocks.size(); i++) {
            Stock stock = stocks.get(i - 1);
            StockRanking stockRanking = new StockRanking();
            stockRanking.setStockCode(stock.getCode());
            stockRanking.setStockName(stock.getName());
            stockRanking.setDate(STOCK_RANKING_CREATE_DATE);
            stockRanking.setMarketValueRank(i);
            stockRanking.setMarketValueRankDelta(0);
            stockRanking.setStakeRank(i);
            stockRanking.setStakeRankDelta(0);
            stockRanking.setMarketValue(i * 100000L);
            stockRanking.setStake(i * 10.0);
            stockRankingRepository.save(stockRanking);
        }
    }

    @Nested
    class GetTopNStockRankingsTest {
        @Nested
        class SortByMarketValue {
            @BeforeEach
            void setUp() {
                params = Map.of(
                    "size", String.valueOf(REQUEST_STOCK_RANK_LIST_SIZE),
                    "sorts", "marketValueRank:ASC"
                );
            }

            @Test
            @DisplayName("should return 200 response code when call api" + TARGET_API)
            void shouldReturnSuccess() throws Exception {
                MvcResult result = callApi(status().isOk());

                StockRankingDataResponse response = getResponse(result);
                assertResponseSize(response, REQUEST_STOCK_RANK_LIST_SIZE);
                assertResponseSort(response.getData());
            }

            private void assertResponseSort(List<StockRankingResponse> responses) {
                List<StockRanking> stockRankings = itUtil.getStockRankingListByDate(STOCK_RANKING_CREATE_DATE);
                stockRankings.sort(Comparator.comparing(StockRanking::getMarketValueRank));

                for (int i = 0; i < responses.size(); i++) {
                    Integer actualRank = responses.get(i).getMarketValueRank();
                    Integer expectedRank = stockRankings.get(i).getMarketValueRank();

                    assertThat(actualRank, is(expectedRank));
                    assertResponse(stockRankings.get(i), responses.get(i));
                }
            }
        }

        @Nested
        class SortByStake {
            @BeforeEach
            void setUp() {
                params = Map.of(
                    "size", String.valueOf(REQUEST_STOCK_RANK_LIST_SIZE),
                    "sorts", "stakeRank:ASC"
                );
            }

            @Test
            @DisplayName("should return 200 response code when call api" + TARGET_API)
            void shouldReturnSuccess() throws Exception {
                MvcResult result = callApi(status().isOk());

                StockRankingDataResponse response = getResponse(result);
                assertResponseSize(response, REQUEST_STOCK_RANK_LIST_SIZE);
                assertResponseSort(response.getData());
            }

            private void assertResponseSort(List<StockRankingResponse> responses) {
                List<StockRanking> stockRankings = itUtil.getStockRankingListByDate(STOCK_RANKING_CREATE_DATE);
                stockRankings.sort(Comparator.comparing(StockRanking::getStakeRank));

                for (int i = 0; i < responses.size(); i++) {
                    final Integer actualRank = responses.get(i).getStakeRank();
                    final Integer expectedRank = stockRankings.get(i).getStakeRank();

                    assertThat(actualRank, is(expectedRank));
                    assertResponse(stockRankings.get(i), responses.get(i));
                }
            }
        }

        @Nested
        class SortByStakeForGuest {
            @BeforeEach
            void setUp() {
                jwt = "";
                params = Map.of(
                    "size", String.valueOf(REQUEST_STOCK_RANK_LIST_SIZE),
                    "sorts", "stakeRank:ASC"
                );
            }

            @Test
            @DisplayName("should return 200 response code when call api" + TARGET_API)
            void shouldReturnSuccess() throws Exception {
                MvcResult result = callApiWeb(status().isOk());

                StockRankingDataResponse response = getResponse(result);
                assertResponseSize(response, REQUEST_STOCK_RANK_LIST_SIZE);
                assertResponseSort(response.getData());
            }

            private void assertResponseSort(List<StockRankingResponse> responses) {
                List<StockRanking> stockRankings = itUtil.getStockRankingListByDate(STOCK_RANKING_CREATE_DATE);
                stockRankings.sort(Comparator.comparing(StockRanking::getStakeRank));

                for (int i = 0; i < responses.size(); i++) {
                    final Integer actualRank = responses.get(i).getStakeRank();
                    final Integer expectedRank = stockRankings.get(i).getStakeRank();

                    assertThat(actualRank, is(expectedRank));
                    assertResponse(stockRankings.get(i), responses.get(i));
                }
            }
        }

        @Nested
        class SortByNotMatchSortColumnForGuest {
            @BeforeEach
            void setUp() {
                jwt = "";
                params = Map.of(
                    "size", String.valueOf(-10),
                    "sorts", "notMatchColumn:ASC"
                );
            }

            @Test
            @DisplayName("should return 200 response code when call api" + TARGET_API)
            void shouldReturnSuccess() throws Exception {
                MvcResult result = callApiWeb(status().isOk());

                StockRankingDataResponse response = getResponse(result);
                assertResponseSize(response, Math.min(50, stocksTestHolder.getItems().size()));
                assertResponseSort(response.getData());
            }

            private void assertResponseSort(List<StockRankingResponse> responses) {
                List<StockRanking> stockRankings = itUtil.getStockRankingListByDate(STOCK_RANKING_CREATE_DATE);
                stockRankings.sort(Comparator.comparing(StockRanking::getStakeRank));

                for (int i = 0; i < responses.size(); i++) {
                    final Integer actualRank = responses.get(i).getStakeRank();
                    final Integer expectedRank = stockRankings.get(i).getStakeRank();

                    assertThat(actualRank, is(expectedRank));
                    assertResponse(stockRankings.get(i), responses.get(i));
                }
            }
        }
    }

    private void assertResponse(StockRanking stockRanking, StockRankingResponse response) {
        assertThat(stockRanking.getStockName(), is(response.getStockName()));
        assertThat(stockRanking.getStockCode(), is(response.getStockCode()));
    }

    private void assertResponseSize(StockRankingDataResponse response, int listSize) {
        assertThat(response.getData().size(), is(listSize));
    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                get(TARGET_API)
                    .params(toMultiValueMap(params))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    private MvcResult callApiWeb(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                get(TARGET_API)
                    .params(toMultiValueMap(params))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(X_APP_VERSION, X_APP_VERSION_WEB)
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    private StockRankingDataResponse getResponse(MvcResult result) throws Exception {
        return itUtil.getResult(result, StockRankingDataResponse.class);
    }
}
