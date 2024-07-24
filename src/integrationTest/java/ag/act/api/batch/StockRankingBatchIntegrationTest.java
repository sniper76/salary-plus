package ag.act.api.batch;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.BatchLog;
import ag.act.entity.Solidarity;
import ag.act.entity.SolidarityDailyStatistics;
import ag.act.entity.admin.StockRanking;
import ag.act.model.BatchRequest;
import ag.act.model.SimpleStringResponse;
import ag.act.util.DateTimeUtil;
import ag.act.util.KoreanDateTimeUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class StockRankingBatchIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/batch/stock-rankings";
    private static final String BATCH_NAME = "STOCK_RANKINGS";
    private List<MockedStatic<?>> statics;
    private Map<String, Object> request;
    private LocalDate yesterday;
    private int dataSize;
    private List<StatisticsData> statisticsDataList;
    private List<Long> dataList;

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(DateTimeUtil.class));
        itUtil.init();

        dataSize = someIntegerBetween(10, 20);
        dataList = new ArrayList<>(dataSize);
        for (int index = 0; index < dataSize; index++) {
            dataList.add((long) (index * 10));
        }
        Collections.shuffle(dataList);

        final int batchPeriod = 1;
        request = Map.of("batchPeriod", batchPeriod, "periodTimeUnit", BatchRequest.PeriodTimeUnitEnum.MINUTE.name());

        String date = someString(5);
        given(DateTimeUtil.getCurrentFormattedDateTime()).willReturn(date);
        given(DateTimeUtil.getCurrentDateTimeMinusMinutes(batchPeriod)).willReturn(LocalDateTime.now());
    }

    private SimpleStringResponse callApi() throws Exception {
        MvcResult response = mockMvc
            .perform(
                post(TARGET_API)
                    .content(objectMapperUtil.toJson(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(batchXApiKey())
            )
            .andExpect(status().isOk())
            .andReturn();

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            SimpleStringResponse.class
        );
    }

    private void assertResponse(SimpleStringResponse result, int resultSize) {
        final String expectedResult = "[Batch] %s batch successfully finished. [creation: %s / %s]".formatted(
            BATCH_NAME, resultSize, resultSize);
        assertThat(result.getStatus(), is(expectedResult));

        final BatchLog batchLog = itUtil.findLastBatchLog(BATCH_NAME).orElseThrow(() -> new RuntimeException("[TEST] BatchLog를 찾을 수 없습니다."));
        assertThat(batchLog.getResult(), is(expectedResult));
    }

    private void stubStockWithStatistics(boolean makeTwoDaysAgoData) {
        itUtil.deleteAllSolidarityDailyStatistics();
        itUtil.cleanUpAllStockRanking();

        final ZonedDateTime nowInKoreanTime = KoreanDateTimeUtil.getNowInKoreanTime();
        yesterday = nowInKoreanTime.minusDays(1).toLocalDate();
        final LocalDate twoDaysAgo = nowInKoreanTime.minusDays(2).toLocalDate();

        statisticsDataList = IntStream.range(0, dataSize)
            .mapToObj(index -> {
                final String stockCode = someStockCode();
                itUtil.createStock(stockCode);
                final Solidarity solidarity = itUtil.createSolidarity(stockCode);

                return new StatisticsData(
                    itUtil.createSolidarityDailyStatistics(stockCode, solidarity.getId(), yesterday),
                    makeTwoDaysAgoData ? makeStockRanking(stockCode, twoDaysAgo, index + 1) : null
                );
            }).toList();
    }

    private StockRanking makeStockRanking(String stockCode, LocalDate twoDaysAgo, int rank) {
        final StockRanking stockRanking = itUtil.createStockRanking(stockCode, twoDaysAgo, rank, rank);

        stockRanking.setStake(dataList.get(rank - 1).doubleValue());
        stockRanking.setMarketValue(dataList.get(rank - 1));
        return itUtil.updateStockRanking(stockRanking);
    }

    record StatisticsData(
        SolidarityDailyStatistics yesterdaySolidarityDailyStatistics,
        StockRanking twoDaysAgoStockRanking
    ) {

    }

    @Nested
    class WhenSuccess {

        private void assertRanking(List<StockRanking> stockRankingList) {

            assertStockRankingWithValue(
                stockRankingList,
                solidarityDailyStatistics -> BigDecimal.valueOf(solidarityDailyStatistics.getMarketValue()),
                StockRanking::getMarketValueRank,
                stockRanking -> BigDecimal.valueOf(stockRanking.getMarketValue())
            );

            assertStockRankingWithValue(
                stockRankingList,
                solidarityDailyStatistics -> BigDecimal.valueOf(solidarityDailyStatistics.getStake()),
                StockRanking::getStakeRank,
                stockRanking -> BigDecimal.valueOf(stockRanking.getStake())
            );
        }

        private void assertStockRankingWithValue(
            List<StockRanking> stockRankingList,
            Function<SolidarityDailyStatistics, BigDecimal> getValueFromDailyStatistics,
            Function<StockRanking, Integer> getValueRank,
            Function<StockRanking, BigDecimal> getValueFromStockRanking
        ) {
            final List<SolidarityDailyStatistics> sortedSolidarityDailyStatistics = statisticsDataList.stream()
                .map(StatisticsData::yesterdaySolidarityDailyStatistics)
                .sorted(Comparator.comparing(getValueFromDailyStatistics).reversed()).toList();

            final List<StockRanking> sortedStockRankings = stockRankingList.stream()
                .sorted(Comparator.comparing(getValueRank)).toList();

            assertRankingItems(getValueFromDailyStatistics, getValueFromStockRanking, sortedSolidarityDailyStatistics, sortedStockRankings);
        }

        private void assertRankingItems(
            Function<SolidarityDailyStatistics, BigDecimal> getValueFromDailyStatistics,
            Function<StockRanking, BigDecimal> getValueFromStockRanking,
            List<SolidarityDailyStatistics> sortedSolidarityDailyStatistics,
            List<StockRanking> sortedStockRankings
        ) {

            final Map<String, StockRanking> twoDaysAgoStockRankingMap = statisticsDataList.stream()
                .map(StatisticsData::twoDaysAgoStockRanking)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(StockRanking::getStockCode, Function.identity()));

            for (int index = 0; index < sortedSolidarityDailyStatistics.size(); index++) {
                final SolidarityDailyStatistics solidarityDailyStatistics = sortedSolidarityDailyStatistics.get(index);
                final StockRanking stockRanking = sortedStockRankings.get(index);

                assertThat(stockRanking.getStockCode(), is(solidarityDailyStatistics.getStockCode()));
                assertThat(
                    Optional.of(stockRanking).map(getValueFromStockRanking).orElseThrow(),
                    is(Optional.of(solidarityDailyStatistics).map(getValueFromDailyStatistics).orElseThrow())
                );
                final Optional<StockRanking> twoDaysAgoStockRanking = Optional.ofNullable(
                    twoDaysAgoStockRankingMap.get(stockRanking.getStockCode())
                );
                assertThat(
                    stockRanking.getStakeRankDelta(),
                    is(twoDaysAgoStockRanking.map(it -> it.getStakeRank() - stockRanking.getStakeRank()).orElse(0))
                );
                assertThat(
                    stockRanking.getMarketValueRankDelta(),
                    is(twoDaysAgoStockRanking.map(it -> it.getMarketValueRank() - stockRanking.getMarketValueRank()).orElse(0))
                );
                assertThat(stockRanking.getStockName(), notNullValue());
            }
        }

        @Nested
        class AndExistTwoDaysAgoData {
            @BeforeEach
            void setUp() {
                stubStockWithStatistics(true);
            }

            @Order(2)
            @Test
            void shouldBeSuccess() throws Exception {
                final SimpleStringResponse result = callApi();
                assertResponse(result, dataSize);
                final List<StockRanking> stockRankingList = itUtil.getStockRankingListByDate(yesterday);
                assertThat(stockRankingList.size(), is(dataSize));
                assertRanking(stockRankingList);
            }
        }

        @Nested
        class AndNotFoundTwoDaysAgoData {
            @BeforeEach
            void setUp() {
                stubStockWithStatistics(false);
            }

            @Order(1)
            @Test
            void shouldBeSuccess() throws Exception {
                final SimpleStringResponse result = callApi();
                assertResponse(result, dataSize);
                final List<StockRanking> stockRankingList = itUtil.getStockRankingListByDate(yesterday);
                assertThat(stockRankingList.size(), is(dataSize));
                assertRanking(stockRankingList);
            }
        }
    }
}
