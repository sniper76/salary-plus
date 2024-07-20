package ag.act.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeParser {
    private static final DateTimeFormatter FORMATTER_YYYYMMDD = DateTimeFormatter.ofPattern("yyyyMMdd");

    private DateTimeParser() {
        //
    }

    public static LocalDateTime parseDate(String date) {
        return LocalDate.parse(date, FORMATTER_YYYYMMDD).atStartOfDay();
    }

    public static String toDate(LocalDateTime date) {
        return date.format(FORMATTER_YYYYMMDD);
    }
}
