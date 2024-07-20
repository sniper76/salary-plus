package ag.act.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class DecimalFormatUtil {
    private static final DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private static final DecimalFormat decimalFormatOmitFollowingZero = new DecimalFormat("#.##");

    public static String formatWithTwoDecimalPlaces(double value) {
        BigDecimal bigDecimalValue = new BigDecimal(Double.toString(value));
        bigDecimalValue = bigDecimalValue.setScale(2, RoundingMode.HALF_UP);

        return decimalFormat.format(bigDecimalValue);
    }

    public static String formatWithoutFollowingZero(double value) {
        BigDecimal bigDecimalValue = new BigDecimal(Double.toString(value));
        bigDecimalValue = bigDecimalValue.setScale(2, RoundingMode.HALF_UP);

        return decimalFormatOmitFollowingZero.format(bigDecimalValue);
    }

    public static double twoDecimalPlaceDouble(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
