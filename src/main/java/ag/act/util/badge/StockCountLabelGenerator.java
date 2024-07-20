package ag.act.util.badge;

import ag.act.entity.UserHoldingStock;
import org.springframework.stereotype.Component;

@Component
public class StockCountLabelGenerator {

    private final BadgeLabelGenerator badgeLabelGenerator;

    public StockCountLabelGenerator(BadgeLabelGenerator badgeLabelGenerator) {
        this.badgeLabelGenerator = badgeLabelGenerator;
    }

    public String generate(UserHoldingStock userHoldingStock) {
        if (userHoldingStock == null) {
            return null;
        }

        return badgeLabelGenerator.generateStockQuantityBadge(userHoldingStock.getQuantity());
    }
}
