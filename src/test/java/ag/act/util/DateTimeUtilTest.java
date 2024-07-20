package ag.act.util;

import ag.act.converter.DateTimeConverter;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;

import static ag.act.TestUtil.someInstantInTheFuture;
import static ag.act.TestUtil.someKoreanTimeInToday;
import static ag.act.TestUtil.someLocalDate;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomTimes.someTimeInThePast;
import static shiver.me.timbers.data.random.RandomTimes.someTimeLastMonth;
import static shiver.me.timbers.data.random.RandomTimes.someTimeToday;
import static shiver.me.timbers.data.random.RandomTimes.someTimeYesterday;

@MockitoSettings(strictness = Strictness.LENIENT)
class DateTimeUtilTest {

    @Nested
    class IsYesterday {

        @Nested
        class WhenInputIsYesterday {
            @Test
            void shouldReturnTrue() {

                // Given
                final LocalDateTime input = DateTimeConverter.convert(someTimeYesterday().toInstant());

                // When
                final boolean actual = DateTimeUtil.isYesterday(input);

                // Then
                assertThat(actual, is(true));
            }
        }

        @Nested
        class WhenInputIsNotYesterday {

            @ParameterizedTest(name = "{index} => inputDate=''{0}''")
            @MethodSource("noYesterdayInputProvider")
            void shouldReturnFalse(LocalDateTime input) {

                // When
                final boolean actual = DateTimeUtil.isYesterday(input);

                // Then
                assertThat(actual, is(false));
            }

            private static Stream<Arguments> noYesterdayInputProvider() {
                return Stream.of(
                    Arguments.of(DateTimeConverter.convert(someTimeToday().toInstant())),
                    Arguments.of(DateTimeConverter.convert(someTimeLastMonth().toInstant())),
                    Arguments.of(DateTimeConverter.convert(someInstantInTheFuture()))
                );
            }
        }

    }

    @Nested
    class IsBeforeToday {

        @Nested
        class WhenInputIsToday {
            @Test
            void shouldReturnTrue() {

                // Given
                final LocalDateTime input = DateTimeConverter.convert(someTimeToday().toInstant());

                // When
                final boolean actual = DateTimeUtil.isBeforeToday(input);

                // Then
                assertThat(actual, is(false));
            }
        }

        @Nested
        class WhenInputIsBeforeToday {

            @ParameterizedTest(name = "{index} => inputDate=''{0}''")
            @MethodSource("noYesterdayInputProvider")
            void shouldReturnFalse(LocalDateTime input) {

                // When
                final boolean actual = DateTimeUtil.isBeforeToday(input);

                // Then
                assertThat(actual, is(true));
            }

            private static Stream<Arguments> noYesterdayInputProvider() {
                return Stream.of(
                    Arguments.of(DateTimeConverter.convert(someTimeYesterday().toInstant())),
                    Arguments.of(DateTimeConverter.convert(someTimeLastMonth().toInstant())),
                    Arguments.of(DateTimeConverter.convert(someTimeInThePast().toInstant()))
                );
            }
        }

    }

    @Nested
    class GetLatestStockMarketClosingDate {

        private List<MockedStatic<?>> statics;

        @BeforeEach
        void setUp() {
            statics = List.of(mockStatic(KoreanDateTimeUtil.class));
        }

        @AfterEach
        void tearDown() {
            statics.forEach(MockedStatic::close);
        }

        @ParameterizedTest(name = "{index} => dateTime=''{0}'', expectedDateString=''{1}''")
        @MethodSource("quantityAndBadgeInputProvider")
        void shouldReturnLatestStockMarketClosingDate(ZonedDateTime dateTime, String expectedDateString) {

            // Given
            given(KoreanDateTimeUtil.getNowInKoreanTime()).willReturn(dateTime);

            // When
            final String actual = DateTimeUtil.getLatestStockMarketClosingDate();

            // Then
            assertThat(actual, Matchers.is(expectedDateString));
        }

        private static Stream<Arguments> quantityAndBadgeInputProvider() {
            return Stream.of(
                Arguments.of(createKoreanDate(10), "20230707"),//월
                Arguments.of(createKoreanDate(11), "20230710"),//화
                Arguments.of(createKoreanDate(12), "20230711"),//수
                Arguments.of(createKoreanDate(13), "20230712"),//목
                Arguments.of(createKoreanDate(14), "20230713"),//금
                Arguments.of(createKoreanDate(15), "20230714"),//토
                Arguments.of(createKoreanDate(16), "20230714"),//일
                Arguments.of(createKoreanDate(17), "20230714"),//월
                Arguments.of(createKoreanDate(18), "20230717"),//화
                Arguments.of(createKoreanDate(19), "20230718") //수
            );
        }

        private static ZonedDateTime createKoreanDate(int dayOfMonth) {
            return someKoreanTimeInToday()
                .truncatedTo(ChronoUnit.DAYS)
                .withYear(2023)
                .withMonth(7)
                .withDayOfMonth(dayOfMonth)
                .withHour(0)
                .withMinute(0)
                .withSecond(0);
        }
    }

    @Nested
    class GetFormattedCurrentByZone {

        @ParameterizedTest(name = "{index} => inputDate=''{0}'', expectedDateString=''{1}''")
        @MethodSource("dateProvider")
        void shouldReturnFormattedCurrentByZone(String inputDate, String expectedDateString) {
            // Given
            final Instant instant = Instant.parse(inputDate);

            // When
            final String actual = DateTimeUtil.getFormattedDateTimeByZone("yyyyMMdd", "UTC+9", instant);

            // Then
            assertThat(actual, is(expectedDateString));
        }

        private static Stream<Arguments> dateProvider() {
            return Stream.of(
                Arguments.of("2021-07-07T14:59:59.00Z", "20210707"),
                Arguments.of("2021-07-07T15:00:00.00Z", "20210708"),
                Arguments.of("2021-07-07T16:00:00.00Z", "20210708")
            );
        }
    }

    @Nested
    class GetFormattedKoreanTime {

        @ParameterizedTest(name = "{index} => inputDate=''{0}'', expectedDateString=''{1}''")
        @MethodSource("dateProvider")
        void shouldReturnFormattedCurrentByZone(String inputDate, String expectedDateString) {
            // Given
            final Instant instant = Instant.parse(inputDate);

            // When
            final String actual = DateTimeUtil.getFormattedKoreanTime("yyyy-MM-dd HH:mm:ss", instant);

            // Then
            assertThat(actual, is(expectedDateString));
        }

        private static Stream<Arguments> dateProvider() {
            return Stream.of(
                Arguments.of("2021-07-07T14:59:59.00Z", "2021-07-07 23:59:59"),
                Arguments.of("2021-07-07T15:00:00.00Z", "2021-07-08 00:00:00"),
                Arguments.of("2021-07-07T16:00:00.00Z", "2021-07-08 01:00:00")
            );
        }
    }

    @Nested
    class AdjustToPreviousOrSameDayOfWeek {
        @Test
        void shouldReturnSameDate() {
            // Given
            final LocalDate referenceDate = someLocalDate();
            final DayOfWeek dayOfWeek = referenceDate.getDayOfWeek();


            // When
            final LocalDate actual = DateTimeUtil.adjustToPreviousOrSameDayOfWeek(referenceDate, dayOfWeek);

            // Then
            assertThat(actual, is(referenceDate));
        }

        @Test
        void shouldReturnPreviousDayOfWeekDate() {
            // Given
            final LocalDate referenceDate = someLocalDate();
            final LocalDate expected = referenceDate.minusDays(someIntegerBetween(1, 6));
            final DayOfWeek dayOfWeek = expected.getDayOfWeek();

            // When
            final LocalDate actual = DateTimeUtil.adjustToPreviousOrSameDayOfWeek(referenceDate, dayOfWeek);

            // Then
            assertThat(actual, is(expected));
        }
    }

    @Nested
    class IsMonthBefore {

        @Nested
        class WhenTodayIsBeforeApril {

            private static Stream<Arguments> valueProvider() {
                return Stream.of(
                    Arguments.of(LocalDate.now().withMonth(someIntegerBetween(1, 3))),
                    Arguments.of(LocalDate.now().withMonth(3).withDayOfMonth(31))
                );
            }

            @ParameterizedTest
            @MethodSource("valueProvider")
            void shouldReturnTrue(LocalDate today) {
                final boolean actual = DateTimeUtil.isMonthBefore(today, Month.APRIL);

                assertThat(actual, is(true));
            }
        }

        @Nested
        class WhenTodayIsAfterApril {

            private static Stream<Arguments> valueProvider() {
                return Stream.of(
                    Arguments.of(LocalDate.now().withMonth(someIntegerBetween(4, 12))),
                    Arguments.of(LocalDate.now().withMonth(4).withDayOfMonth(1))
                );
            }

            @ParameterizedTest
            @MethodSource("valueProvider")
            void shouldReturnFalse(LocalDate today) {
                final boolean actual = DateTimeUtil.isMonthBefore(today, Month.APRIL);

                assertThat(actual, is(false));
            }
        }
    }
}
