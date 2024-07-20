package ag.act.handler.admin;

import ag.act.api.AdminSolidarityApiDelegate;
import ag.act.core.guard.IsAdminGuard;
import ag.act.core.guard.UseGuards;
import ag.act.facade.solidarity.SolidarityFacade;
import ag.act.model.SolidarityDataResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@UseGuards({IsAdminGuard.class})
public class AdminSolidarityApiDelegateImpl implements AdminSolidarityApiDelegate {

    private final SolidarityFacade solidarityFacade;

    public AdminSolidarityApiDelegateImpl(SolidarityFacade solidarityFacade) {
        this.solidarityFacade = solidarityFacade;
    }

    @Override
    public ResponseEntity<SolidarityDataResponse> updateSolidarityToActive(Long solidarityId) {
        return ResponseEntity.ok(solidarityFacade.updateSolidarityToActive(solidarityId));
    }
}
