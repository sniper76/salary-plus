package ag.act.api.admin.dashboard;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.converter.dashboard.DashboardStatisticsItemResponseConverter;
import ag.act.dto.admin.DashboardStatisticsAgeCountDto;
import ag.act.entity.User;
import ag.act.entity.admin.DashboardAgeStatistics;
import ag.act.enums.admin.AgeTitle;
import ag.act.enums.admin.DashboardStatisticsPeriodType;
import ag.act.enums.admin.DashboardStatisticsType;
import ag.act.model.DashboardAgeStatisticsResponse;
import ag.act.model.DashboardStatisticsAgeDataResponse;
import ag.act.model.DashboardStatisticsItemResponse;
import ag.act.util.KoreanDateTimeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ag.act.TestUtil.toMultiValueMap;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetDashboardAgeStatisticsApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/dashboard/statistics/age";

    private String adminJwt;
    private Map<String, Object> params;
    private LocalDate todayLocalDate;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DashboardStatisticsType type = DashboardStatisticsType.DAILY_USER_AGE_COUNT;
    private final DashboardStatisticsPeriodType periodTypeMonthly = DashboardStatisticsPeriodType.MONTHLY;

    @Autowired
    private DashboardStatisticsItemResponseConverter dashboardStatisticsItemResponseConverter;

    @BeforeEach
    void setUp() {
        itUtil.init();
        final User adminUser = itUtil.createAdminUser();
        adminJwt = itUtil.createJwt(adminUser.getId());
    }

    @Nested
    class WhenDashboardStatisticsByAge {
        private final List<DashboardAgeStatistics> dataList = new ArrayList<>();

        @BeforeEach
        void setUp() {
            createData(type);
        }

        private void assertResultResponseDaily(DashboardStatisticsAgeDataResponse result) {
            DashboardAgeStatisticsResponse dataResponse = result.getData();

            String day1before = todayLocalDate.minusDays(1).format(formatter);
            String day2before = todayLocalDate.minusDays(2).format(formatter);

            List<DashboardStatisticsAgeCountDto> databaseAgeCountDto = itUtil.findByAgeTypeAndInDate(List.of(day1before, day2before));
            assertDataResponse(dataResponse, databaseAgeCountDto);
        }

        private void assertResultResponseMonthly(DashboardStatisticsAgeDataResponse result) {
            DashboardAgeStatisticsResponse dataResponse = result.getData();

            String lastDayOfPastMonth = KoreanDateTimeUtil.getFormattedLastDayOfPastMonth(1, "yyyy-MM-dd");
            String yesterdayTime = KoreanDateTimeUtil.getFormattedYesterdayTime("yyyy-MM-dd");

            List<DashboardStatisticsAgeCountDto> databaseAgeCountDto = itUtil.findByAgeTypeAndInDate(
                List.of(lastDayOfPastMonth, yesterdayTime)
            );
            assertDataResponse(dataResponse, databaseAgeCountDto);
        }

        private void assertDataResponse(
            DashboardAgeStatisticsResponse dataResponse, List<DashboardStatisticsAgeCountDto> databaseAgeCountDto
        ) {
            DashboardStatisticsAgeCountDto database1DayBefore = databaseAgeCountDto.get(0);
            DashboardStatisticsAgeCountDto database2DayBefore;
            if (databaseAgeCountDto.size() > 1) {
                database2DayBefore = databaseAgeCountDto.get(1);
            } else {
                database2DayBefore = DashboardStatisticsAgeCountDto.empty();
            }

            long total = dataResponse.getTotal();
            DashboardStatisticsPeriodType periodType = DashboardStatisticsPeriodType.fromValue(dataResponse.getPeriodType());

            assertResponseSummary(
                database1DayBefore.getAge10Value(), database2DayBefore.getAge10Value(), total,
                AgeTitle.AGE10.getDisplayName(), periodType, dataResponse.getAge10()
            );
            assertResponseSummary(
                database1DayBefore.getAge20Value(), database2DayBefore.getAge20Value(), total,
                AgeTitle.AGE20.getDisplayName(), periodType, dataResponse.getAge20()
            );
            assertResponseSummary(
                database1DayBefore.getAge30Value(), database2DayBefore.getAge30Value(), total,
                AgeTitle.AGE30.getDisplayName(), periodType, dataResponse.getAge30()
            );
            assertResponseSummary(
                database1DayBefore.getAge40Value(), database2DayBefore.getAge40Value(), total,
                AgeTitle.AGE40.getDisplayName(), periodType, dataResponse.getAge40()
            );
            assertResponseSummary(
                database1DayBefore.getAge50Value(), database2DayBefore.getAge50Value(), total,
                AgeTitle.AGE50.getDisplayName(), periodType, dataResponse.getAge50()
            );
            assertResponseSummary(
                database1DayBefore.getAge60Value(), database2DayBefore.getAge60Value(), total,
                AgeTitle.AGE60.getDisplayName(), periodType, dataResponse.getAge60()
            );
            assertResponseSummary(
                database1DayBefore.getAge70AndOver(), database2DayBefore.getAge70AndOver(), total,
                AgeTitle.AGE70OVER.getDisplayName(), periodType, dataResponse.getAge70()
            );
        }

        private void assertResponseSummary(
            long currentValue, long pastValue, long total, String title,
            DashboardStatisticsPeriodType periodType, DashboardStatisticsItemResponse ageResponse
        ) {
            DashboardStatisticsItemResponse itemResponse = dashboardStatisticsItemResponseConverter.convert(
                currentValue, pastValue, total, title, periodType
            );

            assertThat(ageResponse.getTitle(), is(itemResponse.getTitle()));
            assertThat(ageResponse.getValue(), is(itemResponse.getValue()));
            assertThat(ageResponse.getPercent(), is(itemResponse.getPercent()));
            assertThat(ageResponse.getUpDown(), is(itemResponse.getUpDown()));
            assertThat(ageResponse.getUpDownText(), is(itemResponse.getUpDownText()));
            assertThat(ageResponse.getUpDownPercent(), is(itemResponse.getUpDownPercent()));
        }

        private void createData(DashboardStatisticsType type) {
            todayLocalDate = KoreanDateTimeUtil.getTodayLocalDate();
            for (int k = 1; k <= 10; k++) {
                dataList.add(
                    itUtil.createDashboardAgeStatistics(
                        type,
                        todayLocalDate.minusDays(k).format(formatter)
                    )
                );
            }
        }

        private DashboardStatisticsAgeDataResponse callApiAndGetResult(Map<String, Object> paramsMap) throws Exception {
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
                DashboardStatisticsAgeDataResponse.class
            );
        }

        @Nested
        class WhenSearchWithoutParams {
            @BeforeEach
            void setUp() {
                params = Map.of(
                    "type", type
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturn() throws Exception {
                assertResultResponseDaily(callApiAndGetResult(params));
            }
        }

        @Nested
        class WhenSearchMonthlyParams {
            @BeforeEach
            void setUp() {
                params = Map.of(
                    "type", type,
                    "periodType", periodTypeMonthly
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturn() throws Exception {
                assertResultResponseMonthly(callApiAndGetResult(params));
            }
        }
    }
}
