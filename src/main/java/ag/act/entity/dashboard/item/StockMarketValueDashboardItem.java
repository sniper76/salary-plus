package ag.act.entity.dashboard.item;

import ag.act.util.StockMarketValueUtil;

public class StockMarketValueDashboardItem extends DashboardItem {

    public StockMarketValueDashboardItem(Long current, Long before) {
        super("시가액", "억원", current, before);
    }

    public StockMarketValueDashboardItem(Long current) {
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

        return String.format(
            "%s%s%s",
            getVariationPrefixCharacter(variation),
            StockMarketValueUtil.convertValueToBillionFormatWithExactOneDecimal(Math.abs((Long) variation)),
            unit
        );
    }

    @Override
    public String getCurrentValueTextWithUnit() {
        return String.format(
            "%s%s",
            StockMarketValueUtil.convertValueToBillionFormatWithExactOneDecimal(this.current.longValue()),
            unit
        );
    }
}
