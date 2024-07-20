package ag.act.dto.solidarity;

import ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SolidarityLeaderElectionApplyDto {
    String stockCode;
    Long solidarityLeaderApplicantId;
    SolidarityLeaderElectionApplyStatus applyStatus;
    String reasonsForApply;
    String knowledgeOfCompanyManagement;
    String goals;
    String commentsForStockHolder;
    Long solidarityLeaderElectionId;
    SolidarityLeaderElectionStatus electionStatus;

    public SolidarityLeaderElectionApplyDto(
        String stockCode,
        Long solidarityLeaderApplicantId,
        SolidarityLeaderElectionApplyStatus applyStatus,
        String reasonsForApply,
        String knowledgeOfCompanyManagement,
        String goals,
        String commentsForStockHolder,
        Long solidarityLeaderElectionId
    ) {
        this.stockCode = stockCode;
        this.solidarityLeaderApplicantId = solidarityLeaderApplicantId;
        this.applyStatus = applyStatus;
        this.reasonsForApply = reasonsForApply;
        this.knowledgeOfCompanyManagement = knowledgeOfCompanyManagement;
        this.goals = goals;
        this.commentsForStockHolder = commentsForStockHolder;
        this.solidarityLeaderElectionId = solidarityLeaderElectionId;
    }

    public void setSolidarityLeaderElectionId(Long solidarityLeaderElectionId) {
        this.solidarityLeaderElectionId = solidarityLeaderElectionId;
    }

    public void setElectionStatus(SolidarityLeaderElectionStatus electionStatus) {
        this.electionStatus = electionStatus;
    }
}
