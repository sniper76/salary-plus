package ag.act.entity.dashboard.item;

import ag.act.constants.ActColors;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.text.NumberFormat;

@AllArgsConstructor
@Getter
public abstract class DashboardItem implements ActColors {
    protected String title;
    protected String unit;
    protected Number current;
    protected Number before;

    protected abstract Number calculateVariation();

    protected abstract String getVariationText(Number variation);

    protected String generateNoVariationText() {
        return "-";
    }

    public String getCurrentValueTextWithUnit() {
        final NumberFormat formatter = NumberFormat.getInstance();

        return String.format("%s%s", formatter.format(current), unit);
    }

    public ag.act.model.DashboardItemResponseVariation getVariationResponse() {
        final Number variation = calculateVariation();

        return new ag.act.model.DashboardItemResponseVariation()
            .text(getVariationText(variation))
            .color(getVariationTextColor(variation));
    }

    protected String getVariationTextColor(Number variation) {
        if (variation.doubleValue() == 0) {
            return COLOR_BLACK;
        } else if (variation.doubleValue() > 0) {
            return COLOR_RED;
        } else {
            return COLOR_BLUE;
        }
    }

    protected String getVariationPrefixCharacter(Number variation) {
        if (variation.doubleValue() == 0) {
            return "";
        } else if (variation.doubleValue() > 0) {
            return "▲" + " ";
        } else {
            return "▼" + " ";
        }
    }
}
