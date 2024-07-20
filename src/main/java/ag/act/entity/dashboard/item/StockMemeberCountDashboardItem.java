package ag.act.entity.dashboard.item;

import java.text.NumberFormat;

public class StockMemeberCountDashboardItem extends DashboardItem {
    public StockMemeberCountDashboardItem(Integer current, Integer before) {
        super("주주수", "명", current, before);
    }

    public StockMemeberCountDashboardItem(Integer current) {
        this(current, current);
    }

    @Override
    public Number calculateVariation() {
        return current.intValue() - before.intValue();
    }

    @Override
    public String getVariationText(Number variation) {
        if (variation.intValue() == 0) {
            return generateNoVariationText();
        }

        final NumberFormat formatter = NumberFormat.getInstance();

        return String.format(
            "%s%s%s",
            getVariationPrefixCharacter(variation),
            formatter.format(Math.abs((Integer) variation)),
            unit
        );
    }
}
