package ag.act.api.batch;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.BatchLog;
import ag.act.entity.StockDartCorporation;
import ag.act.util.DateTimeUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.MultiValueMap;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@SuppressWarnings({"AbbreviationAsWordInName", "SpellCheckingInspection", "checkstyle:MemberName", "checkstyle:LineLength"})
class UpdateDartCorpCodesBatchIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/batch/dart/update-corp-codes";
    private static final String BATCH_NAME = "UPDATE_DART_CORP_CODE";
    private List<MockedStatic<?>> statics;

    @Value("classpath:/dart/dart.CORPCODE.xml.zip")
    private Resource corpCodeZipFile;
    private Map<String, Integer> request;
    @Mock
    private HttpResponse<InputStream> response;
    private String date;
    private static final int totalCount = 3; // ZIP 파일 안에 XML에 3개가 들어 있다
    private List<StockDartCorporation> expectedStockDartCorporations;

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() throws Exception {
        statics = List.of(mockStatic(DateTimeUtil.class));
        itUtil.init();
        itUtil.deleteAllStockDartCorporations();

        final int batchPeriod = 1;
        request = Map.of("batchPeriod", batchPeriod);
        date = someString(5);
        final URI uri = mock(URI.class);

        given(DateTimeUtil.getCurrentFormattedDateTime()).willReturn(date);
        given(DateTimeUtil.getCurrentDateTimeMinusHours(batchPeriod)).willReturn(LocalDateTime.now());
        given(dartHttpUriBuilderFactory.createUri(eq(corpCodeApiUrl), any(MultiValueMap.class))).willReturn(uri);
        given(defaultHttpClientUtil.getInputStream(eq(uri), anyMap())).willReturn(response);
        given(response.body()).willAnswer(invocation -> corpCodeZipFile.getInputStream());

        alreadyExistingStockDartCorporation();

        expectedStockDartCorporations = List.of(
            createStockDartCorporationObject("00664048", "우리넷", "115440", "20230609"),
            createStockDartCorporationObject("01091382", "세토피아", "222810", "20230609"),
            createStockDartCorporationObject("00105138", "파라텍", "033540", "20230612")
        );
    }

    private StockDartCorporation createStockDartCorporationObject(String corpCode, String corpName, String stockCode, String modifyDate) {
        final StockDartCorporation stockDartCorporation = new StockDartCorporation();
        stockDartCorporation.setCorpCode(corpCode);
        stockDartCorporation.setCorpName(corpName);
        stockDartCorporation.setStockCode(stockCode);
        stockDartCorporation.setModifyDate(modifyDate);
        stockDartCorporation.setStatus(ag.act.model.Status.ACTIVE);

        return stockDartCorporation;
    }

    private void alreadyExistingStockDartCorporation() {
        itUtil.createStockDartCorporation("00664048", "우리넷", "115440", "20230609");
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

        assertResponse(result);
    }

    private void assertResponse(ag.act.model.SimpleStringResponse result) {
        final String expectedResult = "[Batch] %s batch successfully finished. [modification: %s / %s] on %s"
            .formatted(BATCH_NAME, totalCount, totalCount, date);

        assertThat(result.getStatus(), is(expectedResult));
        assertBatchLog(expectedResult);
        assertStockDartCorporations();
    }

    private void assertStockDartCorporations() {
        final List<StockDartCorporation> stockDartCorporations = itUtil.findAllStockDartCorporations();

        assertThat(stockDartCorporations.size(), greaterThanOrEqualTo(totalCount));

        expectedStockDartCorporations.forEach(expected -> {
            final Optional<StockDartCorporation> actual = findStockDartCorporation(stockDartCorporations, expected.getCorpCode());
            assertThat(actual.isPresent(), is(true));

            final StockDartCorporation actualStockDartCorporation = actual.get();
            assertStockDartCorporation(expected, actualStockDartCorporation);
        });
    }

    private void assertStockDartCorporation(StockDartCorporation expected, StockDartCorporation actualStockDartCorporation) {
        assertThat(actualStockDartCorporation.getCorpCode(), is(expected.getCorpCode()));
        assertThat(actualStockDartCorporation.getCorpName(), is(expected.getCorpName()));
        assertThat(actualStockDartCorporation.getStockCode(), is(expected.getStockCode()));
        assertThat(actualStockDartCorporation.getModifyDate(), is(expected.getModifyDate()));
    }

    private Optional<StockDartCorporation> findStockDartCorporation(List<StockDartCorporation> stockDartCorporations, String corpCode) {
        return stockDartCorporations.stream().filter(it -> corpCode.equals(it.getCorpCode())).findFirst();
    }

    private void assertBatchLog(String expectedResult) {
        final BatchLog batchLog = itUtil.findLastBatchLog(BATCH_NAME)
            .orElseThrow(() -> new RuntimeException("[TEST] BatchLog를 찾을 수 없습니다."));
        assertThat(batchLog.getResult(), is(expectedResult));
    }

}
