package ag.act.util;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateTimeFormatUtil {

    private DateTimeFormatUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static DateTimeFormatter ofPattern(String pattern) {
        return ofPattern(pattern, Locale.getDefault());
    }

    public static DateTimeFormatter ofPattern(String pattern, Locale locale) {
        return DateTimeFormatter.ofPattern(pattern, locale);
    }

    public static DateTimeFormatter yyMMdd() {
        return ofPattern("yyMMdd");
    }

    public static DateTimeFormatter yyyyMMdd() {
        return ofPattern("yyyyMMdd");
    }

    public static DateTimeFormatter yyyy_MM_dd() {
        return ofPattern("yyyy-MM-dd");
    }

    public static DateTimeFormatter yyyy_MM_dd_korean() {
        return ofPattern("yyyy년 MM월 dd일", Locale.KOREAN);
    }

    public static DateTimeFormatter yyyy_MM_dd_HH_korean() {
        return ofPattern("yyyy년 MM월 dd일 HH시", Locale.KOREAN);
    }

    public static DateTimeFormatter yyyy_MM_dd_hh_mm_ss_a() {
        return ofPattern("yyyy-MM-dd hh:mm:ss a", getAmPmSupportLocale());
    }

    public static DateTimeFormatter yyyy_MM_dd_HH_mm() {
        return ofPattern("yyyy-MM-dd HH:mm");
    }

    private static Locale getAmPmSupportLocale() {
        // Use Locale.US to format the time with "AM/PM" instead of "오전/오후"
        return Locale.US;
    }
}
