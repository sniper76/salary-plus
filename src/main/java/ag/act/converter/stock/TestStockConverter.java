package ag.act.converter.stock;

import ag.act.entity.Stock;
import ag.act.entity.TestStock;
import org.springframework.stereotype.Component;

@Component
public class TestStockConverter {
    public Stock convert(TestStock testStock) {
        final Stock stock = new Stock();

        stock.setCode(testStock.getCode());
        stock.setName(testStock.getName());
        stock.setStandardCode("ACT" + testStock.getCode() + "999");
        stock.setFullName(testStock.getName() + "액트주");
        stock.setMarketType("CONDUIT");
        stock.setStockType("액트주");
        stock.setClosingPrice(10_000);
        stock.setTotalIssuedQuantity(1_000_000L);

        return stock;
    }
}
