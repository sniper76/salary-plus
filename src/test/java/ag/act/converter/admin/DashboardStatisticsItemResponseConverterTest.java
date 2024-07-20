package ag.act.converter.admin;

import ag.act.converter.dashboard.DashboardStatisticsItemResponseConverter;
import ag.act.enums.admin.DashboardStatisticsPeriodType;
import ag.act.enums.admin.UpDownType;
import ag.act.model.DashboardStatisticsItemResponse;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@MockitoSettings(strictness = Strictness.LENIENT)
class DashboardStatisticsItemResponseConverterTest {
    @InjectMocks
    private DashboardStatisticsItemResponseConverter converter;

    @Nested
    class Convert {

        private static Stream<Arguments> valueProvider() {
            return Stream.of(
                Arguments.of(100L, 50L, 1000L, "10%", UpDownType.UP.getDisplayName(), "100%"),
                Arguments.of(100L, 0L, 1000L, "10%", UpDownType.UP.getDisplayName(), "100%"),
                Arguments.of(50L, 100L, 1000L, "5%", UpDownType.DOWN.getDisplayName(), "-50%"),
                Arguments.of(0L, 100L, 1000L, "0%", UpDownType.DOWN.getDisplayName(), "-100%"),
                Arguments.of(0L, 0L, 1000L, "0%", "-", "0%"),
                Arguments.of(100L, 100L, 1000L, "10%", "-", "0%"),
                Arguments.of(100L, 100L, 100L, "100%", "-", "0%")
            );
        }

        @SuppressWarnings("LineLength")
        @ParameterizedTest(name = """
            {index} => currentValue=''{0}'', pastValue=''{1}'', total=''{2}'', totalPercent=''{3}'', upDownTitle=''{4}'', yesterdayPercent=''{5}''
            """)
        @MethodSource("valueProvider")
        void shouldReturn(
            long currentValue, long pastValue, long total, String totalPercent, String upDownTitle, String yesterdayPercent
        ) {
            DashboardStatisticsPeriodType periodType = DashboardStatisticsPeriodType.DAILY;

            DashboardStatisticsItemResponse itemResponse = converter.convert(currentValue, pastValue, total, null, periodType);

            assertThat(itemResponse.getPercent(), is(totalPercent));
            assertThat(itemResponse.getUpDown(), is(upDownTitle));
            assertThat(itemResponse.getUpDownPercent(), is(yesterdayPercent));
        }
    }
}
