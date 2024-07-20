package ag.act.util;

import lombok.extern.slf4j.Slf4j;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.Random;

@Slf4j
public class DateTimeUtil {
    private static final LocalDateTime INFINITE_END_DATE
        = LocalDateTime.of(9999, 12, 31, 23, 59, 59);
    private static final int FIFTH_DAY = 5;

    private DateTimeUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean isYesterday(LocalDateTime dateTime) {
        if (dateTime == null) {
            return false;
        }

        final LocalDate yesterday = getYesterdayLocalDate();

        return dateTime.toLocalDate().equals(yesterday);
    }

    public static boolean isNowBefore(LocalDateTime dateTime) {
        if (dateTime == null) {
            return false;
        }

        final LocalDateTime now = LocalDateTime.now();

        return now.isBefore(dateTime);
    }

    public static boolean isNowAfter(LocalDateTime dateTime) {
        if (dateTime == null) {
            return false;
        }

        final LocalDateTime now = LocalDateTime.now();

        return now.isAfter(dateTime);
    }

    public static boolean isBeforeToday(LocalDateTime dateTime) {
        if (dateTime == null) {
            return false;
        }

        final LocalDate today = getTodayLocalDate();

        return dateTime.toLocalDate().isBefore(today);
    }

    public static boolean isBeforeInDays(LocalDateTime dateTime, int pastDays) {
        if (dateTime == null) {
            return false;
        }

        final LocalDateTime daysAgo = LocalDateTime.now().minusDays(pastDays);

        return dateTime.isBefore(daysAgo);
    }

    public static boolean isBeforeInHours(LocalDateTime dateTime, int pastHours) {
        if (dateTime == null) {
            return false;
        }

        final LocalDateTime pastDateTime = LocalDateTime.now().minusHours(pastHours);

        return dateTime.isBefore(pastDateTime);
    }

    public static boolean isPast(Instant sourceInstant) {
        if (sourceInstant == null) {
            return false;
        }
        return sourceInstant.isBefore(Instant.now());
    }

    public static boolean isPast(LocalDateTime sourceDate) {
        if (sourceDate == null) {
            return false;
        }
        return sourceDate.isBefore(LocalDateTime.now());
    }

    public static boolean isMonthBefore(LocalDate sourceDate, Month month) {
        final Month sourceDateMonth = sourceDate.getMonth();
        return sourceDateMonth.getValue() < month.getValue();
    }

    public static LocalDateTime getCurrentDateTimeMinusHours(Integer hours) {
        return LocalDateTime.now().minusHours(hours);
    }

    public static LocalDateTime getCurrentDateTimeMinusMinutes(Integer minutes) {
        return LocalDateTime.now().minusMinutes(minutes);
    }

    public static Instant getTodayInstant() {
        return Instant.now();
    }

    public static LocalDateTime getTodayLocalDateTime() {
        return LocalDateTime.now();
    }

    public static LocalDate getTodayLocalDate() {
        return LocalDate.now();
    }

    public static LocalDate getYesterdayLocalDate() {
        return getTodayLocalDate().minusDays(1);
    }

    public static LocalDateTime getYesterdayLocalDateTimeInKoreanTime() {
        return KoreanDateTimeUtil.getNowInKoreanTime().minusDays(1).toLocalDateTime();
    }

    public static String getLatestStockMarketClosingDate() {
        final LocalDateTime date = getYesterdayLocalDateTimeInKoreanTime();

        final DayOfWeek dayOfWeek = date.getDayOfWeek();
        final int dayOfWeekValue = dayOfWeek.getValue();

        final DateTimeFormatter dateTimeFormatter = DateTimeFormatUtil.yyyyMMdd();

        if (dayOfWeekValue > FIFTH_DAY) {
            return date.minusDays(dayOfWeekValue - FIFTH_DAY).format(dateTimeFormatter);
        }

        return date.format(dateTimeFormatter);
    }

    public static String getCurrentFormattedDateTime() {
        return getFormattedCurrentDateTime("yyyy-MM-dd HH:mm:ss");
    }

    public static String getFormattedCurrentDateTime(String format) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatUtil.ofPattern(format);

        return currentDateTime.format(formatter);
    }

    public static String getFormattedDateTime(LocalDateTime localDateTime, String format) {
        DateTimeFormatter formatter = DateTimeFormatUtil.ofPattern(format);

        return getFormattedDateTime(localDateTime, formatter);
    }

    public static String getFormattedDateTime(LocalDateTime localDateTime, DateTimeFormatter formatter) {
        return localDateTime.format(formatter);
    }

    public static String getFormattedDateTimeByZone(String format, String zone, Instant instant) {
        return DateTimeFormatUtil.ofPattern(format)
            .withZone(ZoneId.of(zone))
            .format(instant);
    }

    public static String getFormattedCurrentTimeInKorean(String format) {
        return getFormattedDateTimeByZone(format, "UTC+9", Instant.now());
    }

    public static String getFormattedKoreanTime(String format, Instant instant) {
        return getFormattedDateTimeByZone(format, "UTC+9", instant);
    }

    public static LocalDateTime parseLocalDateTime(String dateTimeString, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatUtil.ofPattern(pattern);
        return LocalDateTime.parse(dateTimeString, formatter);
    }

    public static LocalDate parseLocalDate(String dateString, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatUtil.ofPattern(pattern);
        return LocalDate.parse(dateString, formatter);
    }

    public static LocalDateTime getMinusDaysAndTruncatedToDays(LocalDateTime dateTime, int days) {
        return dateTime.minusDays(days).truncatedTo(ChronoUnit.DAYS);
    }

    public static boolean isInThisYear(LocalDate localDate) {
        final LocalDate endOfLastYearLocalDate = getEndOfLastYearLocalDate();
        final LocalDate startOfNextYearLocalDate = getStartOfNextYearLocalDate();
        return localDate.isAfter(endOfLastYearLocalDate) && localDate.isBefore(startOfNextYearLocalDate);
    }

    public static boolean isInLastYear(LocalDate localDate) {
        final LocalDate endOfTwoYearAgoLocalDate = getEndOfLastYearLocalDate().minusYears(1);
        final LocalDate startOfThisYearLocalDate = getStartOfThisYearLocalDate();
        return localDate.isAfter(endOfTwoYearAgoLocalDate) && localDate.isBefore(startOfThisYearLocalDate);
    }

    public static LocalDate getStartOfNextYearLocalDate() {
        return getEndOfThisYearLocalDate().plusDays(1);
    }

    public static LocalDate getStartOfThisYearLocalDate() {
        return getTodayLocalDate().withMonth(1).withDayOfMonth(1);
    }

    public static LocalDate getEndOfLastYearLocalDate() {
        return getEndOfLastYear().toLocalDate();
    }

    public static LocalDate getEndOfThisYearLocalDate() {
        return getTodayLocalDate().withMonth(12).withDayOfMonth(31);
    }

    public static LocalDateTime getEndOfLastYear() {
        return getTodayLocalDate().minusYears(1).withMonth(12).withDayOfMonth(31).atStartOfDay();
    }

    public static LocalDateTime getPastMonthFromCurrentLocalDateTime(int months) {
        return getTodayLocalDateTime().minusMonths(months);
    }

    public static Date getFutureDateFromCurrentInstant(long days) {
        return Date.from(Instant.now().plus(days, ChronoUnit.DAYS));
    }

    public static String formatLocalDateTime(LocalDateTime localDateTime, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatUtil.ofPattern(pattern);
        return localDateTime.format(formatter);
    }

    public static String formatLocalDate(LocalDate localDate, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatUtil.ofPattern(pattern);
        return localDate.format(formatter);
    }

    public static String extractYearMonth(String date) {
        return date.substring(0, 7);
    }

    public static Date newDate() {
        return new Date();
    }

    public static LocalDateTime getRandomDateTime(LocalDate localDate) {
        Random random = new Random();

        int hour = random.nextInt(24); // 0-23
        int minute = random.nextInt(60); // 0-59
        int second = random.nextInt(60); // 0-59
        int nanoSecond = random.nextInt(1_000_000_000); // 0-999,999,999

        LocalDateTime localDateTime = localDate.atTime(hour, minute, second, nanoSecond);
        return KoreanDateTimeUtil.toUtcDateTimeFromKoreanLocalDateTime(localDateTime);
    }

    public static LocalDate getNextWeek(LocalDate date) {
        return date.plusWeeks(1);
    }

    public static LocalDate getDateBeforeNextWeek(LocalDate date) {
        return date.plusDays(6);
    }

    public static LocalDate adjustToPreviousOrSameDayOfWeek(LocalDate date, DayOfWeek dayOfWeek) {
        return date.with(TemporalAdjusters.previousOrSame(dayOfWeek));
    }

    public static LocalDateTime getEndOfDay(LocalDate date) {
        return date.atStartOfDay().with(LocalTime.MAX);
    }

    public static LocalDateTime getEndOfDay(LocalDateTime date) {
        return date.with(LocalTime.MAX).withNano(0);
    }

    public static LocalDateTime getStartOfDay(LocalDateTime dateTime) {
        return dateTime.with(LocalTime.MIN);
    }

    public static boolean isSimilarLocalDateTime(LocalDateTime localDateTime1, LocalDateTime localDateTime2) {
        final LocalDateTime localDateTimeUntilMinutes1 = localDateTime1.withNano(0).withSecond(0);
        final LocalDateTime localDateTimeUntilMinutes2 = localDateTime2.withNano(0).withSecond(0);

        return localDateTimeUntilMinutes1.isEqual(localDateTimeUntilMinutes2);
    }

    public static LocalDateTime getInfiniteEndDate() {
        return INFINITE_END_DATE;
    }

    public static boolean isDateWithinRange(LocalDateTime startDateTime, LocalDateTime endDateTime, LocalDateTime targetDateTime) {
        if (startDateTime == null || endDateTime == null || targetDateTime == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }

        return !targetDateTime.isBefore(startDateTime) && !targetDateTime.isAfter(endDateTime);
    }

    public static boolean isEvenMinute(final LocalDateTime localDateTime) {
        return localDateTime.getMinute() % 2 == 0;
    }
}
