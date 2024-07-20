package ag.act.api.batch;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.BatchLog;
import ag.act.entity.StockDartCorporation;
import ag.act.model.SimpleStringResponse;
import ag.act.util.DateTimeUtil;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;

import java.net.URI;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static ag.act.TestUtil.somePhoneNumber;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someAlphaString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@SuppressWarnings({"AbbreviationAsWordInName", "SpellCheckingInspection", "checkstyle:MemberName", "checkstyle:LineLength"})
class UpdateDartCorporationsBatchIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/batch/dart/update-stock-dark-corporations";
    private static final String BATCH_NAME = "UPDATE_DART_CORPORATIONS";
    private static final int DART_CORPORATION_PAGE_SIZE = 2;
    private static final long VERSION_2 = 2L;
    private List<MockedStatic<?>> statics;
    private static final String DART_COMPANY_RESPONSE_TEMPLATE = """
        {
            "status": "000",
            "message": "정상",
            "corp_code": "%s",
            "corp_name": "엑세스바이오 인코퍼레이션",
            "corp_name_eng": "Access Bio, Inc.",
            "stock_name": "엑세스바이오",
            "stock_code": "950130",
            "ceo_nm": "%s",
            "corp_cls": "%s",
            "jurir_no": "%s",
            "bizr_no": "%s",
            "adres": "%s",
            "hm_url": "%s",
            "ir_url": "%s",
            "phn_no": "%s",
            "fax_no": "%s",
            "induty_code": "%s",
            "est_dt": "%s",
            "acc_mt": "%s"
        }
        """;

    private Map<String, Integer> request;
    @Mock
    private HttpResponse<String> response1;
    @Mock
    private HttpResponse<String> response2;
    private String date;
    private Map<String, HttpResponse<String>> responseMapByCorpCode;
    private List<StockDartCorporation> expectedStockDartCorporations;
    private int totalStockDartCorporationCount;

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @BeforeEach
    void setUp() throws Exception {
        statics = List.of(mockStatic(DateTimeUtil.class));
        itUtil.init();
        itUtil.deleteAllStockDartCorporations();

        final int batchPeriod = 1;
        request = Map.of("batchPeriod", batchPeriod);
        date = someString(5);

        final StockDartCorporation stockDartCorporation1 = createStockDartCorporation();
        final StockDartCorporation stockDartCorporation2 = createStockDartCorporation();

        createStockDartCorporation();
        createStockDartCorporation();

        final String validPhoneOrFaxNumberSuffix = "-1";
        final String validPhoneOrFaxNumberPrefix = "(02)";

        final String expectedPhoneNumber1 = "";
        final String validPhoneNumber = somePhoneNumber();
        final String expectedValidPhoneNumber2 = "%s%s%s".formatted(validPhoneOrFaxNumberPrefix, validPhoneNumber, validPhoneOrFaxNumberSuffix);
        final String expectedInvalidPhoneNumber2 = "   %s   %s  %s  ,        (02)555-6666        ".formatted(validPhoneOrFaxNumberPrefix, validPhoneNumber, validPhoneOrFaxNumberSuffix);
        final String expectedFaxNumber1 = "";
        final String validFaxNumber = somePhoneNumber();
        final String expectedValidFaxNumber2 = "%s%s%s".formatted(validPhoneOrFaxNumberPrefix, validFaxNumber, validPhoneOrFaxNumberSuffix);
        final String expectedInvalidFaxNumber2 = "   %s   %s  %s  ,        (02)666-7777        ".formatted(validPhoneOrFaxNumberPrefix, validFaxNumber, validPhoneOrFaxNumberSuffix);

        stockDartCorporation1.setRepresentativePhoneNumber(expectedPhoneNumber1);
        stockDartCorporation1.setRepresentativeFaxNumber(expectedFaxNumber1);
        stockDartCorporation2.setRepresentativePhoneNumber(expectedValidPhoneNumber2);
        stockDartCorporation2.setRepresentativeFaxNumber(expectedValidFaxNumber2);

        expectedStockDartCorporations = List.of(
            stockDartCorporation1,
            stockDartCorporation2
        );

        responseMapByCorpCode = Map.of(
            stockDartCorporation1.getCorpCode(), response1,
            stockDartCorporation2.getCorpCode(), response2
        );

        given(DateTimeUtil.getCurrentFormattedDateTime()).willReturn(date);
        given(DateTimeUtil.getCurrentDateTimeMinusHours(batchPeriod)).willReturn(LocalDateTime.now());

        mockHttpClientUtil(
            new TestMokingData(stockDartCorporation1, response1),
            new TestMokingData(stockDartCorporation2, response2)
        );

        mockSuccessReponse(response1, stockDartCorporation1, someAlphaString(50), someAlphaString(50));
        mockSuccessReponse(response2, stockDartCorporation2, expectedInvalidPhoneNumber2, expectedInvalidFaxNumber2);

        given(dartCorporationPageableFactory.getPageable())
            .willReturn(PageRequest.of(0, DART_CORPORATION_PAGE_SIZE, Sort.by(Sort.Direction.ASC, "updatedAt")));
    }

    @SuppressWarnings("unchecked")
    private void mockHttpClientUtil(TestMokingData... testMokingDataList) throws Exception {
        BDDMockito.BDDMyOngoingStubbing<URI> uribddMyOngoingStubbing =
            given(dartHttpUriBuilderFactory.createUri(eq(companyApiUrl), any(LinkedMultiValueMap.class)));

        for (TestMokingData testMokingData : testMokingDataList) {
            final URI uri = mock(URI.class);
            uribddMyOngoingStubbing = uribddMyOngoingStubbing.willReturn(uri);
            given(defaultHttpClientUtil.get(eq(uri), anyMap())).willReturn(testMokingData.response());
        }
    }

    private StockDartCorporation createStockDartCorporation() {
        totalStockDartCorporationCount++;
        final StockDartCorporation stockDartCorporation = itUtil.createStockDartCorporation(itUtil.createStock().getCode());
        stockDartCorporation.setCorpCode("UpdateDartCorporationsBatchIntegrationTest" + totalStockDartCorporationCount);
        stockDartCorporation.setUpdatedAt(stockDartCorporation.getUpdatedAt().minusDays(1));
        return itUtil.updateStockDartCorporation(stockDartCorporation);
    }

    private void mockSuccessReponse(HttpResponse<String> response, StockDartCorporation stockDartCorporation, String phoneNumber, String faxNumber) {
        given(response.statusCode()).willReturn(HttpStatus.SC_OK);
        given(response.body()).willReturn(DART_COMPANY_RESPONSE_TEMPLATE.formatted(
            stockDartCorporation.getCorpCode(),
            stockDartCorporation.getCeoName(),
            stockDartCorporation.getCorpClass(),
            stockDartCorporation.getJurisdictionalRegistrationNumber(),
            stockDartCorporation.getBusinessRegistrationNumber(),
            stockDartCorporation.getAddress(),
            stockDartCorporation.getHomepageUrl(),
            stockDartCorporation.getIrUrl(),
            phoneNumber,
            faxNumber,
            stockDartCorporation.getIndustryCode(),
            stockDartCorporation.getEstablishmentDate(),
            stockDartCorporation.getAccountSettlementMonth()));
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

        final SimpleStringResponse result = itUtil.getResult(response, SimpleStringResponse.class);

        assertResponse(result);
    }

    private void assertResponse(SimpleStringResponse result) throws Exception {
        final String expectedResult = "[Batch] %s batch successfully finished. [modification: %s / %s] on %s"
            .formatted(BATCH_NAME, responseMapByCorpCode.size(), responseMapByCorpCode.size(), date);

        assertThat(result.getStatus(), is(expectedResult));
        assertBatchLog(expectedResult);
        assertStockDartCorporations();

        then(defaultHttpClientUtil).should(times(responseMapByCorpCode.size())).get(any(URI.class), anyMap());
    }

    private void assertStockDartCorporations() {
        final List<StockDartCorporation> stockDartCorporations = itUtil.findAllStockDartCorporations();

        assertThat(stockDartCorporations.size(), greaterThanOrEqualTo(totalStockDartCorporationCount));

        expectedStockDartCorporations.forEach(expected -> {
            final StockDartCorporation actual = findStockDartCorporation(stockDartCorporations, expected.getCorpCode()).orElseThrow();
            assertThat(actual.getCeoName(), is(expected.getCeoName()));
            assertThat(actual.getCorpClass(), is(expected.getCorpClass()));
            assertThat(actual.getJurisdictionalRegistrationNumber(), is(expected.getJurisdictionalRegistrationNumber()));
            assertThat(actual.getBusinessRegistrationNumber(), is(expected.getBusinessRegistrationNumber()));
            assertThat(actual.getAddress(), is(expected.getAddress()));
            assertThat(actual.getHomepageUrl(), is(expected.getHomepageUrl()));
            assertThat(actual.getIrUrl(), is(expected.getIrUrl()));
            assertThat(actual.getRepresentativePhoneNumber(), is(expected.getRepresentativePhoneNumber()));
            assertThat(actual.getRepresentativeFaxNumber(), is(expected.getRepresentativeFaxNumber()));
            assertThat(actual.getIndustryCode(), is(expected.getIndustryCode()));
            assertThat(actual.getEstablishmentDate(), is(expected.getEstablishmentDate()));
            assertThat(actual.getAccountSettlementMonth(), is(expected.getAccountSettlementMonth()));
            assertThat(actual.getVersion(), is(VERSION_2));
        });
    }

    private Optional<StockDartCorporation> findStockDartCorporation(List<StockDartCorporation> stockDartCorporations, String corpCode) {
        return stockDartCorporations.stream().filter(it -> corpCode.equals(it.getCorpCode())).findFirst();
    }

    private void assertBatchLog(String expectedResult) {
        final BatchLog batchLog = itUtil.findLastBatchLog(BATCH_NAME)
            .orElseThrow(() -> new RuntimeException("[TEST] BatchLog를 찾을 수 없습니다."));
        assertThat(batchLog.getResult(), is(expectedResult));
    }

    record TestMokingData(StockDartCorporation stockDartCorporation, HttpResponse<String> response) {
    }
}
