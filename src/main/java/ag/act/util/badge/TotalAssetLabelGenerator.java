package ag.act.util.badge;

import ag.act.entity.UserHoldingStock;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
public class TotalAssetLabelGenerator {

    private final BadgeLabelGenerator badgeLabelGenerator;

    public TotalAssetLabelGenerator(BadgeLabelGenerator badgeLabelGenerator) {
        this.badgeLabelGenerator = badgeLabelGenerator;
    }

    public String generate(List<UserHoldingStock> userHoldingStocks) {
        if (CollectionUtils.isEmpty(userHoldingStocks)) {
            return null;
        }

        final Long totalAsset = userHoldingStocks.stream()
            .mapToLong(this::getTotalAssetOfOneStock)
            .reduce(0L, Long::sum);

        return badgeLabelGenerator.generateAssetBadge(totalAsset);
    }

    private Long getTotalAssetOfOneStock(UserHoldingStock userHoldingStock) {
        return userHoldingStock.getQuantity() * userHoldingStock.getStock().getClosingPrice();
    }
}
