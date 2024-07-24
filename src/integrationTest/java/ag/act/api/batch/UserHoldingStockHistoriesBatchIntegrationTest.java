package ag.act.api.batch;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.BatchLog;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.model.BatchRequest;
import ag.act.model.SimpleStringResponse;
import ag.act.util.DateTimeUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@SuppressWarnings("MemberName")
class UserHoldingStockHistoriesBatchIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/batch/user-holding-stocks-histories";
    private static final String BATCH_NAME = "USER_HOLDING_STOCK_HISTORIES";
    private List<MockedStatic<?>> statics;
    private Map<String, Object> request;
    private final int BATCH_PERIOD = 1;

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
        cleanUpUserHoldingStock();
    }

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(DateTimeUtil.class));
        itUtil.init();
        cleanUpUserHoldingStock();

        final String date = someString(5);
        given(DateTimeUtil.getCurrentFormattedDateTime()).willReturn(date);
        given(DateTimeUtil.getCurrentDateTimeMinusMinutes(BATCH_PERIOD)).willReturn(LocalDateTime.now());
    }

    private void cleanUpUserHoldingStock() {
        itUtil.deleteAllUserHoldingStock();
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

    private void assertResponse(SimpleStringResponse result, long resultSize) {
        final String expectedResult = "[Batch] %s batch successfully finished. [creation: %s]".formatted(
            BATCH_NAME, resultSize
        );
        assertThat(result.getStatus(), is(expectedResult));

        final BatchLog batchLog = itUtil.findLastBatchLog(BATCH_NAME).orElseThrow();
        assertThat(batchLog.getResult(), is(expectedResult));
    }

    @Nested
    class WhenDailyCreate {
        private List<UserHoldingStock> userHoldingStocks;

        @BeforeEach
        void setUp() {
            request = Map.of(
                "batchPeriod", BATCH_PERIOD,
                "periodTimeUnit", BatchRequest.PeriodTimeUnitEnum.MINUTE.name()
            );
            userHoldingStocks = new ArrayList<>();

            final User user1 = itUtil.createUser();
            final User user2 = itUtil.createUser();

            final Stock stock1 = itUtil.createStock(someStockCode());
            final Stock stock2 = itUtil.createStock(someStockCode());

            userHoldingStocks.add(itUtil.createUserHoldingStock(stock1.getCode(), user1));
            userHoldingStocks.add(itUtil.createUserHoldingStock(stock2.getCode(), user1));

            userHoldingStocks.add(itUtil.createUserHoldingStock(stock1.getCode(), user2));
            userHoldingStocks.add(itUtil.createUserHoldingStock(stock2.getCode(), user2));
        }

        @Test
        void shouldBeSuccess() throws Exception {
            final SimpleStringResponse result = callApi();

            assertResponse(result, userHoldingStocks.size());
        }
    }
}
