package ag.act.handler.stocksearchtrend;

import ag.act.api.StockSearchTrendsApiDelegate;
import ag.act.facade.stock.searchtrend.StockSearchTrendFacade;
import ag.act.model.CreateStockSearchTrendRequest;
import ag.act.model.SimpleStringResponse;
import ag.act.util.SimpleStringResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockSearchTrendsApiDelegateImpl implements StockSearchTrendsApiDelegate {

    private final StockSearchTrendFacade stockSearchTrendFacade;

    @Override
    public ResponseEntity<SimpleStringResponse> createStockSearchTrend(CreateStockSearchTrendRequest createStockSearchTrendRequest) {
        stockSearchTrendFacade.createStockSearchTrend(createStockSearchTrendRequest);

        return SimpleStringResponseUtil.okResponse();
    }
}
