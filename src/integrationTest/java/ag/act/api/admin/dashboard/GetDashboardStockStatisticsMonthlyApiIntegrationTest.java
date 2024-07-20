package ag.act.api.admin.dashboard;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.converter.dashboard.DashboardStatisticsResponseSummaryConverter;
import ag.act.entity.User;
import ag.act.entity.admin.DashboardStockStatistics;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import static ag.act.TestUtil.someStockCode;
import static ag.act.TestUtil.toMultiValueMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetDashboardStockStatisticsMonthlyApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/dashboard/statistics";

    private String stockCode;
    private String adminJwt;
    private Map<String, Object> params;
    private final int defaultSize = 7;
    private LocalDate todayLocalDate;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter formatterMonth = DateTimeFormatter.ofPattern("yyyy-MM").withZone(ZoneId.of("Asia/Seoul"));
    private DashboardStatisticsPeriodType periodType;
    private Map<String, Double> statisticsValueByRecentTwoMonth;

    @Autowired
    private DashboardStatisticsResponseSummaryConverter dashboardStatisticsResponseSummaryConverter;

    @BeforeEach
    void setUp() {
        itUtil.init();
        dbCleaner.clean();

        final User adminUser = itUtil.createAdminUser();
        adminJwt = itUtil.createJwt(adminUser.getId());
        stockCode = someStockCode();
    }

    private void createDataMonthly(DashboardStatisticsType type) {
        todayLocalDate = KoreanDateTimeUtil.getTodayLocalDate();
        LocalDate startDateOfLastMonth = KoreanDateTimeUtil.getStartDateTimeOfThisMonth().minusMonths(1).toLocalDate();

        statisticsValueByRecentTwoMonth = new TreeMap<>();

        int days = 0;
        while (true) {
            LocalDate pastDate = startDateOfLastMonth.plusDays(days++);
            if (pastDate.equals(todayLocalDate)) {
                break;
            }
            String formattedDate = pastDate.format(formatter);
            String yearMonth = formattedDate.substring(0, 7);
            DashboardStockStatistics dashboardStockStatistics = itUtil.createDashboardStockStatistics(type, formattedDate, stockCode);
            Double sumValue = statisticsValueByRecentTwoMonth.getOrDefault(yearMonth, 0.0);
            statisticsValueByRecentTwoMonth.put(yearMonth, sumValue + dashboardStockStatistics.getValue());
        }
    }

    private DashboardStatisticsDataResponse callApiAndGetResult(Map<String, Object> paramsMap) throws Exception {
        MvcResult response = mockMvc
            .perform(
                get(TARGET_API)
                    .params(toMultiValueMap(paramsMap))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + adminJwt)
            )
            .andExpect(status().isOk())
            .andReturn();

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            DashboardStatisticsDataResponse.class
        );
    }

    @Nested
    class WhenDashboardStatisticsByStock {

        @Nested
        class WhenMonthly {
            private static Stream<Arguments> valueProvider() {
                return Stream.of(
                    Arguments.of(DashboardStatisticsType.DAILY_STOCK_POST_COUNT),
                    Arguments.of(DashboardStatisticsType.DAILY_STOCK_COMMENT_COUNT),
                    Arguments.of(DashboardStatisticsType.DAILY_STOCK_USER_HOLDING_COUNT),
                    Arguments.of(DashboardStatisticsType.DAILY_STOCK_MEMBER_COUNT),
                    Arguments.of(DashboardStatisticsType.DAILY_STOCK_LIKED_COUNT),
                    Arguments.of(DashboardStatisticsType.DAILY_STOCK_USER_WITHDRAWAL_COUNT)
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
                createDataMonthly(type);

                assertResponseMonthly(callApiAndGetResult(params), type);
            }
        }

        private void assertResponseMonthly(DashboardStatisticsDataResponse result, DashboardStatisticsType type) {
            DashboardStatisticsResponse dataResponse = result.getData().stream()
                .filter(it -> DashboardStatisticsType.fromValue(it.getType()) == type)
                .findFirst().orElseThrow();

            DashboardStatisticsResponseSummary summary = dataResponse.getSummary();

            List<DashboardStatisticsResponseItemsInner> responseItems = dataResponse.getItems();
            assertThat(responseItems.size(), is(defaultSize));

            final LocalDateTime yesterdayBasedMonth = KoreanDateTimeUtil.getEndDateTimeOfThisMonthFromYesterday();
            for (int i = 0; i < responseItems.size(); i++) {
                DashboardStatisticsResponseItemsInner itemsInner = responseItems.get(i);
                BigDecimal expectedValue = new BigDecimal(statisticsValueByRecentTwoMonth.getOrDefault(itemsInner.getKey(), 0.0));
                String expectedYearMonth = yesterdayBasedMonth.minusMonths(i).format(formatterMonth);

                assertThat(itemsInner.getKey(), is(expectedYearMonth));
                assertThat(itemsInner.getValue(), is(expectedValue));
            }

            assertSummary(dataResponse, summary, responseItems);
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
    }
}
