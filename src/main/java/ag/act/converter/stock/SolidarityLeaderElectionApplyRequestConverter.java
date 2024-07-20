package ag.act.converter.stock;

import ag.act.dto.solidarity.SolidarityLeaderElectionApplyDto;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus;
import ag.act.model.SolidarityLeaderElectionApplyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SolidarityLeaderElectionApplyRequestConverter {

    public SolidarityLeaderElectionApplyDto convert(SolidarityLeaderElectionApplyRequest request, String stockCode) {
        return convert(request, stockCode, null);
    }

    public SolidarityLeaderElectionApplyDto convert(
        SolidarityLeaderElectionApplyRequest request,
        String stockCode,
        Long solidarityLeaderApplicantId
    ) {
        return convert(
            request,
            stockCode,
            null,
            solidarityLeaderApplicantId
        );
    }

    public SolidarityLeaderElectionApplyDto convert(
        SolidarityLeaderElectionApplyRequest request,
        String stockCode,
        Long solidarityLeaderElectionId,
        Long solidarityLeaderApplicantId
    ) {
        return new SolidarityLeaderElectionApplyDto(
            stockCode,
            solidarityLeaderApplicantId,
            SolidarityLeaderElectionApplyStatus.valueOf(request.getApplyStatus()),
            request.getReasonsForApply(),
            request.getKnowledgeOfCompanyManagement(),
            request.getGoals(),
            request.getCommentsForStockHolder(),
            solidarityLeaderElectionId
        );
    }
}
