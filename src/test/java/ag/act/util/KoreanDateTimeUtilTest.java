package ag.act.util;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Stream;

import static ag.act.TestUtil.someKoreanTimeInToday;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;

@MockitoSettings(strictness = Strictness.LENIENT)
class KoreanDateTimeUtilTest {

    private List<MockedStatic<?>> statics;

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(ZoneIdUtil.class));

        given(ZoneIdUtil.getSeoulZoneId()).willReturn(ZoneId.of("Asia/Seoul"));
        given(ZoneIdUtil.getSystemZoneId()).willReturn(ZoneId.of("UTC"));
    }

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @Nested
    class IsBeforeTodayKoreanTime {

        @Nested
        class WhenInputIsBeforeToday {

            @Nested
            class WhenInputDateIsToday {

                @RepeatedTest(100)
                void shouldReturnFalse() {

                    // Given
                    final LocalDateTime input = someKoreanTimeInToday()
                        .withZoneSameInstant(ZoneId.of("UTC")) // convert to UTC like Server does
                        .toLocalDateTime();

                    // When
                    final boolean actual = KoreanDateTimeUtil.isBeforeTodayKoreanTime(input);

                    // Then
                    assertThat(actual, is(false));
                }
            }

            @Nested
            class WhenInputDateIsNotToday {

                @RepeatedTest(100)
                void shouldReturnTrue() {

                    // Given
                    final LocalDateTime input = someKoreanTimeInToday()
                        .minusDays(someIntegerBetween(1, 10)) // past date
                        .withZoneSameInstant(ZoneId.of("UTC")) // convert to UTC like Server does
                        .toLocalDateTime();

                    // When
                    final boolean actual = KoreanDateTimeUtil.isBeforeTodayKoreanTime(input);

                    // Then
                    assertThat(actual, is(true));
                }
            }
        }
    }

    @Nested
    class ToKoreanTime {

        @Nested
        class AndLocalDateTime {
            @Test
            void shouldReturnKoreanLocalDateTime() {

                // Given
                final int hoursInKorean = someIntegerBetween(9, 23);
                final ZonedDateTime utc = ZonedDateTime.of(2023, 9, 11, (hoursInKorean - 9), 0, 0, 0, ZoneId.of("UTC"));
                final LocalDateTime utcLocalDateTime = utc.toLocalDateTime();

                // When
                final ZonedDateTime koreanLocalDateTime = KoreanDateTimeUtil.toKoreanTime(utcLocalDateTime);

                // Then
                assertThat(koreanLocalDateTime.toString(),
                    is("2023-09-11T%s:00+09:00[Asia/Seoul]".formatted(StringUtils.leftPad(String.valueOf(hoursInKorean), 2, "0"))));
                assertThat(koreanLocalDateTime.getYear(), is(2023));
                assertThat(koreanLocalDateTime.getMonthValue(), is(9));
                assertThat(koreanLocalDateTime.getDayOfMonth(), is(11));
                assertThat(koreanLocalDateTime.getHour(), is(hoursInKorean));
            }
        }

        @Nested
        class AndInstant {
            @Test
            void shouldReturnKoreanLocalDateTime() {

                // Given
                final int hoursInKorean = someIntegerBetween(9, 23);
                final ZonedDateTime utc = ZonedDateTime.of(2023, 9, 11, (hoursInKorean - 9), 0, 0, 0, ZoneId.of("UTC"));
                final ZonedDateTime utcLocalDateTime = utc.toLocalDateTime().atZone(ZoneId.of("UTC"));

                // When
                final ZonedDateTime koreanLocalDateTime = KoreanDateTimeUtil.toKoreanTime(utcLocalDateTime.toInstant());

                // Then
                assertThat(koreanLocalDateTime.toString(),
                    is("2023-09-11T%s:00+09:00[Asia/Seoul]".formatted(StringUtils.leftPad(String.valueOf(hoursInKorean), 2, "0"))));
                assertThat(koreanLocalDateTime.getYear(), is(2023));
                assertThat(koreanLocalDateTime.getMonthValue(), is(9));
                assertThat(koreanLocalDateTime.getDayOfMonth(), is(11));
                assertThat(koreanLocalDateTime.getHour(), is(hoursInKorean));
            }
        }
    }

    @Nested
    class GetTodayLocalDate {

        private String koreanTime;

        @BeforeEach
        void setUp() {
            koreanTime = ZonedDateTime.now(ZoneIdUtil.getSeoulZoneId()).format(DateTimeFormatUtil.yyyyMMdd());
        }

        @Test
        void shouldReturnKoreanLocalDate() {
            final LocalDate todayLocalDate = KoreanDateTimeUtil.getTodayLocalDate();

            assertThat(todayLocalDate.format(DateTimeFormatUtil.yyyyMMdd()), is(koreanTime));
        }
    }

    @Nested
    class ToKoreanTimeUntilMidnightWithGivenDays {
        private static Stream<Arguments> valueProvider() {
            return Stream.of(
                Arguments.of(
                    LocalDateTime.of(2023, 9, 10, 15, 0, 0),
                    LocalDateTime.of(2023, 9, 11, 15, 0, 0)
                ),
                Arguments.of(
                    LocalDateTime.of(2023, 9, 10, 15, 0, 1),
                    LocalDateTime.of(2023, 9, 12, 15, 0, 0)
                ),
                Arguments.of(
                    LocalDateTime.of(2023, 9, 10, 23, 0, 0),
                    LocalDateTime.of(2023, 9, 12, 15, 0, 0)
                ),
                Arguments.of(
                    LocalDateTime.of(2023, 9, 11, 0, 0, 0),
                    LocalDateTime.of(2023, 9, 12, 15, 0, 0)
                ),
                Arguments.of(
                    LocalDateTime.of(2023, 9, 11, 10, 0, 0),
                    LocalDateTime.of(2023, 9, 12, 15, 0, 0)
                ),
                Arguments.of(
                    LocalDateTime.of(2023, 9, 11, 15, 0, 0),
                    LocalDateTime.of(2023, 9, 12, 15, 0, 0)
                ),
                Arguments.of(
                    LocalDateTime.of(2023, 9, 11, 15, 0, 1),
                    LocalDateTime.of(2023, 9, 13, 15, 0, 0)
                )
            );
        }

        @ParameterizedTest(name = "{index} => input=''{0}'', expected=''{1}''")
        @MethodSource("valueProvider")
        void shouldReturnEndOfKorenDayButLocalDateTimeInUtc(LocalDateTime input, LocalDateTime expected) {

            // When
            final LocalDateTime actual = KoreanDateTimeUtil.toKoreanTimeUntilMidnightNextDay(input);

            // Then
            assertThat(actual, is(expected));
        }
    }
}
