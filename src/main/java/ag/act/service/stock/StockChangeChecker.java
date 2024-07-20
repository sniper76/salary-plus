package ag.act.service.stock;

import ag.act.entity.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockChangeChecker {

    private static final double STOCK_QUANTITY_DIFF_THRESHOLD = 0.2;
    private final StockChangeNotifier stockChangeNotifier;

    public void checkStockTotalIssuedQuantity(Stock newStock, Stock originalStock) {

        if (!isOverThreshold(originalStock, newStock)) {
            return;
        }

        stockChangeNotifier.notify(
            originalStock.getName(),
            originalStock.getCode(),
            originalStock.getTotalIssuedQuantity(),
            newStock.getTotalIssuedQuantity()
        );
    }

    private boolean isOverThreshold(Stock originalStock, Stock newStock) {
        final long diff = Math.abs(newStock.getTotalIssuedQuantity() - originalStock.getTotalIssuedQuantity());

        return diff >= get10PercentOfTotalIssuedQuantity(newStock)
            || diff >= get10PercentOfTotalIssuedQuantity(originalStock);
    }

    private double get10PercentOfTotalIssuedQuantity(Stock stock) {
        return stock.getTotalIssuedQuantity() * STOCK_QUANTITY_DIFF_THRESHOLD;
    }
}
