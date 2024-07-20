package ag.act.module.krx;

import ag.act.entity.Stock;
import org.springframework.stereotype.Component;

@Component
public class KrxStockMapper {
    public Stock mergeStocks(Stock sourceStock, Stock targetStock) {
        targetStock.setStandardCode(sourceStock.getStandardCode());
        targetStock.setName(sourceStock.getName());
        targetStock.setFullName(sourceStock.getFullName());
        targetStock.setMarketType(sourceStock.getMarketType());
        targetStock.setStockType(sourceStock.getStockType());
        targetStock.setClosingPrice(sourceStock.getClosingPrice());
        targetStock.setTotalIssuedQuantity(sourceStock.getTotalIssuedQuantity());
        return targetStock;
    }
}
