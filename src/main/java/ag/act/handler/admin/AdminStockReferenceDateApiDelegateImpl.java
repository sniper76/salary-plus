package ag.act.handler.admin;

import ag.act.api.AdminStockReferenceDateApiDelegate;
import ag.act.core.guard.IsAdminGuard;
import ag.act.core.guard.UseGuards;
import ag.act.facade.stock.StockReferenceDateFacade;
import ag.act.model.CreateStockReferenceDateRequest;
import ag.act.model.SimpleStringResponse;
import ag.act.model.StockReferenceDateDataResponse;
import ag.act.util.SimpleStringResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@UseGuards(IsAdminGuard.class)
public class AdminStockReferenceDateApiDelegateImpl implements AdminStockReferenceDateApiDelegate {
    private final StockReferenceDateFacade stockReferenceDateFacade;

    public AdminStockReferenceDateApiDelegateImpl(StockReferenceDateFacade stockReferenceDateFacade) {
        this.stockReferenceDateFacade = stockReferenceDateFacade;
    }

    @Override
    public ResponseEntity<ag.act.model.StockReferenceDateDataArrayResponse> getStockReferenceDates(String stockCode) {
        return ResponseEntity.ok(
            new ag.act.model.StockReferenceDateDataArrayResponse().data(
                stockReferenceDateFacade.getStockReferenceDates(stockCode)
            )
        );
    }

    @Override
    public ResponseEntity<StockReferenceDateDataResponse> createStockReferenceDate(
        String stockCode,
        CreateStockReferenceDateRequest createStockReferenceDateRequest
    ) {
        return ResponseEntity.ok(
            new ag.act.model.StockReferenceDateDataResponse().data(
                stockReferenceDateFacade.createStockReferenceDate(stockCode, createStockReferenceDateRequest)
            )
        );
    }

    @Override
    public ResponseEntity<SimpleStringResponse> deleteStockReferenceDateAdmin(String stockCode, Long referenceDateId) {
        stockReferenceDateFacade.deleteStockReferenceDate(referenceDateId);
        return SimpleStringResponseUtil.okResponse();
    }

    @Override
    public ResponseEntity<StockReferenceDateDataResponse> updateStockReferenceDate(
        String stockCode, Long stockReferenceDateId, CreateStockReferenceDateRequest updateStockReferenceDateRequest
    ) {
        return ResponseEntity.ok(
            new ag.act.model.StockReferenceDateDataResponse().data(
                stockReferenceDateFacade.updateStockReferenceDate(stockCode, stockReferenceDateId, updateStockReferenceDateRequest)
            )
        );
    }
}
