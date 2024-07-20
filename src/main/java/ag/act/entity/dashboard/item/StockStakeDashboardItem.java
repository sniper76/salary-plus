package ag.act.entity.dashboard.item;

import ag.act.util.DecimalFormatUtil;

public class StockStakeDashboardItem extends DashboardItem {
    public StockStakeDashboardItem(Double current, Double before) {
        super("지분율", "%", current, before);
    }

    public StockStakeDashboardItem(Double current) {
        this(current, current);
    }

    @Override
    public Number calculateVariation() {
        return current.doubleValue() - before.doubleValue();
    }

    @Override
    public String getVariationText(Number variation) {
        if (Math.abs(variation.doubleValue() - 0) < 1e-7) {
            return generateNoVariationText();
        }

        return String.format(
            "%s%s%s",
            getVariationPrefixCharacter(variation),
            DecimalFormatUtil.formatWithTwoDecimalPlaces(Math.abs((Double) variation)),
            unit
        );
    }

    @Override
    public String getCurrentValueTextWithUnit() {
        return String.format(
            "%s%s",
            DecimalFormatUtil.formatWithTwoDecimalPlaces(this.current.doubleValue()),
            unit
        );
    }
}
