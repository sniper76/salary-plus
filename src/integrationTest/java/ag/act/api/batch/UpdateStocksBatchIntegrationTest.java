package ag.act.api.batch;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.dto.krx.StkItemPriceDto;
import ag.act.dto.krx.StockBaseInfoDto;
import ag.act.dto.krx.StockItemDto;
import ag.act.dto.krx.StockPriceInfoDto;
import ag.act.entity.BatchLog;
import ag.act.entity.Board;
import ag.act.entity.PrivateStock;
import ag.act.entity.Stock;
import ag.act.entity.TestStock;
import ag.act.enums.BoardCategory;
import ag.act.enums.SlackChannel;
import ag.act.util.DateTimeUtil;
import ag.act.util.KoreanDateTimeUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static ag.act.enums.KrxServiceType.KNX_BYDD_TRD;
import static ag.act.enums.KrxServiceType.KNX_ISU_BASE_INFO;
import static ag.act.enums.KrxServiceType.KSQ_BYDD_TRD;
import static ag.act.enums.KrxServiceType.KSQ_ISU_BASE_INFO;
import static ag.act.enums.KrxServiceType.STK_BYDD_TRD;
import static ag.act.enums.KrxServiceType.STK_ISU_BASE_INFO;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomLongs.someLongBetween;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@SuppressWarnings({"AbbreviationAsWordInName", "SpellCheckingInspection", "checkstyle:MemberName", "checkstyle:LineLength"})
class UpdateStocksBatchIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/batch/update-stocks";
    private static final String BATCH_NAME = "UPDATE_STOCKS";
    private static final String STK_BYDD_TRD_CODE = "01_STK_BYDD_TRD_CODE";
    private static final String KSQ_BYDD_TRD_CODE = "02_KSQ_BYDD_TRD_CODE";
    private static final String KNX_BYDD_TRD_CODE = "03_KNX_BYDD_TRD_CODE";
    private static final int batchPeriod = 1;
    private static final String MESSAGE_TEMPLATE = "[%s(%s)] 종목의 주식 발행수가 과도하게 변경되었습니다.\n변경전 주식 발행수: %s\n변경후 주식 발행수: %s\n확인이 필요합니다.";

    private List<MockedStatic<?>> statics;
    @Mock
    private StockBaseInfoDto stockBaseInfo_STK_ISU_BASE_INFO;
    @Mock
    private StockBaseInfoDto stockBaseInfo_KSQ_ISU_BASE_INFO;
    @Mock
    private StockBaseInfoDto stockBaseInfo_KNX_ISU_BASE_INFO;
    @Mock
    private StockItemDto stockItem_STK_BYDD_TRD;
    @Mock
    private StockItemDto stockItem_KSQ_BYDD_TRD;
    @Mock
    private StockItemDto stockItem_KNX_BYDD_TRD;
    @Mock
    private StockPriceInfoDto stockPrice_STK_BYDD_TRD;
    @Mock
    private StockPriceInfoDto stockPrice_KSQ_BYDD_TRD;
    @Mock
    private StockPriceInfoDto stockPrice_KNX_BYDD_TRD;
    @Mock
    private StkItemPriceDto stkItemPrice_STK_ISU_BASE_INFO;
    @Mock
    private StkItemPriceDto stkItemPrice_KSQ_ISU_BASE_INFO;
    @Mock
    private StkItemPriceDto stkItemPrice_KNX_ISU_BASE_INFO;

    private Map<String, Integer> request;
    private String date;
    private Map<String, TestStock> testStockMap;
    private Map<String, PrivateStock> privateStockMap;
    private List<TestKrxStock> testKrxStocks;
    private int createCount;
    private int updateCount;
    private int yearOfReferenceDate;
    private LocalDate todayDate;
    private LocalDate referenceDate;
    private Stock existingStock;

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
        itUtil.deleteBatchLog(BATCH_NAME);
        cleanTestStocks();
        cleanPrivateStocks();
    }

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(DateTimeUtil.class), mockStatic(KoreanDateTimeUtil.class));
        itUtil.init();
        dbCleaner.clean();

        request = Map.of("batchPeriod", batchPeriod);
        date = someString(5);

        given(DateTimeUtil.getLatestStockMarketClosingDate()).willReturn(date);
        given(serverEnvironment.isProd()).willReturn(Boolean.TRUE);
    }

    private void mockTestData() {
        mockKrxStocks();
        mockTestStocks();
        modkPrivateStocks();

        existingStock = createStockForUpdateLogicTest();

        int totalCount = testKrxStocks.size() + privateStockMap.size() + testStockMap.size();
        updateCount = 1;
        createCount = totalCount - updateCount;
    }

    private Stock createStockForUpdateLogicTest() {
        return itUtil.createStock(STK_BYDD_TRD_CODE);
    }

    private void modkPrivateStocks() {
        PrivateStock privateStock1 = itUtil.createPrivateStock();
        PrivateStock privateStock2 = itUtil.createPrivateStock();
        privateStockMap = Map.of(
            privateStock1.getCode(), privateStock1,
            privateStock2.getCode(), privateStock2
        );
    }

    private void mockTestStocks() {
        TestStock testStock1 = createTestStock();
        TestStock testStock2 = createTestStock();
        testStockMap = Map.of(
            testStock1.getCode(), testStock1,
            testStock2.getCode(), testStock2
        );
    }

    private TestStock createTestStock() {
        final TestStock testStock = itUtil.createTestStock();
        alreadyHaveRegularGeneralMeetingStockReferenceDate(testStock.getCode());
        return testStock;
    }

    private void alreadyHaveRegularGeneralMeetingStockReferenceDate(String code) {
        itUtil.createStockReferenceDate(code, referenceDate);
    }

    private void mockKrxStocks() {
        testKrxStocks = List.of(
            new TestKrxStock(STK_BYDD_TRD_CODE, someIntegerBetween(100, 10000), someLongBetween(1L, 1000L)),
            new TestKrxStock(KSQ_BYDD_TRD_CODE, someIntegerBetween(100, 10000), someLongBetween(1L, 1000L)),
            new TestKrxStock(KNX_BYDD_TRD_CODE, someIntegerBetween(100, 10000), someLongBetween(1L, 1000L))
        );

        given(krxHttpClientUtil.callApi(STK_ISU_BASE_INFO, date, StockBaseInfoDto.class)).willReturn(stockBaseInfo_STK_ISU_BASE_INFO);
        given(krxHttpClientUtil.callApi(KSQ_ISU_BASE_INFO, date, StockBaseInfoDto.class)).willReturn(stockBaseInfo_KSQ_ISU_BASE_INFO);
        given(krxHttpClientUtil.callApi(KNX_ISU_BASE_INFO, date, StockBaseInfoDto.class)).willReturn(stockBaseInfo_KNX_ISU_BASE_INFO);
        given(krxHttpClientUtil.callApi(STK_BYDD_TRD, date, StockPriceInfoDto.class)).willReturn(stockPrice_STK_BYDD_TRD);
        given(krxHttpClientUtil.callApi(KSQ_BYDD_TRD, date, StockPriceInfoDto.class)).willReturn(stockPrice_KSQ_BYDD_TRD);
        given(krxHttpClientUtil.callApi(KNX_BYDD_TRD, date, StockPriceInfoDto.class)).willReturn(stockPrice_KNX_BYDD_TRD);

        mockStockBaseInfoDto(stockBaseInfo_STK_ISU_BASE_INFO, stockItem_STK_BYDD_TRD, STK_BYDD_TRD_CODE, testKrxStocks.get(0).totalIssuedQuantity());
        mockStockBaseInfoDto(stockBaseInfo_KSQ_ISU_BASE_INFO, stockItem_KSQ_BYDD_TRD, KSQ_BYDD_TRD_CODE, testKrxStocks.get(1).totalIssuedQuantity());
        mockStockBaseInfoDto(stockBaseInfo_KNX_ISU_BASE_INFO, stockItem_KNX_BYDD_TRD, KNX_BYDD_TRD_CODE, testKrxStocks.get(2).totalIssuedQuantity());
        mockStockPriceInfoDto(stockPrice_STK_BYDD_TRD, stkItemPrice_KNX_ISU_BASE_INFO, STK_BYDD_TRD_CODE, testKrxStocks.get(0).closingPrice(), testKrxStocks.get(0).totalIssuedQuantity());
        mockStockPriceInfoDto(stockPrice_KSQ_BYDD_TRD, stkItemPrice_STK_ISU_BASE_INFO, KSQ_BYDD_TRD_CODE, testKrxStocks.get(1).closingPrice(), testKrxStocks.get(1).totalIssuedQuantity());
        mockStockPriceInfoDto(stockPrice_KNX_BYDD_TRD, stkItemPrice_KSQ_ISU_BASE_INFO, KNX_BYDD_TRD_CODE, testKrxStocks.get(2).closingPrice(), testKrxStocks.get(2).totalIssuedQuantity());
    }

    private void mockStockBaseInfoDto(StockBaseInfoDto stockBaseInfoDto, StockItemDto stockItemDto, String code, Long totalIssuedQuantity) {
        given(stockItemDto.getISU_CD()).willReturn(code);
        given(stockItemDto.getISU_SRT_CD()).willReturn(code);
        given(stockItemDto.getISU_NM()).willReturn("한글 종목명");
        given(stockItemDto.getISU_ABBRV()).willReturn("한글 종목약명");
        given(stockItemDto.getISU_ENG_NM()).willReturn("영문 종목명");
        given(stockItemDto.getLIST_DD()).willReturn("상장일");
        given(stockItemDto.getMKT_TP_NM()).willReturn("시장구분");
        given(stockItemDto.getSECUGRP_NM()).willReturn("증권구분");
        given(stockItemDto.getSECT_TP_NM()).willReturn("소속부");
        given(stockItemDto.getKIND_STKCERT_TP_NM()).willReturn("주식종류");
        given(stockItemDto.getPARVAL()).willReturn("액면가");
        given(stockItemDto.getLIST_SHRS()).willReturn(totalIssuedQuantity.toString());

        given(stockBaseInfoDto.getOutBlock_1()).willReturn(List.of(stockItemDto));
    }

    private void mockStockPriceInfoDto(StockPriceInfoDto stockPriceInfoDto, StkItemPriceDto stkItemPriceDto, String code, Integer closingPrice, Long totalIssuedQuantity) {
        given(stkItemPriceDto.getBAS_DD()).willReturn("기준일자");
        given(stkItemPriceDto.getISU_CD()).willReturn(code);
        given(stkItemPriceDto.getISU_NM()).willReturn("한글 종목명");
        given(stkItemPriceDto.getMKT_NM()).willReturn("시장구분");
        given(stkItemPriceDto.getSECT_TP_NM()).willReturn("소속부");
        given(stkItemPriceDto.getTDD_CLSPRC()).willReturn(closingPrice.toString());
        given(stkItemPriceDto.getCMPPREVDD_PRC()).willReturn("대비");
        given(stkItemPriceDto.getFLUC_RT()).willReturn("등략률");
        given(stkItemPriceDto.getTDD_OPNPRC()).willReturn("시가");
        given(stkItemPriceDto.getTDD_HGPRC()).willReturn("고가");
        given(stkItemPriceDto.getTDD_LWPRC()).willReturn("저가");
        given(stkItemPriceDto.getACC_TRDVOL()).willReturn("거래량");
        given(stkItemPriceDto.getACC_TRDVAL()).willReturn("거래대금");
        given(stkItemPriceDto.getMKTCAP()).willReturn("시가총액");
        given(stkItemPriceDto.getLIST_SHRS()).willReturn(totalIssuedQuantity.toString());

        given(stockPriceInfoDto.getOutBlock_1()).willReturn(List.of(stkItemPriceDto));
    }

    record TestKrxStock(String code, Integer closingPrice, Long totalIssuedQuantity) {

    }
    // end of preparing test data

    // start of doing test
    @Nested
    class WhenBatchTargetDateBetweenJanAndMarch {

        // set batch target time to date between January and March
        // and reference date must be last day of last year
        @BeforeEach
        void setUp() {
            final int someMonthBeforeApril = someIntegerBetween(1, 3);
            final LocalDateTime batchTargetStartTime = LocalDateTime.now().withMonth(someMonthBeforeApril);

            todayDate = batchTargetStartTime.toLocalDate();
            referenceDate = todayDate.minusYears(1).withMonth(12).withDayOfMonth(31);
            yearOfReferenceDate = referenceDate.getYear();

            given(DateTimeUtil.getCurrentDateTimeMinusHours(batchPeriod)).willReturn(batchTargetStartTime);
            given(DateTimeUtil.isMonthBefore(todayDate, Month.APRIL)).willReturn(true);

            given(KoreanDateTimeUtil.getTodayLocalDate()).willReturn(todayDate);
            given(KoreanDateTimeUtil.getEndOfLastYearLocalDate()).willReturn(referenceDate);

            mockTestData();
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final ag.act.model.SimpleStringResponse result = callApiAndGetResult();

            assertResponse(result);
        }
    }

    @Nested
    class WhenBatchTargetDateBetweenAprilAndDecember {

        // set batch target time to date between April And December
        // and reference date must be last day of this year
        @BeforeEach
        void setUp() {
            final int someMonthAfterApril = someIntegerBetween(4, 12);
            final LocalDateTime batchTargetStartTime = LocalDateTime.now().withMonth(someMonthAfterApril);

            todayDate = batchTargetStartTime.toLocalDate();
            referenceDate = todayDate.withMonth(12).withDayOfMonth(31);
            yearOfReferenceDate = referenceDate.getYear();

            given(DateTimeUtil.getCurrentDateTimeMinusHours(batchPeriod)).willReturn(batchTargetStartTime);
            given(DateTimeUtil.isMonthBefore(todayDate, Month.APRIL)).willReturn(false);

            given(KoreanDateTimeUtil.getTodayLocalDate()).willReturn(todayDate);
            given(KoreanDateTimeUtil.getEndOfThisYearLocalDate()).willReturn(referenceDate);

            mockTestData();
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final ag.act.model.SimpleStringResponse result = callApiAndGetResult();

            assertResponse(result);
        }
    }


    private ag.act.model.SimpleStringResponse callApiAndGetResult() throws Exception {
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
            ag.act.model.SimpleStringResponse.class
        );
    }

    private void assertResponse(ag.act.model.SimpleStringResponse result) {
        final String expectedResult = "[Batch] %s batch successfully finished. [creation: %s, modification: %s / %s] on %s"
            .formatted(BATCH_NAME, createCount, updateCount, (createCount + updateCount), date);

        assertThat(result.getStatus(), is(expectedResult));

        assertBatchLog(expectedResult);
        assertAllStocks();
        assertAllBoards();
    }

    private void assertAllBoards() {
        itUtil.findAllStocksWithoutGlobalBoardStock().forEach(this::assertStockHasAllBoardsInDatabase);
    }

    private void assertStockHasAllBoardsInDatabase(Stock stock) {
        Arrays.stream(BoardCategory.activeBoardCategoriesForStocks())
            .forEach(boardCategory -> {
                final Optional<Board> board = itUtil.findBoard(stock.getCode(), boardCategory);
                assertThat(board.isPresent(), is(true));
            });
    }

    private void assertBatchLog(String expectedResult) {
        final BatchLog batchLog = itUtil.findLastBatchLog(BATCH_NAME)
            .orElseThrow(() -> new RuntimeException("[TEST] BatchLog를 찾을 수 없습니다."));
        assertThat(batchLog.getResult(), is(expectedResult));
    }

    private void assertAllStocks() {
        final List<Stock> allStocks = getSortedAllStocks();

        final int krxStocksSize = testKrxStocks.size();
        final int testStocksSize = testStockMap.size();
        final int privateStocksSize = privateStockMap.size();

        final int allStocksSize = krxStocksSize + testStocksSize + privateStocksSize;

        assertThat(allStocks.size(), is(allStocksSize));

        assertKrxStocks(allStocks);
        assretTestStocks(allStocks);
        assretPrivateStocks(allStocks);
        assertSlackMessageSender();
    }

    private void assertSlackMessageSender() {
        then(slackMessageSender).should().sendSlackMessage(
            MESSAGE_TEMPLATE.formatted(
                existingStock.getName(),
                existingStock.getCode(),
                existingStock.getTotalIssuedQuantity(),
                testKrxStocks.get(0).totalIssuedQuantity()
            ),
            SlackChannel.ACT_STOCK_CHANGE_ALERT
        );
    }

    private void assertKrxStocks(List<Stock> allStocks) {
        for (int i = 0; i < testKrxStocks.size(); i++) {
            assertKrxStock(allStocks.get(i), testKrxStocks.get(i));
        }
    }

    private void assretPrivateStocks(List<Stock> allStocks) {
        final List<Stock> privateStocks = getPrivateStocks(allStocks);
        assertThat(privateStocks.size(), is(privateStockMap.size()));
        privateStocks.forEach(this::assretPrivateStock);
    }

    private void assretTestStocks(List<Stock> allStocks) {
        final List<Stock> testStocks = getTestStocks(allStocks);
        assertThat(testStocks.size(), is(testStockMap.size()));
        testStocks.forEach(this::assretTestStock);
    }

    private void assretPrivateStock(Stock stock) {
        final PrivateStock privateStock = privateStockMap.get(stock.getCode());
        final String code = stock.getCode();

        assertThat(code, is(privateStock.getCode()));
        assertThat(stock.getName(), is(privateStock.getName()));
        assertThat(stock.getMarketType(), is(privateStock.getMarketType()));
        assertThat(stock.getStandardCode(), is(privateStock.getStandardCode()));
        assertThat(stock.getFullName(), is(privateStock.getName()));
        assertThat(stock.getStockType(), is(privateStock.getStockType()));
        assertThat(stock.getClosingPrice(), is(privateStock.getClosingPrice()));
        assertThat(stock.getTotalIssuedQuantity(), is(privateStock.getTotalIssuedQuantity()));
        assertThat(stock.getStatus(), is(privateStock.getStatus()));

        assertThat(itUtil.findSolidarity(code).isPresent(), is(true));
        assertThat(itUtil.findRegularGeneralMeetingStockReferenceDate(code, referenceDate).isPresent(), is(true));
        assertThat(itUtil.findRegularGeneralMeetingStockReferenceDate(code, referenceDate).get().getReferenceDate().getYear(), is(yearOfReferenceDate));
    }

    private void assretTestStock(Stock stock) {
        final TestStock testStock = testStockMap.get(stock.getCode());
        final String code = stock.getCode();

        assertThat(code, is(testStock.getCode()));
        assertThat(stock.getName(), is(testStock.getName()));
        assertThat(stock.getMarketType(), is("CONDUIT"));
        assertThat(stock.getStandardCode(), is("ACT" + testStock.getCode() + "999"));
        assertThat(stock.getFullName(), is(testStock.getName() + "액트주"));
        assertThat(stock.getStockType(), is("액트주"));
        assertThat(stock.getClosingPrice(), is(10_000));
        assertThat(stock.getTotalIssuedQuantity(), is(1_000_000L));

        assertThat(itUtil.findSolidarity(code).isPresent(), is(true));
        assertThat(itUtil.findRegularGeneralMeetingStockReferenceDate(code, referenceDate).isPresent(), is(true));
        assertThat(itUtil.findRegularGeneralMeetingStockReferenceDate(code, referenceDate).get().getReferenceDate().getYear(), is(yearOfReferenceDate));
    }

    private void assertKrxStock(Stock stock, TestKrxStock testKrxStock) {
        assertThat(stock.getCode(), is(testKrxStock.code()));
        assertThat(stock.getName(), is("한글 종목약명"));
        assertThat(stock.getMarketType(), is("시장구분"));
        assertThat(stock.getStandardCode(), is(testKrxStock.code()));
        assertThat(stock.getFullName(), is("한글 종목명"));
        assertThat(stock.getStockType(), is("주식종류"));
        assertThat(stock.getClosingPrice(), is(testKrxStock.closingPrice()));
        assertThat(stock.getTotalIssuedQuantity(), is(testKrxStock.totalIssuedQuantity()));

        assertThat(itUtil.findSolidarity(testKrxStock.code()).isPresent(), is(true));
        assertThat(itUtil.findRegularGeneralMeetingStockReferenceDate(testKrxStock.code(), referenceDate).isPresent(), is(true));
        assertThat(itUtil.findRegularGeneralMeetingStockReferenceDate(testKrxStock.code(), referenceDate).get().getReferenceDate().getYear(), is(yearOfReferenceDate));
    }

    private List<Stock> getPrivateStocks(List<Stock> allStocks) {
        return allStocks.stream()
            .filter(stock -> stock.getCode().startsWith("PRIVATE_STOCK_"))
            .toList();
    }

    private List<Stock> getTestStocks(List<Stock> allStocks) {
        return allStocks.stream()
            .filter(stock -> stock.getCode().startsWith("TEST_STOCK_"))
            .toList();
    }

    private List<Stock> getSortedAllStocks() {
        return itUtil.findAllStocksWithoutGlobalBoardStock().stream().sorted(Comparator.comparing(Stock::getCreatedAt)).toList();
    }

    private void cleanTestStocks() {
        itUtil.deleteTestStocks(List.copyOf(testStockMap.keySet()));
    }

    private void cleanPrivateStocks() {
        itUtil.deletePrivateStocks(List.copyOf(privateStockMap.keySet()));
    }
}
