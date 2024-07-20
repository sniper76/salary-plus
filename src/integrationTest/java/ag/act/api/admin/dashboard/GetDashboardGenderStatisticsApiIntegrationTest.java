package ag.act.api.admin.dashboard;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.converter.dashboard.DashboardStatisticsItemResponseConverter;
import ag.act.dto.admin.DashboardStatisticsGenderCountDto;
import ag.act.entity.User;
import ag.act.entity.admin.DashboardGenderStatistics;
import ag.act.enums.admin.DashboardStatisticsPeriodType;
import ag.act.enums.admin.DashboardStatisticsType;
import ag.act.enums.admin.GenderTitle;
import ag.act.model.DashboardGenderStatisticsResponse;
import ag.act.model.DashboardStatisticsGenderDataResponse;
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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetDashboardGenderStatisticsApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/dashboard/statistics/gender";

    private String adminJwt;
    private Map<String, Object> params;
    private LocalDate todayLocalDate;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DashboardStatisticsType type = DashboardStatisticsType.DAILY_USER_GENDER_COUNT;
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
    class WhenDashboardStatisticsByGender {
        private final List<DashboardGenderStatistics> dataList = new ArrayList<>();

        @BeforeEach
        void setUp() {
            createData(type);
        }

        private void assertResultResponseDaily(DashboardStatisticsGenderDataResponse result) {
            DashboardGenderStatisticsResponse dataResponse = result.getData();

            String day1before = todayLocalDate.minusDays(1).format(formatter);
            String day2before = todayLocalDate.minusDays(2).format(formatter);

            List<DashboardStatisticsGenderCountDto> databaseAgeCountDto = itUtil.findByGenderTypeAndInDate(List.of(day1before, day2before));
            assertDataResponse(dataResponse, databaseAgeCountDto);
        }

        private void assertResultResponseMonthly(DashboardStatisticsGenderDataResponse result) {
            DashboardGenderStatisticsResponse dataResponse = result.getData();

            String lastDayOfPastMonth = KoreanDateTimeUtil.getFormattedLastDayOfPastMonth(1, "yyyy-MM-dd");
            String yesterdayTime = KoreanDateTimeUtil.getFormattedYesterdayTime("yyyy-MM-dd");

            List<DashboardStatisticsGenderCountDto> databaseAgeCountDto = itUtil.findByGenderTypeAndInDate(
                List.of(lastDayOfPastMonth, yesterdayTime)
            );
            assertDataResponse(dataResponse, databaseAgeCountDto);
        }

        private void assertDataResponse(
            DashboardGenderStatisticsResponse dataResponse, List<DashboardStatisticsGenderCountDto> databaseAgeCountDto
        ) {
            DashboardStatisticsGenderCountDto database1DayBefore = databaseAgeCountDto.get(0);
            DashboardStatisticsGenderCountDto database2DayBefore;
            if (databaseAgeCountDto.size() > 1) {
                database2DayBefore = databaseAgeCountDto.get(1);
            } else {
                database2DayBefore = DashboardStatisticsGenderCountDto.empty();
            }

            long total = dataResponse.getTotal();
            DashboardStatisticsPeriodType periodType = DashboardStatisticsPeriodType.fromValue(dataResponse.getPeriodType());

            assertResponseSummary(
                database1DayBefore.getMaleValue(), database2DayBefore.getMaleValue(), total,
                GenderTitle.MALE.getDisplayName(), periodType, dataResponse.getMale()
            );
            assertResponseSummary(
                database1DayBefore.getFemaleValue(), database2DayBefore.getFemaleValue(), total,
                GenderTitle.FEMALE.getDisplayName(), periodType, dataResponse.getFemale()
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
                    itUtil.createDashboardGenderStatistics(
                        type,
                        todayLocalDate.minusDays(k).format(formatter)
                    )
                );
            }
        }

        private DashboardStatisticsGenderDataResponse callApiAndGetResult(Map<String, Object> paramsMap) throws Exception {
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
                DashboardStatisticsGenderDataResponse.class
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
