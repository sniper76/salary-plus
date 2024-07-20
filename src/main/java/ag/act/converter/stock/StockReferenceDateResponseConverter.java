package ag.act.converter.stock;

import ag.act.converter.Converter;
import ag.act.entity.StockReferenceDate;
import ag.act.model.StockReferenceDateResponse;
import org.springframework.stereotype.Component;

@Component
public class StockReferenceDateResponseConverter implements Converter<StockReferenceDate, ag.act.model.StockReferenceDateResponse> {
    private ag.act.model.StockReferenceDateResponse convert(StockReferenceDate stockReferenceDate) {
        return new ag.act.model.StockReferenceDateResponse()
            .id(stockReferenceDate.getId())
            .stockCode(stockReferenceDate.getStockCode())
            .referenceDate(stockReferenceDate.getReferenceDate());
    }

    @Override
    public StockReferenceDateResponse apply(StockReferenceDate stockReferenceDate) {
        return convert(stockReferenceDate);
    }
}
