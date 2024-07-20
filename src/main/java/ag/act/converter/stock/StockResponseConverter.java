package ag.act.converter.stock;

import ag.act.converter.DateTimeConverter;
import ag.act.entity.Stock;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StockResponseConverter {
    public ag.act.model.StockResponse convert(Stock stock) {
        return new ag.act.model.StockResponse()
            .code(stock.getCode())
            .name(stock.getName())
            .totalIssuedQuantity(stock.getTotalIssuedQuantity())
            .representativePhoneNumber(stock.getRepresentativePhoneNumber())
            .status(stock.getStatus())
            .createdAt(DateTimeConverter.convert(stock.getCreatedAt()))
            .updatedAt(DateTimeConverter.convert(stock.getUpdatedAt()));
    }

    public List<ag.act.model.StockResponse> convert(List<Stock> stocks) {
        return stocks.stream()
            .map(this::convert)
            .toList();
    }
}
