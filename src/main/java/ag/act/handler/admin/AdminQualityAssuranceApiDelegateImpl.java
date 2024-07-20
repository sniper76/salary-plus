package ag.act.handler.admin;

import ag.act.api.AdminQualityAssuranceApiDelegate;
import ag.act.core.guard.IsAdminGuard;
import ag.act.core.guard.UseGuards;
import ag.act.facade.admin.AdminQualityAssuranceFacade;
import ag.act.model.CreateOrUpdateTestStockRequest;
import ag.act.model.SimpleStringResponse;
import ag.act.util.SimpleStringResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@UseGuards(IsAdminGuard.class)
public class AdminQualityAssuranceApiDelegateImpl implements AdminQualityAssuranceApiDelegate {

    private final AdminQualityAssuranceFacade adminQualityAssuranceFacade;

    public AdminQualityAssuranceApiDelegateImpl(AdminQualityAssuranceFacade adminQualityAssuranceFacade) {
        this.adminQualityAssuranceFacade = adminQualityAssuranceFacade;
    }

    @Override
    public ResponseEntity<SimpleStringResponse> createOrUpdateTestStock(
        String stockCode,
        CreateOrUpdateTestStockRequest createOrUpdateTestStockRequest
    ) {
        adminQualityAssuranceFacade.createOrUpdateTestStock(stockCode, createOrUpdateTestStockRequest);
        return SimpleStringResponseUtil.okResponse();
    }
}
