package ag.act.api.admin.stock;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.SolidarityDailyStatistics;
import ag.act.entity.User;
import ag.act.enums.admin.StockStatisticsPeriodType;
import ag.act.enums.admin.StockStatisticsType;
import ag.act.model.GetStockStatisticsDataResponse;
import ag.act.model.StockStatisticsResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import static ag.act.TestUtil.someStockCode;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

public class GetStockDailyStatisticsApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/stocks/{code}/statistics/{type}/{periodType}";
    public static final int DAYS_20 = 20;
    private static final DateTimeFormatter RESPONSE_FORMATTER_YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter RESPONSE_FORMATTER_YYYY_MM = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final DateTimeFormatter REQUEST_FORMATTER_YYYY_MM = DateTimeFormatter.ofPattern("yyyyMM");

    private String jwt;
    private List<StockData> stockDataList;
    private List<SolidarityDailyStatistics> solidarityDailyStatistics;
    private String searchType;
    private String periodType;
    private String period;
    private StockData selectedStockData;
    private List<SolidarityDailyStatistics> selectedStatistics;
    private DateTimeFormatter responseDateFormatter;

    @AfterEach
    void tearDown() {
        cleanUpExistingSolidarityDailyStatistics();
    }

    @BeforeEach
    void setUp() {
        itUtil.init();
        final User adminUser = itUtil.createAdminUser();
        jwt = itUtil.createJwt(adminUser.getId());

        cleanUpExistingSolidarityDailyStatistics();

        // 작년 12월 1일
        final LocalDate firstDayOfLastYear = LocalDate.now().minusYears(1).withMonth(12).withDayOfMonth(1);

        stockDataList = List.of(
            new StockData(someStockCode(), someLong(), firstDayOfLastYear),
            new StockData(someStockCode(), someLong(), firstDayOfLastYear.minusMonths(1)),
            new StockData(someStockCode(), someLong(), firstDayOfLastYear.minusMonths(2)),
            new StockData(someStockCode(), someLong(), firstDayOfLastYear.minusMonths(3)),
            new StockData(someStockCode(), someLong(), firstDayOfLastYear.minusMonths(4)),
            new StockData(someStockCode(), someLong(), firstDayOfLastYear.minusMonths(5))
        );

        solidarityDailyStatistics = stockDataList.stream().map(this::mockSolidarityDailyStatistics).flatMap(List::stream).toList();
    }

    record StockData(String code, Long solidarityId, LocalDate date) {
    }

    private void cleanUpExistingSolidarityDailyStatistics() {
        itUtil.findAllSolidarityDailyStatistics().forEach(itUtil::deleteSolidarityDailyStatistics);
    }

    private List<SolidarityDailyStatistics> mockSolidarityDailyStatistics(StockData stockData) {
        return List.of(
            itUtil.createSolidarityDailyStatistics(stockData.code(), stockData.solidarityId(), stockData.date().minusDays(DAYS_20)),
            itUtil.createSolidarityDailyStatistics(stockData.code(), stockData.solidarityId(), stockData.date()),
            itUtil.createSolidarityDailyStatistics(stockData.code(), stockData.solidarityId(), stockData.date().plusDays(DAYS_20))
        );
    }

    @Nested
    class WhenDailyStatistics {

        @BeforeEach
        void setUp() {
            responseDateFormatter = RESPONSE_FORMATTER_YYYY_MM_DD;
            selectedStockData = stockDataList.get(someIntegerBetween(0, stockDataList.size() - 1));
            periodType = StockStatisticsPeriodType.DAILY.name();
            period = selectedStockData.date().format(REQUEST_FORMATTER_YYYY_MM);
            selectedStatistics = solidarityDailyStatistics.stream()
                .filter(it -> it.getStockCode().equals(selectedStockData.code()))
                .filter(it -> it.getDate().format(REQUEST_FORMATTER_YYYY_MM).equals(selectedStockData.date().format(REQUEST_FORMATTER_YYYY_MM)))
                .sorted(Comparator.comparing(SolidarityDailyStatistics::getDate))
                .toList();
        }

        @Nested
        class AndSearchTypeIsStockQuantity {

            @BeforeEach
            void setUp() {
                searchType = StockStatisticsType.STOCK_QUANTITY.name();
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnStatistics() throws Exception {
                assertResponse(callApiAndGetResult(), item -> BigDecimal.valueOf(item.getStockQuantity()));
            }
        }

        @Nested
        class AndSearchTypeIsMemberCount {

            @BeforeEach
            void setUp() {
                searchType = StockStatisticsType.MEMBER_COUNT.name();
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnStatistics() throws Exception {
                assertResponse(callApiAndGetResult(), item -> BigDecimal.valueOf(item.getMemberCount()));
            }
        }

        @Nested
        class AndSearchTypeIsMarketValue {

            @BeforeEach
            void setUp() {
                searchType = StockStatisticsType.MARKET_VALUE.name();
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnStatistics() throws Exception {
                assertResponse(callApiAndGetResult(), item -> BigDecimal.valueOf(item.getMarketValue()));
            }
        }

        @Nested
        class AndSearchTypeIsStake {

            @BeforeEach
            void setUp() {
                searchType = StockStatisticsType.STAKE.name();
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnStatistics() throws Exception {
                assertResponse(callApiAndGetResult(), item -> BigDecimal.valueOf(item.getStake()));
            }
        }

        @Nested
        class WhenPeriodIsInvalid {

            @BeforeEach
            void setUp() {
                searchType = someEnum(StockStatisticsType.class).name();
                period = someAlphanumericString(6);
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequestException() throws Exception {
                final MvcResult mvcResult = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(mvcResult, 400, "검색할 period 를 'YYYYMM' 형식으로 입력해주세요.");
            }
        }

        private void assertResponse(GetStockStatisticsDataResponse result, Function<SolidarityDailyStatistics, BigDecimal> getValue) {
            final List<ag.act.model.StockStatisticsResponse> items = result.getData();

            assertThat(items.size(), is(2));
            assertStatistics(items.get(0), getValue.apply(selectedStatistics.get(0)), selectedStockData.date());
            assertStatistics(items.get(1), getValue.apply(selectedStatistics.get(1)), selectedStockData.date().plusDays(DAYS_20));
        }
    }

    @Nested
    class WhenMonthlyStatistics {
        @BeforeEach
        void setUp() {
            responseDateFormatter = RESPONSE_FORMATTER_YYYY_MM;
            selectedStockData = stockDataList.get(someIntegerBetween(0, stockDataList.size() - 1));
            periodType = StockStatisticsPeriodType.MONTHLY.name();
            period = selectedStockData.date().format(DateTimeFormatter.ofPattern("yyyy"));
            final List<SolidarityDailyStatistics> temp = solidarityDailyStatistics.stream()
                .filter(it -> it.getStockCode().equals(selectedStockData.code()))
                .sorted(Comparator.comparing(SolidarityDailyStatistics::getDate))
                .toList();
            selectedStatistics = List.of(temp.get(0), temp.get(2));
        }

        @Nested
        class AndSearchTypeIsStockQuantity {

            @BeforeEach
            void setUp() {
                searchType = StockStatisticsType.STOCK_QUANTITY.name();
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnStatistics() throws Exception {
                assertResponse(callApiAndGetResult(), item -> BigDecimal.valueOf(item.getStockQuantity()));
            }
        }

        @Nested
        class AndSearchTypeIsMemberCount {

            @BeforeEach
            void setUp() {
                searchType = StockStatisticsType.MEMBER_COUNT.name();
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnStatistics() throws Exception {
                assertResponse(callApiAndGetResult(), item -> BigDecimal.valueOf(item.getMemberCount()));
            }
        }

        @Nested
        class AndSearchTypeIsMarketValue {

            @BeforeEach
            void setUp() {
                searchType = StockStatisticsType.MARKET_VALUE.name();
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnStatistics() throws Exception {
                assertResponse(callApiAndGetResult(), item -> BigDecimal.valueOf(item.getMarketValue()));
            }
        }

        @Nested
        class AndSearchTypeIsStake {

            @BeforeEach
            void setUp() {
                searchType = StockStatisticsType.STAKE.name();
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnStatistics() throws Exception {
                assertResponse(callApiAndGetResult(), item -> BigDecimal.valueOf(item.getStake()));
            }
        }

        @Nested
        class WhenPeriodIsInvalid {

            @BeforeEach
            void setUp() {
                searchType = someEnum(StockStatisticsType.class).name();
                period = someAlphanumericString(6);
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequestException() throws Exception {
                final MvcResult mvcResult = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(mvcResult, 400, "검색할 period 를 'YYYY' 형식으로 입력해주세요.");
            }
        }

        private void assertResponse(GetStockStatisticsDataResponse result, Function<SolidarityDailyStatistics, BigDecimal> getValue) {
            final List<ag.act.model.StockStatisticsResponse> items = result.getData();

            assertThat(items.size(), is(2));
            assertStatistics(items.get(0), getValue.apply(selectedStatistics.get(0)), selectedStockData.date().minusDays(DAYS_20));
            assertStatistics(items.get(1), getValue.apply(selectedStatistics.get(1)), selectedStockData.date().plusDays(DAYS_20));
        }
    }

    private void assertStatistics(StockStatisticsResponse stockStatisticsResponse, BigDecimal value, LocalDate date) {
        assertThat(stockStatisticsResponse.getKey(), is(date.format(responseDateFormatter)));
        assertThat(stockStatisticsResponse.getValue(), is(value));
    }

    private GetStockStatisticsDataResponse callApiAndGetResult() throws Exception {

        final MvcResult response = callApi(status().isOk());

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            GetStockStatisticsDataResponse.class
        );
    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                get(TARGET_API, selectedStockData.code(), searchType, periodType)
                    .param("period", period)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(resultMatcher)
            .andReturn();
    }
}
