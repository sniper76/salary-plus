package ag.act.handler.admin.acceptor;

import ag.act.api.AdminStockAcceptorUserApiDelegate;
import ag.act.core.guard.IsAdminGuard;
import ag.act.core.guard.UseGuards;
import ag.act.facade.admin.stock.AdminStockAcceptorUserFacade;
import ag.act.model.CreateStockAcceptorUserRequest;
import ag.act.model.DeleteStockAcceptorUserRequest;
import ag.act.model.SimpleStringResponse;
import ag.act.util.SimpleStringResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@UseGuards(IsAdminGuard.class)
public class AdminStockAcceptorUserApiDelegateImpl implements AdminStockAcceptorUserApiDelegate {

    private final AdminStockAcceptorUserFacade adminStockAcceptorUserFacade;

    @Override
    public ResponseEntity<SimpleStringResponse> createStockAcceptorUser(
        String code, CreateStockAcceptorUserRequest createStockAcceptorUserRequest
    ) {
        adminStockAcceptorUserFacade.createStockAcceptorUser(
            code, createStockAcceptorUserRequest.getUserId()
        );
        return ResponseEntity.ok(SimpleStringResponseUtil.ok());
    }

    @Override
    public ResponseEntity<SimpleStringResponse> deleteStockAcceptorUser(
        String code, DeleteStockAcceptorUserRequest deleteStockAcceptorUserRequest
    ) {
        adminStockAcceptorUserFacade.deleteStockAcceptorUser(
            code, deleteStockAcceptorUserRequest.getUserId()
        );
        return ResponseEntity.ok(SimpleStringResponseUtil.ok());
    }
}
