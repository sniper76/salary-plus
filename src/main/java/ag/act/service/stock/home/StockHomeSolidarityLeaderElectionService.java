package ag.act.service.stock.home;

import ag.act.dto.SolidarityLeaderApplicationDto;
import ag.act.entity.SolidarityLeader;
import ag.act.service.solidarity.election.SolidarityLeaderApplicantService;
import ag.act.service.solidarity.election.SolidarityLeaderElectionService;
import ag.act.service.solidarity.election.SolidarityLeaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class StockHomeSolidarityLeaderElectionService {

    private final SolidarityLeaderElectionService solidarityLeaderElectionService;
    private final SolidarityLeaderApplicantService solidarityLeaderApplicantService;
    private final SolidarityLeaderService solidarityLeaderService;

    public Optional<SolidarityLeaderApplicationDto> findLeaderApplication(Long userId, String stockCode) {
        Optional<SolidarityLeader> leader = solidarityLeaderService.findLeader(stockCode);

        if (leader.isPresent()) {
            return Optional.empty();
        }

        return solidarityLeaderApplicantService.findLatestAppliedSolidarityLeaderApplicantByStockCodeAndUserId(stockCode, userId)
            .filter(applicant -> isApplicationVisible(applicant.getSolidarityLeaderElectionId()));
    }

    private boolean isApplicationVisible(Long solidarityLeaderElectionId) {
        return !solidarityLeaderElectionService.isSolidarityLeaderElectionStatusNotInPreVoteStatus(solidarityLeaderElectionId);
    }
}
