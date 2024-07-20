package ag.act.handler.stock;

import ag.act.api.AdminPrivateStockApiDelegate;
import ag.act.core.guard.IsAdminGuard;
import ag.act.core.guard.UseGuards;
import ag.act.facade.admin.stock.AdminStockFacade;
import ag.act.model.CreatePrivateStockRequest;
import ag.act.model.SimpleStringResponse;
import ag.act.util.SimpleStringResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@UseGuards(IsAdminGuard.class)
@RequiredArgsConstructor
@Service
public class AdminPrivateStockApiDelegateImpl implements AdminPrivateStockApiDelegate {

    private final AdminStockFacade adminStockFacade;

    @Override
    public ResponseEntity<SimpleStringResponse> createPrivateStock(CreatePrivateStockRequest createPrivateStockRequest) {
        adminStockFacade.createPrivateStock(createPrivateStockRequest);
        return ResponseEntity.ok(SimpleStringResponseUtil.ok());
    }
}
