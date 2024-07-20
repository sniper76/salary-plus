package ag.act.converter.stock;

import ag.act.entity.PrivateStock;
import ag.act.entity.Stock;
import ag.act.model.CreatePrivateStockRequest;
import ag.act.model.Status;
import org.springframework.stereotype.Component;

@Component
public class PrivateStockConverter {
    public Stock convert(PrivateStock privateStock) {
        final Stock stock = new Stock();

        stock.setCode(privateStock.getCode());
        stock.setName(privateStock.getName());
        stock.setStandardCode(privateStock.getStandardCode());
        stock.setFullName(privateStock.getFullName());
        stock.setMarketType(privateStock.getMarketType());
        stock.setStockType(privateStock.getStockType());
        stock.setClosingPrice(privateStock.getClosingPrice());
        stock.setTotalIssuedQuantity(privateStock.getTotalIssuedQuantity());
        stock.setStatus(privateStock.getStatus());

        return stock;
    }

    public PrivateStock convert(CreatePrivateStockRequest request) {
        PrivateStock privateStock = new PrivateStock();
        privateStock.setCode(request.getCode());
        privateStock.setName(request.getName());
        privateStock.setStandardCode(request.getStandardCode());
        privateStock.setFullName(request.getName());
        privateStock.setStockType(request.getStockType());
        privateStock.setClosingPrice(request.getClosingPrice());
        privateStock.setTotalIssuedQuantity(request.getTotalIssuedQuantity());
        privateStock.setStatus(Status.ACTIVE);

        return privateStock;
    }
}
