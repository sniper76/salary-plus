package ag.act.util;

import org.apache.commons.lang3.StringUtils;

public class NumberUtil {

    public static Integer toInteger(String value, Integer defaultValue) {
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static Long toLong(String value, Long defaultValue) {
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }

        try {
            return Double.valueOf(value.trim()).longValue();
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static Long toLong(String value) {
        return toLong(value, 0L);
    }

    public static long getPercentage(Long numerator, long denominator) {
        if (denominator == 0) {
            return 0;
        }
        return numerator * 100 / denominator;
    }
}
