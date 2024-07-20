package ag.act.api.batch;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.BatchLog;
import ag.act.entity.MyDataSummary;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStockHistoryOnDate;
import ag.act.itutil.ITUtil;
import ag.act.model.BatchRequest;
import ag.act.model.SimpleStringResponse;
import ag.act.util.DateTimeUtil;
import ag.act.util.KoreanDateTimeUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@SuppressWarnings({"MemberName", "FileTabCharacter"})
class UserHoldingStockFirstHistoriesBatchIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/batch/user-holding-stocks-histories";
    private static final String BATCH_NAME = "USER_HOLDING_STOCK_HISTORIES";
    private List<MockedStatic<?>> statics;
    private Map<String, Object> request;
    private List<UserHoldingStockHistoryResult> firstCreateResults;
    private final int BATCH_PERIOD = 1;
    private LocalDate stock1LastDate;
    private LocalDate stock2LastDate;
    private LocalDate stock3LastDate;
    private LocalDate stock4LastDate;
    private String stockCode1;
    private String stockCode2;
    private String stockCode3;
    private String stockCode4;
    private User user;

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
        cleanUpMyDataSummary();
    }

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(DateTimeUtil.class));
        itUtil.init();

        cleanUpMyDataSummary();

        final String date = someString(5);
        given(DateTimeUtil.getCurrentFormattedDateTime()).willReturn(date);
        given(DateTimeUtil.getCurrentDateTimeMinusMinutes(BATCH_PERIOD)).willReturn(LocalDateTime.now());
        given(appRenewalDateProvider.get()).willReturn(APP_RENEWAL_LOCALDATE);

        createUserWithBaseData();
    }

    private void cleanUpMyDataSummary() {
        itUtil.deleteAllMyDataSummary();
    }

    private SimpleStringResponse callApi() throws Exception {
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

    record UserHoldingStockHistoryResult(
        String stockCode,
        long quantity,
        LocalDate date
    ) {
    }

    private void createUserWithBaseData() {
        user = itUtil.createUser();

        stockCode1 = "A1";
        stockCode2 = "B1";
        stockCode3 = "C1";
        stockCode4 = "D1";
        final Stock stock1 = itUtil.createStock(stockCode1);
        final Stock stock2 = itUtil.createStock(stockCode2);
        final Stock stock3 = itUtil.createStock(stockCode3);
        final Stock stock4 = itUtil.createStock(stockCode4);

        /**
         * code	name	quantity	date	code	name	quantity	date
         * A3	액트 테스트3	500	2024.4.25	A3	액트 테스트3	500	2024.4.25
         * A1	액트 테스트1	400	2024.4.24	A1	액트 테스트1	400	2024.4.24
         * S1	SK하이닉스	320	2023.9.4	S1	SK하이닉스	320	2023.9.4
         * S1	SK하이닉스	320	2023.9.1	S1	SK하이닉스	320	2023.9.3
         * K1	삼성전자	    10	2024.4.25   S1	SK하이닉스	320	2023.9.2
         * K1	삼성전자	    20	2024.4.21   S1	SK하이닉스	320	2023.9.1
         *                                  K1	삼성전자	    10	2024.4.25
         *                                  K1	삼성전자	    20	2024.4.24
         *                                  K1	삼성전자	    20	2024.4.23
         *                                  K1	삼성전자	    20	2024.4.22
         *                                  K1	삼성전자	    20	2024.4.21
         */
        stock1LastDate = LocalDate.of(2024, 4, 25);
        stock2LastDate = LocalDate.of(2024, 4, 24);
        stock3LastDate = LocalDate.of(2023, 9, 4);
        stock4LastDate = LocalDate.of(2024, 4, 25);

        final MyDataSummary myDataSummary1 = itUtil.createMyDataSummary(user, List.of(
            new ITUtil.JsonMyDataStockDto(user.getId(), stock1.getCode(), 500L, stock1LastDate),
            new ITUtil.JsonMyDataStockDto(user.getId(), stock2.getCode(), 400L, stock2LastDate),
            new ITUtil.JsonMyDataStockDto(user.getId(), stock3.getCode(), 320L, stock3LastDate),
            new ITUtil.JsonMyDataStockDto(user.getId(), stock3.getCode(), 320L, LocalDate.of(2023, 9, 1)),
            new ITUtil.JsonMyDataStockDto(user.getId(), stock4.getCode(), 10L, stock4LastDate),
            new ITUtil.JsonMyDataStockDto(user.getId(), stock4.getCode(), 20L, LocalDate.of(2024, 4, 21))
        ));

        itUtil.createMyDataSummary(itUtil.createUser(), List.of());

        firstCreateResults = new ArrayList<>();
        firstCreateResults.add(new UserHoldingStockHistoryResult(stock1.getCode(), 500L, stock1LastDate));
        firstCreateResults.add(new UserHoldingStockHistoryResult(stock2.getCode(), 400L, stock2LastDate));
        firstCreateResults.add(new UserHoldingStockHistoryResult(stock3.getCode(), 320L, stock3LastDate));
        firstCreateResults.add(new UserHoldingStockHistoryResult(stock3.getCode(), 320L, LocalDate.of(2023, 9, 3)));
        firstCreateResults.add(new UserHoldingStockHistoryResult(stock3.getCode(), 320L, LocalDate.of(2023, 9, 2)));
        firstCreateResults.add(new UserHoldingStockHistoryResult(stock3.getCode(), 320L, LocalDate.of(2023, 9, 1)));
        firstCreateResults.add(new UserHoldingStockHistoryResult(stock4.getCode(), 10L, stock4LastDate));
        firstCreateResults.add(new UserHoldingStockHistoryResult(stock4.getCode(), 20L, LocalDate.of(2024, 4, 24)));
        firstCreateResults.add(new UserHoldingStockHistoryResult(stock4.getCode(), 20L, LocalDate.of(2024, 4, 23)));
        firstCreateResults.add(new UserHoldingStockHistoryResult(stock4.getCode(), 20L, LocalDate.of(2024, 4, 22)));
        firstCreateResults.add(new UserHoldingStockHistoryResult(stock4.getCode(), 20L, LocalDate.of(2024, 4, 21)));
    }

    @Nested
    class WhenIsFirstCreate {

        @BeforeEach
        void setUp() {
            request = Map.of(
                "batchPeriod", BATCH_PERIOD,
                "periodTimeUnit", BatchRequest.PeriodTimeUnitEnum.MINUTE.name(),
                "isFirstCreateUserHoldingStockHistory", true
            );
        }

        @Test
        void shouldBeSuccess() throws Exception {
            final SimpleStringResponse result = callApi();

            final ZonedDateTime nowInKoreanTime = KoreanDateTimeUtil.getNowInKoreanTime();
            final LocalDate yesterday = nowInKoreanTime.minusDays(1).toLocalDate();
            final long term1 = ChronoUnit.DAYS.between(stock1LastDate, yesterday);
            final long term2 = ChronoUnit.DAYS.between(stock2LastDate, yesterday);
            final long term3 = ChronoUnit.DAYS.between(stock3LastDate, yesterday);
            final long term4 = ChronoUnit.DAYS.between(stock4LastDate, yesterday);
            final long resultSize = term1 + term2 + term3 + term4 + firstCreateResults.size();
            assertResponse(result, resultSize);

            assertResultDate(term1, stockCode1, stock1LastDate);
            assertResultDate(term2, stockCode2, stock2LastDate);
            assertResultDate(term3, stockCode3, stock3LastDate);
            assertResultDate(term4, stockCode4, stock4LastDate);
        }

        private void assertResultDate(long term4, String stockCode, LocalDate stockLastDate) {
            final List<UserHoldingStockHistoryOnDate> databaseHistory4DateList = itUtil.findAllByUserIdAndStockCodeOrderByStockCodeAscDate(
                user.getId(), stockCode
            );
            final List<UserHoldingStockHistoryResult> history4Results = firstCreateResults
                .stream()
                .filter(it -> it.stockCode().equals(stockCode))
                .toList();

            assertThat((long) databaseHistory4DateList.size(), is(term4 + history4Results.size()));
            for (int index = 0; index < databaseHistory4DateList.size(); index++) {
                UserHoldingStockHistoryOnDate stockHistoryOnDate = databaseHistory4DateList.get(index);
                if (stockHistoryOnDate.getDate().isAfter(stockLastDate)
                    || stockHistoryOnDate.getDate().isEqual(stockLastDate)) {
                    final UserHoldingStockHistoryResult maxHistoryDate = Collections.max(history4Results, Comparator.comparing(c -> c.date));

                    assertThat(stockHistoryOnDate.getQuantity(), is(maxHistoryDate.quantity()));
                } else {
                    final UserHoldingStockHistoryResult minHistoryDate = Collections.min(history4Results, Comparator.comparing(c -> c.date));

                    assertThat(stockHistoryOnDate.getQuantity(), is(minHistoryDate.quantity()));
                }
            }
        }
    }
}
