package ag.act.facade.stock;

import ag.act.converter.StockRankingConverter;
import ag.act.model.StockRankingDataResponse;
import ag.act.service.admin.stock.StockRankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockRankingFacade {

    private final StockRankingService stockRankingService;
    private final StockRankingConverter stockRankingConverter;

    public ag.act.model.StockRankingDataResponse getTopNStockRankings(PageRequest pageRequest) {
        StockRankingDataResponse stockRankingDataResponse = new StockRankingDataResponse();
        stockRankingDataResponse.setData(stockRankingConverter.convert(stockRankingService.findTopNStockRankings(pageRequest)));

        return stockRankingDataResponse;
    }
}
