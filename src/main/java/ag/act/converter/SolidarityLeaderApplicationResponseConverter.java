package ag.act.converter;

import ag.act.converter.stock.SimpleStockResponseConverter;
import ag.act.converter.stock.SolidarityLeaderApplicantConverter;
import ag.act.dto.SolidarityLeaderApplicationDto;
import ag.act.entity.User;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus;
import ag.act.model.SolidarityLeaderApplicationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SolidarityLeaderApplicationResponseConverter {

    private final SimpleStockResponseConverter simpleStockResponseConverter;
    private final SolidarityLeaderApplicantConverter solidarityLeaderApplicantConverter;

    public SolidarityLeaderApplicationResponse convert(SolidarityLeaderApplicationDto solidarityLeaderApplicationDto) {
        final User user = solidarityLeaderApplicationDto.getUser();
        final SolidarityLeaderElectionApplyStatus applyStatus = solidarityLeaderApplicationDto.getApplyStatus();

        return new SolidarityLeaderApplicationResponse()
            .user(solidarityLeaderApplicantConverter.convert(user))
            .stock(simpleStockResponseConverter.convert(solidarityLeaderApplicationDto.getSimpleStock()))
            .solidarityLeaderApplicantId(solidarityLeaderApplicationDto.getSolidarityLeaderApplicantId())
            .applyStatus(applyStatus == null ? null : applyStatus.name())
            .reasonsForApply(solidarityLeaderApplicationDto.getReasonsForApply())
            .knowledgeOfCompanyManagement(solidarityLeaderApplicationDto.getKnowledgeOfCompanyManagement())
            .goals(solidarityLeaderApplicationDto.getGoals())
            .commentsForStockHolder(solidarityLeaderApplicationDto.getCommentsForStockHolder());
    }
}
