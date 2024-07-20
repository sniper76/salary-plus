package ag.act.facade.solidarity.leader;

import ag.act.converter.SolidarityLeaderApplicationResponseConverter;
import ag.act.dto.SolidarityLeaderApplicationDto;
import ag.act.entity.SolidarityLeaderApplicant;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus;
import ag.act.exception.BadRequestException;
import ag.act.model.DeleteSolidarityApplicantRequest;
import ag.act.model.SimpleStringResponse;
import ag.act.model.SolidarityLeaderApplicationResponse;
import ag.act.model.WithdrawSolidarityLeaderApplicantRequest;
import ag.act.service.solidarity.election.BlockedSolidarityLeaderApplicantService;
import ag.act.service.solidarity.election.SolidarityLeaderApplicantService;
import ag.act.service.solidarity.election.SolidarityLeaderElectionService;
import ag.act.util.SimpleStringResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AdminSolidarityLeaderApplicantFacade {

    private final SolidarityLeaderApplicantService solidarityLeaderApplicantService;
    private final SolidarityLeaderApplicationResponseConverter solidarityLeaderApplicationResponseConverter;
    private final BlockedSolidarityLeaderApplicantService blockedSolidarityLeaderApplicantService;
    private final SolidarityLeaderElectionService solidarityLeaderElectionService;

    public void cancelLeader(DeleteSolidarityApplicantRequest deleteSolidarityApplicantRequest) {
        solidarityLeaderApplicantService.cancelLeaderApplication(deleteSolidarityApplicantRequest.getSolidarityLeaderApplicantId());
    }

    public SolidarityLeaderApplicationResponse getSolidarityLeaderApplicant(Long solidarityLeaderElectionId, Long solidarityLeaderApplicantId) {
        final SolidarityLeaderApplicationDto applicationDto = solidarityLeaderApplicantService.getSolidarityLeaderApplicationDto(
            solidarityLeaderElectionId,
            solidarityLeaderApplicantId
        );

        return solidarityLeaderApplicationResponseConverter.convert(applicationDto);
    }

    public SimpleStringResponse withdrawApplicant(
        String stockCode,
        Long solidarityLeaderApplicantId,
        WithdrawSolidarityLeaderApplicantRequest request
    ) {
        Long onGoingElectionId = solidarityLeaderElectionService.findOnGoingSolidarityLeaderElection(stockCode)
            .map(SolidarityLeaderElection::getId)
            .orElseThrow(() -> new BadRequestException("관리자가 지원을 철회할 수 있는 기간이 아닙니다."));

        SolidarityLeaderApplicant withdrawnApplicant = solidarityLeaderApplicantService.withdrawLeaderApplicant(
            onGoingElectionId,
            solidarityLeaderApplicantId,
            SolidarityLeaderElectionApplyStatus.DELETED_BY_ADMIN
        );

        blockedSolidarityLeaderApplicantService.createBlockedSolidarityLeaderApplicant(
            withdrawnApplicant.getUserId(),
            stockCode,
            request.getReason()
        );

        return SimpleStringResponseUtil.ok();
    }
}
