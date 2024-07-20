package ag.act.facade.solidarity.leader;

import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.SolidarityLeaderApplicationResponseConverter;
import ag.act.converter.SolidarityLeaderElectionProcedureConverter;
import ag.act.converter.stock.SolidarityLeaderApplicantConverter;
import ag.act.dto.SolidarityLeaderApplicationDto;
import ag.act.dto.solidarity.SolidarityLeaderElectionApplyDto;
import ag.act.entity.User;
import ag.act.enums.SolidarityLeaderElectionProcedure;
import ag.act.model.GetSolidarityLeaderApplicantResponse;
import ag.act.model.SolidarityLeaderApplicationResponse;
import ag.act.model.SolidarityLeaderElectionProceduresDataResponse;
import ag.act.service.solidarity.election.SolidarityLeaderElectionService;
import ag.act.validator.solidarity.BlockedSolidarityLeaderElectionApplicantsValidator;
import ag.act.validator.solidarity.SolidarityLeaderElectionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SolidarityLeaderElectionFacade {
    private final BlockedSolidarityLeaderElectionApplicantsValidator solidarityLeaderElectionApplicantsValidator;
    private final SolidarityLeaderElectionProcedureConverter solidarityLeaderElectionProcedureConverter;
    private final SolidarityLeaderApplicantConverter solidarityLeaderApplicantConverter;
    private final SolidarityLeaderApplicationResponseConverter solidarityLeaderApplicationResponseConverter;
    private final SolidarityLeaderElectionService solidarityLeaderElectionService;
    private final SolidarityLeaderElectionValidator solidarityLeaderElectionValidator;

    public SolidarityLeaderElectionProceduresDataResponse getProcedures() {
        return solidarityLeaderElectionProcedureConverter.convert(SolidarityLeaderElectionProcedure.getSortedProcedures());
    }

    public void createSolidarityLeaderApplicant(SolidarityLeaderElectionApplyDto solidarityLeaderElectionApplyDto) {
        solidarityLeaderElectionApplicantsValidator.validate(solidarityLeaderElectionApplyDto, ActUserProvider.getNoneNull());
        solidarityLeaderElectionService.createSolidarityLeaderApplicant(solidarityLeaderElectionApplyDto);
    }

    public void updateSolidarityLeaderApplicant(SolidarityLeaderElectionApplyDto solidarityLeaderElectionApplyDto) {
        solidarityLeaderElectionService.updateSolidarityLeaderApplicant(solidarityLeaderElectionApplyDto);
    }

    public GetSolidarityLeaderApplicantResponse getSolidarityLeaderApplicants(String stockCode, Long solidarityLeaderElectionId) {
        return solidarityLeaderApplicantConverter.convert(
            solidarityLeaderElectionService.getSolidarityLeaderApplicants(stockCode, solidarityLeaderElectionId)
        );
    }

    public SolidarityLeaderApplicationResponse getSolidarityLeaderApplication(
        String stockCode,
        Long solidarityLeaderElectionId,
        Long solidarityLeaderApplicantId
    ) {
        final SolidarityLeaderApplicationDto application = solidarityLeaderElectionService.getApplication(
            solidarityLeaderElectionId,
            solidarityLeaderApplicantId
        );

        solidarityLeaderElectionValidator.validateSameStockCode(stockCode, application);

        return solidarityLeaderApplicationResponseConverter.convert(application);
    }

    public void withdrawSolidarityLeaderApplicant(Long solidarityLeaderElectionId, Long solidarityLeaderApplicantId) {
        solidarityLeaderElectionService.withdrawSolidarityLeaderApplicant(solidarityLeaderElectionId, solidarityLeaderApplicantId);
    }

    public SolidarityLeaderApplicationResponse getLatestApplication(String stockCode) {
        final User user = ActUserProvider.getNoneNull();

        return solidarityLeaderElectionService.findLatestApplication(stockCode, user.getId())
            .map(SolidarityLeaderApplicationDto::removeOldSolidarityLeaderElectionData)
            .map(solidarityLeaderApplicationResponseConverter::convert)
            .orElse(null);
    }
}
