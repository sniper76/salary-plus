package ag.act.converter.stock;

import ag.act.dto.StockReferenceDateDto;
import ag.act.entity.StockReferenceDate;
import org.springframework.stereotype.Component;

@Component
public class StockReferenceDateRequestConverter {
    public StockReferenceDate convert(StockReferenceDateDto stockReferenceDateDto) {
        final StockReferenceDate stockReferenceDate = new StockReferenceDate();

        stockReferenceDate.setStockCode(stockReferenceDateDto.getStockCode());
        stockReferenceDate.setReferenceDate(stockReferenceDateDto.getReferenceDate());

        return stockReferenceDate;
    }

    public StockReferenceDateDto convert(String stockCode, ag.act.model.CreateStockReferenceDateRequest createStockReferenceDateRequest) {
        return StockReferenceDateDto.builder()
            .stockCode(stockCode)
            .referenceDate(createStockReferenceDateRequest.getReferenceDate())
            .build();
    }
}
