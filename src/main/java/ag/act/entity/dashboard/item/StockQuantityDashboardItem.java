package ag.act.entity.dashboard.item;

import java.text.NumberFormat;

public class StockQuantityDashboardItem extends DashboardItem {
    public StockQuantityDashboardItem(Long current, Long before) {
        super("주식수", "주", current, before);
    }

    public StockQuantityDashboardItem(Long current) {
        this(current, current);
    }

    @Override
    public Number calculateVariation() {
        return current.longValue() - before.longValue();
    }

    @Override
    public String getVariationText(Number variation) {
        if (variation.longValue() == 0) {
            return generateNoVariationText();
        }

        final NumberFormat formatter = NumberFormat.getInstance();

        return String.format(
            "%s%s%s",
            getVariationPrefixCharacter(variation),
            formatter.format(Math.abs((Long) variation)),
            unit
        );
    }
}
