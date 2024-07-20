package ag.act.dto;

import ag.act.dto.stock.SimpleStockDto;
import ag.act.entity.User;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class SolidarityLeaderApplicationDto {
    @Getter
    private User user;

    private String stockCode;
    private String stockStandardCode;
    private String stockName;

    @Getter
    private Long solidarityLeaderElectionId;
    @Getter
    private Long solidarityLeaderApplicantId;
    @Getter
    private SolidarityLeaderElectionApplyStatus applyStatus;
    @Getter
    private String reasonsForApply;
    @Getter
    private String knowledgeOfCompanyManagement;
    @Getter
    private String goals;
    @Getter
    private String commentsForStockHolder;

    public Long getUserId() {
        return user.getId();
    }

    public SimpleStockDto getSimpleStock() {
        return SimpleStockDto.of(stockCode, stockName, stockStandardCode);
    }

    public SolidarityLeaderApplicationDto removeOldSolidarityLeaderElectionData() {
        removeSolidarityLeaderElectionId();
        removeSolidarityLeaderApplicantId();
        removeApplyStatus();

        return this;
    }

    private void removeSolidarityLeaderElectionId() {
        this.solidarityLeaderElectionId = null;
    }

    private void removeSolidarityLeaderApplicantId() {
        this.solidarityLeaderApplicantId = null;
    }

    private void removeApplyStatus() {
        this.applyStatus = null;
    }
}
