package ag.act.api.batch;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.BatchLog;
import ag.act.entity.Solidarity;
import ag.act.entity.SolidarityDailyStatistics;
import ag.act.entity.SolidarityDailySummary;
import ag.act.entity.Stock;
import ag.act.repository.SolidarityDailyStatisticsRepository;
import ag.act.util.DateTimeUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class CreateSolidarityDailyStatisticsBatchIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String BATCH_NAME = "CREATE_SOLIDARITY_DAILY_STATISTICS";
    private static final String TARGET_API = "/api/batch/create-solidarity-daily-statistics";
    private List<MockedStatic<?>> statics;
    @Autowired
    private SolidarityDailyStatisticsRepository solidarityDailyStatisticsRepository;
    private Map<String, Integer> request;
    private List<Solidarity> solidarityList;

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(DateTimeUtil.class));
        itUtil.init();

        itUtil.findAllSolidarities()
            .stream()
            .map(solidarity -> {
                solidarity.setMostRecentDailySummary(null);
                solidarity.setSecondMostRecentDailySummary(null);
                return solidarity;
            })
            .forEach(itUtil::updateSolidarity);

        final int batchPeriod = 1;
        request = Map.of("batchPeriod", batchPeriod);
        String date = someString(5);

        solidarityList = Stream.concat(
                Stream.of(mockDataForSolidarityDailySummary(false)),
                Stream.of(mockDataForSolidarityDailySummary(true))
            )
            .filter(Objects::nonNull)
            .toList();

        given(DateTimeUtil.getCurrentFormattedDateTime()).willReturn(date);
        given(DateTimeUtil.getCurrentDateTimeMinusHours(batchPeriod)).willReturn(LocalDateTime.now());
    }

    private Solidarity mockDataForSolidarityDailySummary(boolean isSecondMostRecentSolidarityDailySummaryExists) {
        Stock stock = itUtil.createStock();
        stock.setTotalIssuedQuantity(10000L);
        stock.setClosingPrice(100);
        itUtil.updateStock(stock);

        final Solidarity solidarity = itUtil.createSolidarity(stock.getCode());

        if (isSecondMostRecentSolidarityDailySummaryExists) {
            SolidarityDailySummary summary = itUtil.createSolidarityDailySummary();

            solidarity.setSecondMostRecentDailySummary(summary);
            return itUtil.updateSolidarity(solidarity);
        }

        return null;
    }

    @DisplayName("Should return 200 response code when call " + TARGET_API)
    @Test
    void shouldReturnSuccess() throws Exception {
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

        final ag.act.model.SimpleStringResponse result = objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            ag.act.model.SimpleStringResponse.class
        );

        assertBatchResultResponse(result);

        final List<SolidarityDailyStatistics> statisticsList =
            solidarityDailyStatisticsRepository.findAll().stream().toList();

        assertThat(statisticsList.size(), is(1));

        final SolidarityDailyStatistics actualStatistics = statisticsList.get(0);
        final Solidarity expectedSolidarity = solidarityList.get(0);
        final SolidarityDailySummary secondMostRecentDailySummary = expectedSolidarity.getSecondMostRecentDailySummary();

        assertThat(actualStatistics.getStockCode(), is(expectedSolidarity.getStockCode()));
        assertThat(actualStatistics.getStake(), is(secondMostRecentDailySummary.getStake()));
        assertThat(actualStatistics.getStockQuantity(), is(secondMostRecentDailySummary.getStockQuantity()));
        assertThat(actualStatistics.getMarketValue(), is(secondMostRecentDailySummary.getMarketValue()));
        assertThat(actualStatistics.getMemberCount(), is(secondMostRecentDailySummary.getMemberCount()));
        assertThat(actualStatistics.getDate(), is(secondMostRecentDailySummary.getUpdatedAt().toLocalDate()));
    }

    private void assertBatchResultResponse(ag.act.model.SimpleStringResponse result) {
        final String expectedResult = "[Batch] %s batch successfully finished. [creation: %s / %s]".formatted(BATCH_NAME, 1, 1);
        assertThat(result.getStatus(), is(expectedResult));

        final BatchLog batchLog = itUtil.findLastBatchLog(BATCH_NAME).orElseThrow(() -> new RuntimeException("[TEST] BatchLog를 찾을 수 없습니다."));
        assertThat(batchLog.getResult(), is(expectedResult));
    }

}
