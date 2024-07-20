package ag.act.handler;

import ag.act.api.CommonApiDelegate;
import ag.act.facade.stock.StockFacade;
import ag.act.model.GetSimpleStockDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CommonApiDelegateImpl implements CommonApiDelegate {

    private final StockFacade stockFacade;

    @Override
    public ResponseEntity<GetSimpleStockDataResponse> getCommonStocks() {
        return ResponseEntity.ok(
            new GetSimpleStockDataResponse()
                .data(stockFacade.getSimpleStocksWithoutTestStocks()));
    }
}
