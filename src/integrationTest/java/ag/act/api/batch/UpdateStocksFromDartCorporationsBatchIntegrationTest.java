package ag.act.api.batch;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.BatchLog;
import ag.act.entity.Stock;
import ag.act.entity.StockDartCorporation;
import ag.act.util.DateTimeUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@SuppressWarnings({"AbbreviationAsWordInName", "SpellCheckingInspection", "checkstyle:MemberName", "checkstyle:LineLength"})
class UpdateStocksFromDartCorporationsBatchIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/batch/update-stocks-from-dart-corporations";
    private static final String BATCH_NAME = "UPDATE_STOCKS_FROM_DART_CORPORATIONS";
    private List<MockedStatic<?>> statics;

    private Map<String, Integer> request;
    private String date;
    private List<StockDartCorporation> expectedStockDartCorporations;

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @BeforeEach
    void setUp() throws Exception {
        statics = List.of(mockStatic(DateTimeUtil.class));
        itUtil.init();

        final int batchPeriod = 1;
        request = Map.of("batchPeriod", batchPeriod);
        date = someString(5);

        given(DateTimeUtil.getCurrentFormattedDateTime()).willReturn(date);
        given(DateTimeUtil.getCurrentDateTimeMinusHours(batchPeriod)).willReturn(LocalDateTime.now());

        itUtil.createStockDartCorporation(itUtil.createStock().getCode());
        itUtil.createStockDartCorporation(itUtil.createStock().getCode());
        expectedStockDartCorporations = itUtil.getAllDartCorporationsWithStock();
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

        assertResponse(result);
    }

    private void assertResponse(ag.act.model.SimpleStringResponse result) {
        final String expectedResult = "[Batch] %s batch successfully finished. [modification: %s / %s] on %s"
            .formatted(BATCH_NAME, expectedStockDartCorporations.size(), expectedStockDartCorporations.size(), date);

        assertThat(result.getStatus(), is(expectedResult));
        assertBatchLog(expectedResult);
        assertStockDartCorporations();
    }

    private void assertStockDartCorporations() {
        final List<Stock> allStocks = itUtil.findAllStocks();

        expectedStockDartCorporations.forEach(expected -> {
            final Optional<Stock> actual = findStock(allStocks, expected.getStockCode());
            assertThat(actual.isPresent(), is(true));

            final Stock actualStock = actual.get();
            assertThat(actualStock.getRepresentativePhoneNumber(), is(expected.getRepresentativePhoneNumber()));
            assertThat(actualStock.getAddress(), is(expected.getAddress()));
        });
    }

    private Optional<Stock> findStock(List<Stock> stocks, String stockCode) {
        return stocks.stream().filter(it -> stockCode.equals(it.getCode())).findFirst();
    }

    private void assertBatchLog(String expectedResult) {
        final BatchLog batchLog = itUtil.findLastBatchLog(BATCH_NAME)
            .orElseThrow(() -> new RuntimeException("[TEST] BatchLog를 찾을 수 없습니다."));
        assertThat(batchLog.getResult(), is(expectedResult));
    }
}
