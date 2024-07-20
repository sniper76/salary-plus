package ag.act.converter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class DateTimeConverter {
    private DateTimeConverter() {
        // No instance please.
    }

    public static Instant convert(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(ZoneOffset.systemDefault()).toInstant();
    }

    public static LocalDateTime convert(Instant instant) {
        if (instant == null) {
            return null;
        }
        return LocalDateTime.ofInstant(instant, ZoneOffset.systemDefault());
    }
}
