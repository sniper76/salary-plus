package ag.act.converter.stock;

import ag.act.dto.SolidarityLeaderApplicationDto;
import ag.act.model.SolidarityLeaderApplicationResponse;
import org.springframework.stereotype.Component;

@Component
public class SolidarityLeaderApplicationResponseForStockHomeConverter {

    public SolidarityLeaderApplicationResponse convert(SolidarityLeaderApplicationDto solidarityLeaderApplicationDto) {
        return new SolidarityLeaderApplicationResponse()
            .solidarityLeaderElectionId(solidarityLeaderApplicationDto.getSolidarityLeaderElectionId())
            .applyStatus(solidarityLeaderApplicationDto.getApplyStatus().name())
            .solidarityLeaderApplicantId(solidarityLeaderApplicationDto.getSolidarityLeaderApplicantId());
    }
}
