package ag.act.handler.admin;

import ag.act.api.AdminSolidarityLeaderApplicantApiDelegate;
import ag.act.core.guard.IsAdminGuard;
import ag.act.core.guard.UseGuards;
import ag.act.facade.solidarity.leader.AdminSolidarityLeaderApplicantFacade;
import ag.act.model.SimpleStringResponse;
import ag.act.model.SolidarityLeaderApplicationResponse;
import ag.act.model.WithdrawSolidarityLeaderApplicantRequest;
import ag.act.util.SimpleStringResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@UseGuards(IsAdminGuard.class)
public class AdminSolidarityLeaderApplicantApiDelegateImpl implements AdminSolidarityLeaderApplicantApiDelegate {

    private final AdminSolidarityLeaderApplicantFacade adminSolidarityLeaderApplicantFacade;

    @Override
    public ResponseEntity<SimpleStringResponse> withdrawSolidarityLeaderApplicant(
        String stockCode,
        Long solidarityLeaderApplicantId,
        WithdrawSolidarityLeaderApplicantRequest withdrawSolidarityLeaderApplicantRequest
    ) {
        return ResponseEntity.ok(
            adminSolidarityLeaderApplicantFacade.withdrawApplicant(
                stockCode,
                solidarityLeaderApplicantId,
                withdrawSolidarityLeaderApplicantRequest
            )
        );
    }

    @Override
    public ResponseEntity<SimpleStringResponse> deleteSolidarityLeaderApplicant(
        Long solidarityId,
        ag.act.model.DeleteSolidarityApplicantRequest deleteSolidarityApplicantRequest
    ) {
        adminSolidarityLeaderApplicantFacade.cancelLeader(deleteSolidarityApplicantRequest);

        return SimpleStringResponseUtil.okResponse();
    }

    @Override
    public ResponseEntity<SolidarityLeaderApplicationResponse> getSolidarityLeaderApplicant(
        String stockCode,
        Long solidarityLeaderElectionId,
        Long solidarityLeaderApplicantId
    ) {
        return ResponseEntity.ok(
            adminSolidarityLeaderApplicantFacade.getSolidarityLeaderApplicant(solidarityLeaderElectionId, solidarityLeaderApplicantId)
        );
    }
}
