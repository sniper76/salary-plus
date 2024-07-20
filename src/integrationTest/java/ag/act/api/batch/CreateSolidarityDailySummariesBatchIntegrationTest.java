package ag.act.api.batch;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.BatchLog;
import ag.act.entity.Solidarity;
import ag.act.entity.SolidarityDailySummary;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.repository.SolidarityDailySummaryRepository;
import ag.act.repository.SolidarityRepository;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class CreateSolidarityDailySummariesBatchIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String BATCH_NAME = "CREATE_SOLIDARITY_DAILY_SUMMARIES";
    private static final String TARGET_API = "/api/batch/create-solidarity-daily-summaries";
    private List<MockedStatic<?>> statics;
    @Autowired
    private SolidarityDailySummaryRepository solidarityDailySummaryRepository;
    @Autowired
    private SolidarityRepository solidarityRepository;
    private Map<String, Integer> request;

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(DateTimeUtil.class));
        itUtil.init();

        dbCleaner.clean();

        final int batchPeriod = 1;
        request = Map.of("batchPeriod", batchPeriod);
        String date = someString(5);

        mockDataForSolidarityDailySummary(false);
        mockDataForSolidarityDailySummary(true);

        given(DateTimeUtil.getCurrentFormattedDateTime()).willReturn(date);
        given(DateTimeUtil.getCurrentDateTimeMinusHours(batchPeriod)).willReturn(LocalDateTime.now());
    }

    private void mockDataForSolidarityDailySummary(boolean isMostRecentSolidarityDailySummaryExists) {
        Stock stock = itUtil.createStock();
        stock.setTotalIssuedQuantity(10000L);
        stock.setClosingPrice(100);
        itUtil.updateStock(stock);

        final Solidarity solidarity = itUtil.createSolidarity(stock.getCode());

        if (isMostRecentSolidarityDailySummaryExists) {
            SolidarityDailySummary summary = itUtil.createSolidarityDailySummary();

            solidarity.setMostRecentDailySummary(summary);
            itUtil.updateSolidarity(solidarity);
        }

        User user1 = itUtil.createUser();
        UserHoldingStock userHoldingStock1 = itUtil.createUserHoldingStock(stock.getCode(), user1);
        userHoldingStock1.setQuantity(1L);
        itUtil.updateUserHoldingStock(userHoldingStock1);

        User user2 = itUtil.createUser();
        UserHoldingStock userHoldingStock2 = itUtil.createUserHoldingStock(stock.getCode(), user2);
        userHoldingStock2.setQuantity(2L);
        itUtil.updateUserHoldingStock(userHoldingStock2);

        mockDummyStock(stock);
    }

    private void mockDummyStock(Stock stock) {
        User user = itUtil.createUser();
        UserHoldingStock dummyUserHoldingStock = itUtil.createDummyUserHoldingStock(stock.getCode(), user);
        dummyUserHoldingStock.setQuantity(500L);
        itUtil.updateUserHoldingStock(dummyUserHoldingStock);
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
                    .header("x-api-key", "b0e6f688a1a08462201ef69f4")
            )
            .andExpect(status().isOk())
            .andReturn();

        final ag.act.model.SimpleStringResponse result = objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            ag.act.model.SimpleStringResponse.class
        );

        assertBatchResultResponse(result);

        final List<SolidarityDailySummary> summaries =
            solidarityDailySummaryRepository.findAll().stream().toList();

        final List<Solidarity> solidarities =
            solidarityRepository.findAll().stream().toList();

        assertThat(summaries.size(), is(3));
        assertThat(solidarities.size(), is(2));

        final SolidarityDailySummary existingSummary = summaries.get(0);
        final SolidarityDailySummary summary1 = summaries.get(1);
        final SolidarityDailySummary summary2 = summaries.get(2);

        final Solidarity solidarity1 = solidarities.get(0);
        final Solidarity solidarity2 = solidarities.get(1);


        assertSolidarityDailySummaryCreated(summary1);
        assertThat(solidarity1.getMostRecentDailySummary().getId(), is(summary1.getId()));
        assertThat(solidarity1.getSecondMostRecentDailySummary(), is(nullValue()));

        assertSolidarityDailySummaryCreated(summary2);
        assertThat(solidarity2.getMostRecentDailySummary().getId(), is(summary2.getId()));
        assertThat(solidarity2.getSecondMostRecentDailySummary().getId(), is(existingSummary.getId()));
    }

    private void assertBatchResultResponse(ag.act.model.SimpleStringResponse result) {
        final String expectedResult = "[Batch] %s batch successfully finished. [creation: %s / %s]".formatted(BATCH_NAME, 2, 2);
        assertThat(result.getStatus(), is(expectedResult));

        final BatchLog batchLog = itUtil.findLastBatchLog(BATCH_NAME).orElseThrow(() -> new RuntimeException("[TEST] BatchLog를 찾을 수 없습니다."));
        assertThat(batchLog.getResult(), is(expectedResult));
    }

    private void assertSolidarityDailySummaryCreated(SolidarityDailySummary summary) {
        assertThat(summary.getMemberCount(), is(2));
        assertThat(summary.getStockQuantity(), is(3L));
        assertThat(summary.getMarketValue(), is(100 * 3L));
        assertThat(summary.getStake(), is(0.03));
    }
}
