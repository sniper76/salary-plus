package ag.act.module.stocksearchrecommendation;

import ag.act.converter.stock.SimpleStockResponseConverter;
import ag.act.model.StockSearchRecommendationSectionResponse;
import ag.act.repository.interfaces.SimpleStock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StockSearchRecommendationSectionResponseConverter {

    private final SimpleStockResponseConverter simpleStockResponseConverter;

    public StockSearchRecommendationSectionResponse convert(
        StockSearchRecommendationSectionType sectionType,
        List<SimpleStock> simpleStocks,
        String baseDateTime
    ) {
        return new StockSearchRecommendationSectionResponse()
            .title(sectionType.getTitle())
            .type(sectionType.getValue())
            .baseDateTime(baseDateTime)
            .stocks(simpleStockResponseConverter.convertSimpleStocks(simpleStocks));
    }
}
