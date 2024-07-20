package ag.act.facade.stock.searchtrend;


import ag.act.configuration.security.ActUserProvider;
import ag.act.model.CreateStockSearchTrendRequest;
import ag.act.service.stocksearchtrend.StockSearchTrendService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Transactional
public class StockSearchTrendFacade {

    private final StockSearchTrendService stockSearchTrendService;

    public void createStockSearchTrend(CreateStockSearchTrendRequest createStockSearchTrendRequest) {
        stockSearchTrendService.createStockSearchTrend(
            createStockSearchTrendRequest.getStockCode(),
            ActUserProvider.getNoneNull().getId()
        );
    }
}
