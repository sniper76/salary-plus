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
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class UpdateSolidarityDailySummariesBatchIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String BATCH_NAME = "UPDATE_SOLIDARITY_DAILY_SUMMARIES";
    private static final String TARGET_API = "/api/batch/update-solidarity-daily-summaries";
    private List<MockedStatic<?>> statics;
    @Autowired
    private SolidarityDailySummaryRepository solidarityDailySummaryRepository;
    @Autowired
    private SolidarityRepository solidarityRepository;
    private Map<String, Integer> request;
    private SolidarityDailySummary originSummary;

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
        originSummary = mockDataForSolidarityDailySummary(true);

        given(DateTimeUtil.getCurrentFormattedDateTime()).willReturn(date);
        given(DateTimeUtil.getCurrentDateTimeMinusHours(batchPeriod)).willReturn(LocalDateTime.now());
    }

    private SolidarityDailySummary mockDataForSolidarityDailySummary(boolean isMostRecentSolidarityDailySummaryExists) {
        Stock stock = itUtil.createStock();
        stock.setTotalIssuedQuantity(10000L);
        stock.setClosingPrice(100);
        itUtil.updateStock(stock);

        final Solidarity solidarity = itUtil.createSolidarity(stock.getCode());

        SolidarityDailySummary summary = null;
        if (isMostRecentSolidarityDailySummaryExists) {
            summary = itUtil.createSolidarityDailySummary();

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

        return summary;
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
        Thread.sleep(1_000);

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
        assertSummariesAndSolidarities();
    }


    private void assertBatchResultResponse(ag.act.model.SimpleStringResponse result) {
        final String expectedResult = "[Batch] %s batch successfully finished. [modification: %s / %s]".formatted(BATCH_NAME, 1, 2);
        assertThat(result.getStatus(), is(expectedResult));

        final BatchLog batchLog = itUtil.findLastBatchLog(BATCH_NAME).orElseThrow(() -> new RuntimeException("[TEST] BatchLog를 찾을 수 없습니다."));
        assertThat(batchLog.getResult(), is(expectedResult));
    }

    private void assertSummariesAndSolidarities() {
        List<SolidarityDailySummary> summaries =
            solidarityDailySummaryRepository.findAll().stream().toList();
        List<Solidarity> solidarities =
            solidarityRepository.findAll().stream().toList();
        final SolidarityDailySummary fetchedSummary = summaries.get(0);

        assertThat(summaries.size(), is(1));
        assertThat(solidarities.size(), is(2));
        assertThat(fetchedSummary.getUpdatedAt(), is(greaterThan(originSummary.getUpdatedAt())));
    }
}

