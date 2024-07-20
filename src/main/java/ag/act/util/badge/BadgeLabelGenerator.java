package ag.act.util.badge;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class BadgeLabelGenerator {
    public String generateStockQuantityBadge(Long quantity) {
        // 주식수: 1+/100+/500+/1000+/5000+/1만+/5만+/10만+/50만+/100만+/500만+/1000만+/5000만+/1억+
        if (quantity == 0) {
            return null;
        }

        if (quantity >= 100_000_000L) {
            return "1억주+";
        } else if (quantity >= 10_000L) {
            return formatBadgeWithScalePostfix(quantity / 10_000L, "만주+");
        } else if (quantity >= 100L) {
            return formatBadgeWithScalePostfix(quantity, "주+");
        } else {
            return "1주+";
        }
    }

    private String formatBadgeWithScalePostfix(Long quantity, String postfix) {
        final String quantityString = quantity.toString();
        final int zeroSize = quantityString.length();
        final int firstNumber = Integer.parseInt(quantityString.substring(0, 1));

        if (firstNumber >= 5) {
            return StringUtils.rightPad("5", zeroSize, "0") + postfix;
        }
        return StringUtils.rightPad("1", zeroSize, "0") + postfix;
    }

    public String generateAssetBadge(Long asset) {
        // 총자산: 1억+/10억+(1억미만은 미표시)

        if (asset >= 10_000_000_000L) {
            return "100억+";
        } else if (asset >= 1_000_000_000L) {
            return "10억+";
        } else if (asset >= 100_000_000L) {
            return "1억+";
        }

        return null;
    }
}
