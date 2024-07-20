package ag.act.converter.stock;

import ag.act.converter.Converter;
import ag.act.entity.Stock;
import ag.act.model.SimpleStockResponse;
import ag.act.repository.interfaces.SimpleStock;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SimpleStockResponseConverter implements Converter<Stock, SimpleStockResponse> {

    public SimpleStockResponse convert(Stock stock) {
        return new SimpleStockResponse()
            .code(stock.getCode())
            .name(stock.getName())
            .standardCode(stock.getStandardCode());
    }

    public SimpleStockResponse convert(SimpleStock simpleStock) {
        return new SimpleStockResponse()
            .code(simpleStock.getCode())
            .name(simpleStock.getName())
            .standardCode(simpleStock.getStandardCode());
    }

    public List<SimpleStockResponse> convertSimpleStocks(List<SimpleStock> simpaleStockList) {
        return simpaleStockList.stream()
            .map(this::convert)
            .toList();
    }

    @Override
    public SimpleStockResponse apply(Stock stock) {
        return convert(stock);
    }
}
