package ag.act.converter.stock;

import ag.act.entity.Stock;
import ag.act.model.StockDataArrayResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StockDataArrayResponseConverter {
    private final StockResponseConverter stockResponseConverter;

    public StockDataArrayResponseConverter(StockResponseConverter stockResponseConverter) {
        this.stockResponseConverter = stockResponseConverter;
    }

    public ag.act.model.StockDataArrayResponse convert(List<Stock> stocks) {
        return new StockDataArrayResponse()
            .data(stocks.stream().map(stockResponseConverter::convert).toList());
    }
}
