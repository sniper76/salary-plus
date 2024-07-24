package ag.act.api.admin.dashboard;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.converter.dashboard.DashboardStatisticsResponseSummaryConverter;
import ag.act.entity.User;
import ag.act.entity.admin.DashboardStatistics;
import ag.act.enums.admin.DashboardStatisticsPeriodType;
import ag.act.enums.admin.DashboardStatisticsType;
import ag.act.model.DashboardStatisticsDataResponse;
import ag.act.model.DashboardStatisticsResponse;
import ag.act.model.DashboardStatisticsResponseItemsInner;
import ag.act.model.DashboardStatisticsResponseSummary;
import ag.act.util.KoreanDateTimeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Stream;

import static ag.act.TestUtil.toMultiValueMap;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetDashboardStatisticsApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/dashboard/statistics";
    private static final int SEVEN_MONTHS = 7;
    private static final int SIX_MONTHS = 6;
    private final int defaultSize = 7;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter formatterMonthly = DateTimeFormatter.ofPattern("yyyy-MM");
    private final List<DashboardStatistics> dashboardStatisticsListByDaily = new ArrayList<>();
    private String adminJwt;
    private Map<String, Object> params;
    private LocalDate todayLocalDate;
    @Autowired
    private DashboardStatisticsResponseSummaryConverter dashboardStatisticsResponseSummaryConverter;
    private Map<String, Double> statisticsValueByRecentTwoMonth;
    private DashboardStatisticsPeriodType periodType;

    @BeforeEach
    void setUp() {
        itUtil.init();
        final User adminUser = itUtil.createAdminUser();
        adminJwt = itUtil.createJwt(adminUser.getId());
    }

    private void createDailyData(DashboardStatisticsType type) {
        todayLocalDate = KoreanDateTimeUtil.getTodayLocalDate();
        for (int k = 1; k <= 10; k++) {
            dashboardStatisticsListByDaily.add(
                itUtil.createDashboardStatistics(
                    type,
                    todayLocalDate.minusDays(k).format(formatter)
                )
            );
        }
    }

    private void createMonthlyData(DashboardStatisticsType type) {
        todayLocalDate = KoreanDateTimeUtil.getTodayLocalDate();
        LocalDate startDateOfLastMonth = KoreanDateTimeUtil.getStartDateTimeOfThisMonth()
            .minusMonths(todayLocalDate.getDayOfMonth() == 1 ? SEVEN_MONTHS : SIX_MONTHS)
            .toLocalDate();

        statisticsValueByRecentTwoMonth = new TreeMap<>();

        int days = 0;
        while (true) {
            LocalDate pastDate = startDateOfLastMonth.plusDays(days++);
            if (pastDate.equals(todayLocalDate)) {
                break;
            }
            String formattedDate = pastDate.format(formatter);
            String yearMonth = formattedDate.substring(0, 7);
            DashboardStatistics dashboardStatistics = itUtil.createDashboardStatistics(type, formattedDate);
            Double sumValue = statisticsValueByRecentTwoMonth.getOrDefault(yearMonth, 0.0);
            statisticsValueByRecentTwoMonth.put(yearMonth, sumValue + dashboardStatistics.getValue());
        }
    }

    private DashboardStatisticsDataResponse callApiAndGetResult(Map<String, Object> paramsMap) throws Exception {
        MvcResult response = mockMvc
            .perform(
                get(TARGET_API)
                    .params(toMultiValueMap(paramsMap))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(adminJwt)))
            )
            .andExpect(status().isOk())
            .andReturn();

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            DashboardStatisticsDataResponse.class
        );
    }

    private void assertSummary(
        DashboardStatisticsResponse dataResponse,
        DashboardStatisticsResponseSummary summary,
        List<DashboardStatisticsResponseItemsInner> responseItems
    ) {
        DashboardStatisticsResponseItemsInner innerResponse1 = responseItems.get(0);
        DashboardStatisticsResponseItemsInner innerResponse2 = responseItems.get(1);

        DashboardStatisticsResponseSummary responseSummary = dashboardStatisticsResponseSummaryConverter.convert(
            periodType, innerResponse1.getValue().doubleValue(), innerResponse2.getValue().doubleValue()
        );

        assertThat(dataResponse.getValue(), is(innerResponse1.getValue()));
        assertThat(summary.getUpDown(), is(responseSummary.getUpDown()));
        assertThat(summary.getUpDownText(), is(responseSummary.getUpDownText()));
        assertThat(summary.getUpDownPercent(), is(responseSummary.getUpDownPercent()));
    }

    private void assertResponse(
        DashboardStatisticsDataResponse result,
        DashboardStatisticsType type,
        List<DashboardStatistics> dashboardStatistics
    ) {
        DashboardStatisticsResponse dataResponse = result.getData().stream()
            .filter(it -> Objects.equals(it.getType(), type.name()))
            .findFirst().orElseThrow();

        DashboardStatisticsResponseSummary summary = dataResponse.getSummary();

        List<DashboardStatisticsResponseItemsInner> responseItems = dataResponse.getItems();
        assertThat(responseItems.size(), is(defaultSize));

        for (int i = 0; i < responseItems.size(); i++) {
            DashboardStatisticsResponseItemsInner itemsInner = responseItems.get(i);
            assertThat(itemsInner.getValue(), is(new BigDecimal(dashboardStatistics.get(i).getValue())));
        }

        assertSummary(dataResponse, summary, responseItems);
    }

    private void assertStatisticsOrder(DashboardStatisticsDataResponse response, List<DashboardStatisticsType> dashboardStatisticsTypes) {
        final List<DashboardStatisticsResponse> responseData = response.getData();

        for (int i = 0; i < responseData.size(); i++) {
            assertThat(responseData.get(i).getType(), is(dashboardStatisticsTypes.get(i).name()));
        }
    }

    @Nested
    class WhenDashboardStatistics {

        @Nested
        class WhenDailySearch {

            @Nested
            class WhenWithoutMau {

                private static Stream<Arguments> valueProvider() {
                    return Stream.of(
                        Arguments.of(DashboardStatisticsType.DAILY_USER_ACTIVE_MY_DATA_ACCESS_COUNT),
                        Arguments.of(DashboardStatisticsType.DAILY_USER_ACTIVE_MY_DATA_ACCESS_ACCUMULATE),
                        Arguments.of(DashboardStatisticsType.DAILY_USER_STATUS_PROCESSING_COUNT),
                        Arguments.of(DashboardStatisticsType.DAILY_USER_STATUS_ACTIVE_COUNT),
                        Arguments.of(DashboardStatisticsType.DAILY_USER_STATUS_WITHDRAWAL_COUNT),
                        Arguments.of(DashboardStatisticsType.DAILY_USER_REGISTRATION_COUNT),
                        Arguments.of(DashboardStatisticsType.DAILY_USER_WITHDRAWAL_COUNT),
                        Arguments.of(DashboardStatisticsType.DAILY_POST_VIEW_COUNT),
                        Arguments.of(DashboardStatisticsType.DAILY_USER_LOGIN_COUNT),
                        Arguments.of(DashboardStatisticsType.DAILY_USER_REUSE_RATE),
                        Arguments.of(DashboardStatisticsType.DAILY_USER_ACCESS_PIN_NUMBER_COUNT),
                        Arguments.of(DashboardStatisticsType.DAILY_TOTAL_ASSET_PRICE),
                        Arguments.of(DashboardStatisticsType.DAILY_ACTIVE_USER)
                    );
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @ParameterizedTest(name = "{index} => type=''{0}''")
                @MethodSource("valueProvider")
                void shouldReturn(DashboardStatisticsType type) throws Exception {
                    periodType = DashboardStatisticsPeriodType.DAILY;

                    params = Map.of(
                        "type", type,
                        "periodType", periodType.name()
                    );
                    createDailyData(type);

                    assertResponse(callApiAndGetResult(params), type, dashboardStatisticsListByDaily);
                }
            }

            @Nested
            class AndTypeIsNotProvided {
                private final Map<DashboardStatisticsType, List<DashboardStatistics>> dailyDashboardStatisticsForAllTypes = new HashMap<>();

                private List<DashboardStatisticsType> valueProvider() {
                    return List.of(
                        DashboardStatisticsType.DAILY_USER_REGISTRATION_COUNT,
                        DashboardStatisticsType.DAILY_USER_WITHDRAWAL_COUNT,
                        DashboardStatisticsType.DAILY_ACTIVE_USER,
                        // DashboardStatisticsType.MONTHLY_ACTIVE_USER,
                        DashboardStatisticsType.DAILY_TOTAL_ASSET_PRICE,
                        DashboardStatisticsType.DAILY_USER_LOGIN_COUNT,
                        DashboardStatisticsType.DAILY_USER_REUSE_RATE,
                        DashboardStatisticsType.DAILY_POST_VIEW_COUNT,
                        DashboardStatisticsType.DAILY_USER_ACCESS_PIN_NUMBER_COUNT,
                        DashboardStatisticsType.DAILY_USER_STATUS_ACTIVE_COUNT,
                        DashboardStatisticsType.DAILY_USER_STATUS_PROCESSING_COUNT,
                        DashboardStatisticsType.DAILY_USER_STATUS_WITHDRAWAL_COUNT,
                        DashboardStatisticsType.DAILY_USER_ACTIVE_MY_DATA_ACCESS_COUNT,
                        DashboardStatisticsType.DAILY_USER_ACTIVE_MY_DATA_ACCESS_ACCUMULATE
                    );
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API + " without type")
                @Test
                void shouldReturnAllStatistics() throws Exception {

                    final List<DashboardStatisticsType> dashboardStatisticsTypes = valueProvider();

                    periodType = DashboardStatisticsPeriodType.DAILY;
                    params = Map.of(
                        "type", "",
                        "periodType", periodType
                    );

                    dashboardStatisticsTypes.forEach(type -> {
                        createDailyData(type);
                        dailyDashboardStatisticsForAllTypes.put(type, new ArrayList<>(dashboardStatisticsListByDaily));
                        dashboardStatisticsListByDaily.clear();
                    });

                    final var response = callApiAndGetResult(params);

                    assertStatisticsOrder(response, dashboardStatisticsTypes);
                    dashboardStatisticsTypes.forEach(type -> assertResponse(response, type, dailyDashboardStatisticsForAllTypes.get(type)));
                }
            }
        }

        @SuppressWarnings("UnpredictableBigDecimalConstructorCall")
        @Nested
        class WhenMonthlySearch {

            private void assertResponse(DashboardStatisticsDataResponse result, DashboardStatisticsType type) {
                DashboardStatisticsResponse dataResponse = result.getData().stream()
                    .filter(it -> DashboardStatisticsType.fromValue(it.getType()) == type)
                    .findFirst().orElseThrow();

                DashboardStatisticsResponseSummary summary = dataResponse.getSummary();

                List<DashboardStatisticsResponseItemsInner> responseItems = dataResponse.getItems();
                assertThat(responseItems.size(), is(defaultSize));

                LocalDate yesterdayBasedMonth = KoreanDateTimeUtil.getEndDateTimeOfThisMonthFromYesterday().toLocalDate();
                for (int i = 0; i < responseItems.size(); i++) {
                    DashboardStatisticsResponseItemsInner itemsInner = responseItems.get(i);
                    BigDecimal expectedValue = new BigDecimal(statisticsValueByRecentTwoMonth.getOrDefault(itemsInner.getKey(), 0.0));
                    String expectedYearMonth = yesterdayBasedMonth.minusMonths(i).format(formatterMonthly);

                    assertThat(itemsInner.getKey(), is(expectedYearMonth));
                    assertThat(itemsInner.getValue(), is(expectedValue));
                }

                assertSummary(dataResponse, summary, responseItems);
            }

            @Nested
            class WhenMonthlyAccumulate {

                private static Stream<Arguments> valueProvider() {
                    return Stream.of(
                        Arguments.of(DashboardStatisticsType.DAILY_TOTAL_ASSET_PRICE),
                        Arguments.of(DashboardStatisticsType.DAILY_USER_ACTIVE_MY_DATA_ACCESS_ACCUMULATE)
                    );
                }

                private void createDataMonthlyAccumulate(DashboardStatisticsType type) {
                    todayLocalDate = KoreanDateTimeUtil.getTodayLocalDate();
                    LocalDate startDateOfLastMonth = KoreanDateTimeUtil.getStartDateTimeOfThisMonth().minusMonths(1).toLocalDate();
                    String yesterday = KoreanDateTimeUtil.getYesterdayLocalDate().format(formatter);
                    String endDateOfLastMonth = KoreanDateTimeUtil.getEndDateTimeOfPastMonth(1).format(formatter);

                    statisticsValueByRecentTwoMonth = new TreeMap<>();

                    int days = 0;
                    while (true) {
                        LocalDate pastDate = startDateOfLastMonth.plusDays(days++);
                        if (pastDate.equals(todayLocalDate)) {
                            break;
                        }
                        String formattedDate = pastDate.format(formatter);
                        String yearMonth = formattedDate.substring(0, 7);
                        DashboardStatistics dashboardStatistics = itUtil.createDashboardStatistics(type, formattedDate);

                        if (List.of(yesterday, endDateOfLastMonth).contains(formattedDate)) {
                            statisticsValueByRecentTwoMonth.put(yearMonth, dashboardStatistics.getValue());
                        }
                    }
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @ParameterizedTest(name = "{index} => type=''{0}''")
                @MethodSource("valueProvider")
                void shouldReturn(DashboardStatisticsType type) throws Exception {
                    periodType = DashboardStatisticsPeriodType.MONTHLY;

                    params = Map.of(
                        "type", type,
                        "periodType", periodType.name()
                    );
                    createDataMonthlyAccumulate(type);

                    assertResponse(callApiAndGetResult(params), type);
                }
            }

            @Nested
            class WhenMonthlyBetweenSearchType {

                private static Stream<Arguments> valueProvider() {
                    return Stream.of(
                        Arguments.of(DashboardStatisticsType.DAILY_USER_ACTIVE_MY_DATA_ACCESS_COUNT),
                        Arguments.of(DashboardStatisticsType.DAILY_USER_STATUS_PROCESSING_COUNT),
                        Arguments.of(DashboardStatisticsType.DAILY_USER_STATUS_ACTIVE_COUNT),
                        Arguments.of(DashboardStatisticsType.DAILY_USER_STATUS_WITHDRAWAL_COUNT),
                        Arguments.of(DashboardStatisticsType.DAILY_USER_REGISTRATION_COUNT),
                        Arguments.of(DashboardStatisticsType.DAILY_USER_WITHDRAWAL_COUNT),
                        Arguments.of(DashboardStatisticsType.DAILY_POST_VIEW_COUNT),
                        Arguments.of(DashboardStatisticsType.DAILY_USER_LOGIN_COUNT),
                        Arguments.of(DashboardStatisticsType.DAILY_USER_REUSE_RATE),
                        Arguments.of(DashboardStatisticsType.DAILY_USER_ACCESS_PIN_NUMBER_COUNT)
                    );
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @ParameterizedTest(name = "{index} => type=''{0}''")
                @MethodSource("valueProvider")
                void shouldReturn(DashboardStatisticsType type) throws Exception {
                    periodType = DashboardStatisticsPeriodType.MONTHLY;

                    params = Map.of(
                        "type", type,
                        "periodType", periodType.name()
                    );
                    createMonthlyData(type);

                    assertResponse(callApiAndGetResult(params), type);
                }
            }

            @Nested
            class WhenDauIsEmpty {
                private final DashboardStatisticsType type = DashboardStatisticsType.DAILY_ACTIVE_USER;

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturn() throws Exception {
                    periodType = DashboardStatisticsPeriodType.MONTHLY;

                    params = Map.of(
                        "type", type,
                        "periodType", periodType.name()
                    );
                    createDailyData(type);

                    assertResponse(callApiAndGetResult(params));
                }

                private void assertResponse(DashboardStatisticsDataResponse result) {
                    List<DashboardStatisticsResponse> dataResponse = result.getData();

                    assertThat(dataResponse.size(), is(0));
                }
            }
        }
    }
}
