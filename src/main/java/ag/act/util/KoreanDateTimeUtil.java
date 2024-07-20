package ag.act.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class KoreanDateTimeUtil {

    private KoreanDateTimeUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static ZonedDateTime getNowInKoreanTime() {
        return ZonedDateTime.now(ZoneIdUtil.getSeoulZoneId());
    }

    public static ZonedDateTime getYesterdayZonedDateTime() {
        return getNowInKoreanTime().minusDays(1);
    }

    public static boolean isBeforeTodayKoreanTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return false;
        }

        final ZoneId seoulZone = ZoneIdUtil.getSeoulZoneId();
        final ZonedDateTime targetZonedDateTime = localDateTime.atZone(ZoneIdUtil.getSystemZoneId()).withZoneSameInstant(seoulZone);
        final ZonedDateTime todayZonedDateTime = ZonedDateTime.now().withZoneSameInstant(seoulZone).truncatedTo(ChronoUnit.DAYS);

        return targetZonedDateTime.isBefore(todayZonedDateTime);
    }

    public static String getFormattedCurrentDateTime(String format) {
        DateTimeFormatter formatter = DateTimeFormatUtil.ofPattern(format);
        return getFormattedCurrentDateTime(formatter);
    }

    public static String getFormattedCurrentDateTime(DateTimeFormatter formatter) {
        return getNowInKoreanTime().format(formatter);
    }

    public static String getFormattedCurrentDateTime() {
        return getFormattedCurrentDateTime("yyyy-MM-dd HH:mm:ss");
    }

    public static String getFormattedYesterdayTime(String format) {
        DateTimeFormatter formatter = DateTimeFormatUtil.ofPattern(format);
        return getYesterdayZonedDateTime().format(formatter);
    }

    public static String getFormattedLastDayOfPastMonth(int months, String format) {
        DateTimeFormatter formatter = DateTimeFormatUtil.ofPattern(format);
        return getPastMonthFromCurrent(months)
            .truncatedTo(ChronoUnit.DAYS).withDayOfMonth(1).plusMonths(1).minusSeconds(1).format(formatter);
    }

    public static ZonedDateTime getPastMonthFromCurrent(int months) {
        return getNowInKoreanTime().minusMonths(months);
    }

    public static ZonedDateTime toKoreanTime(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneIdUtil.getSystemZoneId()).withZoneSameInstant(ZoneIdUtil.getSeoulZoneId());
    }

    public static ZonedDateTime toKoreanTime(Instant instant) {
        return instant.atZone(ZoneIdUtil.getSystemZoneId()).withZoneSameInstant(ZoneIdUtil.getSeoulZoneId());
    }

    public static ZonedDateTime toEndDateTimeOfMonth(ZonedDateTime zonedDateTime) {
        return zonedDateTime.truncatedTo(ChronoUnit.DAYS).withDayOfMonth(1).plusMonths(1).minusSeconds(1);
    }

    public static ZonedDateTime toStartDateTimeOfMonth(ZonedDateTime zonedDateTime) {
        return zonedDateTime.truncatedTo(ChronoUnit.DAYS).withDayOfMonth(1);
    }

    public static boolean isEndOfMonth(ZonedDateTime dateTime) {
        int dayOfMonth = dateTime.getDayOfMonth();
        int lengthOfMonthInDays = dateTime.getMonth().length(dateTime.toLocalDate().isLeapYear());

        return dayOfMonth == lengthOfMonthInDays;
    }

    public static LocalDate getTodayLocalDate() {
        return getTodayLocalDateTime().toLocalDate();
    }

    public static LocalDateTime getTodayLocalDateTime() {
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatUtil.yyyy_MM_dd_hh_mm_ss_a();
        final String formattedKoreanTime = getNowInKoreanTime().format(dateTimeFormatter);
        return LocalDateTime.parse(formattedKoreanTime, dateTimeFormatter);
    }

    public static LocalDateTime getCurrentDateTime() {
        return toUtcLocalDateTime(getNowInKoreanTime());
    }

    public static LocalDateTime getStartDateTimeOfToday() {
        return toUtcLocalDateTime(
            getNowInKoreanTime().truncatedTo(ChronoUnit.DAYS)
        );
    }

    public static LocalDateTime getEndDateTimeOfToday() {
        return toUtcLocalDateTime(
            getNowInKoreanTime()
                .truncatedTo(ChronoUnit.DAYS)
                .with(LocalTime.MAX)
        );
    }

    public static LocalDate getYesterdayLocalDate() {
        return getTodayLocalDate().minusDays(1);
    }

    public static LocalDateTime getYesterdayStartDateTime() {
        return getYesterdayZonedDateTime().truncatedTo(ChronoUnit.DAYS).toLocalDateTime();
    }

    public static LocalDateTime getYesterdayEndDateTime() {
        return getNowInKoreanTime().truncatedTo(ChronoUnit.DAYS).minusSeconds(1).toLocalDateTime();
    }

    public static LocalDateTime getStartDateTimeOfThisMonth() {
        return getTodayLocalDate().withDayOfMonth(1).atStartOfDay();
    }

    public static LocalDateTime getEndDateTimeOfThisMonth() {
        return getStartDateTimeOfThisMonth().plusMonths(1).minusSeconds(1);
    }

    public static LocalDateTime getEndDateTimeOfThisMonthFromYesterday() {
        return getStartDateTimeOfThisMonthFromYesterday().plusMonths(1).minusSeconds(1);
    }

    public static LocalDateTime getStartDateTimeOfThisMonthFromYesterday() {
        return getYesterdayLocalDate().withDayOfMonth(1).atStartOfDay();
    }

    public static LocalDateTime getEndDateTimeOfPastMonth(int months) {
        return getStartDateOfNextMonth().minusMonths(months).atStartOfDay().minusSeconds(1);
    }

    public static LocalDate getStartDateOfNextMonth() {
        return getTodayLocalDate().plusMonths(1).withDayOfMonth(1);
    }

    public static LocalDate getEndOfLastYearLocalDate() {
        return getTodayLocalDate().minusYears(1).withMonth(12).withDayOfMonth(31);
    }

    public static LocalDateTime getEndOfLastYear() {
        return getEndOfLastYearLocalDate().atStartOfDay();
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

    public static LocalDate getEndOfThisYearLocalDate() {
        return getTodayLocalDate().withMonth(12).withDayOfMonth(31);
    }

    public static boolean isSameYearAndMonth(ZonedDateTime toZonedDateTime) {
        ZonedDateTime nowInKoreanTime = KoreanDateTimeUtil.getNowInKoreanTime();

        return toZonedDateTime.getYear() == nowInKoreanTime.getYear()
            && toZonedDateTime.getMonthValue() == nowInKoreanTime.getMonthValue();
    }

    public static LocalDateTime toUtcDateTimeFromKoreanLocalDate(LocalDate localDate) {
        return toUtcLocalDateTime(localDate.atStartOfDay(ZoneIdUtil.getSeoulZoneId()));
    }

    public static LocalDateTime toUtcDateTimeFromKoreanLocalDateTime(LocalDateTime localDateTime) {
        return toUtcLocalDateTime(localDateTime.atZone(ZoneIdUtil.getSeoulZoneId()));
    }

    public static LocalDateTime toKoreanTimeUntilMidnightNextDay(LocalDateTime baseDateTime) {
        return toKoreanTimeUntilMidnightWithGivenDays(baseDateTime, 1);
    }

    public static LocalDateTime toKoreanTimeUntilMidnightWithGivenDays(LocalDateTime baseDateTime, int daysToAdd) {
        final ZonedDateTime koreanNextDateTime = toKoreanTime(baseDateTime).plusDays(daysToAdd);

        if (koreanNextDateTime.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return toUtcLocalDateTime(koreanNextDateTime);
        }

        return toUtcLocalDateTime(
            koreanNextDateTime
                .truncatedTo(ChronoUnit.DAYS)
                .plusDays(1)
        );
    }

    private static LocalDateTime toUtcLocalDateTime(ZonedDateTime zonedDateTime) {
        return zonedDateTime
            .withZoneSameInstant(ZoneIdUtil.getSystemZoneId())
            .toLocalDateTime();
    }
}
