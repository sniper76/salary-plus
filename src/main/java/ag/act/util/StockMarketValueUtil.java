package ag.act.util;

import java.text.NumberFormat;

public class StockMarketValueUtil {

    private static final double BILLION_DIVISOR = 100_000_000.0;

    public static String convertValueToBillionFormatWithExactOneDecimal(long value) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMinimumFractionDigits(1);
        numberFormat.setMaximumFractionDigits(1);

        return numberFormat.format(value / BILLION_DIVISOR);
    }
}
