package ag.act.enums.admin;

import ag.act.util.DateTimeUtil;
import ag.act.util.KoreanDateTimeUtil;
import ag.act.util.ZoneIdUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@MockitoSettings(strictness = Strictness.LENIENT)
class DashboardStatisticsTypeTest {

    @Nested
    class GetDateList {
        private DashboardStatisticsPeriodType periodType;

        @Nested
        class LastDayOfMonth {

            @Test
            void should() {
                LocalDateTime localDateTime1 = LocalDateTime.of(2023, 8, 1, 12, 12, 12);
                LocalDateTime localDateTime2 = LocalDateTime.of(2023, 9, 1, 12, 12, 12);
                LocalDateTime localDateTime3 = LocalDateTime.of(2023, 10, 1, 12, 12, 12);
                LocalDateTime localDateTime4 = LocalDateTime.of(2023, 11, 1, 12, 12, 12);
                ZonedDateTime zonedDateTime1 = ZonedDateTime.of(localDateTime1, ZoneIdUtil.getSeoulZoneId());
                ZonedDateTime zonedDateTime2 = ZonedDateTime.of(localDateTime2, ZoneIdUtil.getSeoulZoneId());
                ZonedDateTime zonedDateTime3 = ZonedDateTime.of(localDateTime3, ZoneIdUtil.getSeoulZoneId());
                ZonedDateTime zonedDateTime4 = ZonedDateTime.of(localDateTime4, ZoneIdUtil.getSeoulZoneId());

                assertThat(
                    zonedDateTime1.withDayOfMonth(zonedDateTime1.getMonth().length(zonedDateTime1.toLocalDate().isLeapYear())).getDayOfMonth(),
                    is(31)
                );
                assertThat(
                    zonedDateTime2.withDayOfMonth(zonedDateTime2.getMonth().length(zonedDateTime2.toLocalDate().isLeapYear())).getDayOfMonth(),
                    is(30)
                );
                assertThat(
                    zonedDateTime3.withDayOfMonth(zonedDateTime3.getMonth().length(zonedDateTime3.toLocalDate().isLeapYear())).getDayOfMonth(),
                    is(31)
                );
                assertThat(
                    zonedDateTime4.withDayOfMonth(zonedDateTime4.getMonth().length(zonedDateTime4.toLocalDate().isLeapYear())).getDayOfMonth(),
                    is(30)
                );
            }
        }

        @Nested
        class Daily {
            @BeforeEach
            void setUp() {
                periodType = DashboardStatisticsPeriodType.DAILY;
            }

            @Nested
            class Normal {

                @Test
                void shouldReturnDateList() {
                    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    final ZonedDateTime now = KoreanDateTimeUtil.getNowInKoreanTime();
                    final List<String> sourceList = new ArrayList<>();
                    final int size = 5;
                    for (int k = 1; k <= size; k++) {
                        sourceList.add(now.minusDays(k).format(formatter));
                    }

                    List<String> dateList = DashboardStatisticsType.DAILY_POST_VIEW_COUNT.getDateList(
                        periodType,
                        sourceList.get(size - 1),
                        sourceList.get(0)
                    );

                    assertThat(dateList, is(sourceList.stream().sorted().toList()));
                }
            }

            @Nested
            class TotalAssetPriceType {

                @Test
                void shouldReturnDateList() {
                    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    final ZonedDateTime now = KoreanDateTimeUtil.getNowInKoreanTime();
                    final List<String> sourceList = new ArrayList<>();
                    final int size = 5;
                    for (int k = 1; k <= size; k++) {
                        sourceList.add(now.minusDays(k).format(formatter));
                    }

                    List<String> dateList = DashboardStatisticsType.DAILY_TOTAL_ASSET_PRICE.getDateList(
                        periodType,
                        sourceList.get(size - 1),
                        sourceList.get(0)
                    );

                    assertThat(dateList, is(sourceList.stream().sorted().toList()));
                }
            }
        }

        @Nested
        class Monthly {
            @BeforeEach
            void setUp() {
                periodType = DashboardStatisticsPeriodType.MONTHLY;
            }

            @Nested
            class TotalAssetPriceType {


                @Test
                void shouldReturnDateList() {
                    final LocalDate now = LocalDate.of(2023, 11, 16);

                    List<String> dateList = DashboardStatisticsType.DAILY_TOTAL_ASSET_PRICE.getDateList(
                        periodType,
                        "2023-08",
                        "2023-11"
                    );

                    assertThat(dateList, is(List.of("2023-08-31", "2023-09-30", "2023-10-31", "2023-11-30")));
                }
            }

            @Nested
            class NormalCase {

                @Test
                void shouldReturnDateList() {
                    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
                    final ZonedDateTime now = KoreanDateTimeUtil.getNowInKoreanTime();
                    final List<String> sourceList = new ArrayList<>();
                    final int size = 3;
                    for (int k = 1; k <= size; k++) {
                        sourceList.add(now.minusMonths(k).format(formatter));
                    }

                    List<String> dateList = DashboardStatisticsType.DAILY_POST_VIEW_COUNT.getDateList(
                        periodType,
                        sourceList.get(size - 1),
                        sourceList.get(0)
                    );

                    assertThat(
                        dateList.stream().map(DateTimeUtil::extractYearMonth).sorted().toList(),
                        is(sourceList.stream().sorted().toList())
                    );
                }
            }
        }
    }
}
