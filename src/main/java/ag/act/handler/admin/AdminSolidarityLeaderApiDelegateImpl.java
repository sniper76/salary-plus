package ag.act.handler.admin;

import ag.act.api.AdminSolidarityLeaderApiDelegate;
import ag.act.core.guard.IsAdminGuard;
import ag.act.core.guard.UseGuards;
import ag.act.model.CreateSolidarityLeaderForCorporateUserRequest;
import ag.act.model.SimpleStringResponse;
import ag.act.service.solidarity.election.SolidarityLeaderApplicantService;
import ag.act.service.solidarity.election.SolidarityLeaderService;
import ag.act.util.SimpleStringResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@UseGuards(IsAdminGuard.class)
public class AdminSolidarityLeaderApiDelegateImpl implements AdminSolidarityLeaderApiDelegate {

    private final SolidarityLeaderApplicantService solidarityLeaderApplicantService;
    private final SolidarityLeaderService solidarityLeaderService;

    @Override
    public ResponseEntity<SimpleStringResponse> createSolidarityLeader(
        Long solidarityId, ag.act.model.CreateSolidarityLeaderRequest createSolidarityLeaderRequest
    ) {
        solidarityLeaderApplicantService.electLeaderApplicant(createSolidarityLeaderRequest.getSolidarityApplicantId());

        return SimpleStringResponseUtil.okResponse();
    }

    @Override
    public ResponseEntity<ag.act.model.SimpleStringResponse> updateSolidarityLeaderMessageByAdmin(
        Long solidarityId, ag.act.model.UpdateSolidarityLeaderMessageRequest updateSolidarityLeaderMessageRequest
    ) {
        solidarityLeaderService.updateSolidarityLeaderMessage(solidarityId, updateSolidarityLeaderMessageRequest);

        return SimpleStringResponseUtil.okResponse();
    }

    @Override
    public ResponseEntity<ag.act.model.SimpleStringResponse> dismissSolidarityLeader(
        Long solidarityId, ag.act.model.DismissSolidarityLeaderRequest dismissSolidarityLeaderRequest
    ) {
        solidarityLeaderService.dismissSolidarityLeader(dismissSolidarityLeaderRequest.getSolidarityLeaderId());

        return SimpleStringResponseUtil.okResponse();
    }

    @Override
    public ResponseEntity<SimpleStringResponse> createSolidarityLeaderForCorporateUser(
        Long solidarityId, CreateSolidarityLeaderForCorporateUserRequest createSolidarityLeaderForCorporateUserRequest
    ) {
        solidarityLeaderService.createSolidarityLeaderForCorporateUser(solidarityId, createSolidarityLeaderForCorporateUserRequest);
        return SimpleStringResponseUtil.okResponse();
    }
}
