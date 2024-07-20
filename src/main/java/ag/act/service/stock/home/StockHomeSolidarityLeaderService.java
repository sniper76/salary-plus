package ag.act.service.stock.home;

import ag.act.entity.LeaderStatus;
import ag.act.entity.SolidarityLeader;
import ag.act.model.StockHomeLeaderResponse;
import ag.act.service.solidarity.SolidarityService;
import ag.act.service.solidarity.election.SolidarityLeaderApplicantService;
import ag.act.service.solidarity.election.SolidarityLeaderService;
import ag.act.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StockHomeSolidarityLeaderService {
    private final SolidarityLeaderService solidarityLeaderService;
    private final SolidarityLeaderApplicantService solidarityLeaderApplicantService;
    private final SolidarityService solidarityService;
    private final UserService userService;

    public StockHomeLeaderResponse getStockHomeLeader(String stockCode) {
        Optional<SolidarityLeader> leaderOptional = solidarityLeaderService.findLeader(stockCode);

        if (leaderOptional.isPresent()) {
            return createLeaderMessageWithElectedStatus(leaderOptional.get());
        } else {
            return createUserAppliedStatusWithElectionInProgressStatus(stockCode);
        }
    }

    private StockHomeLeaderResponse createLeaderMessageWithElectedStatus(SolidarityLeader leader) {
        return new StockHomeLeaderResponse()
            .status(LeaderStatus.ELECTED.getName())
            .message(getLeaderMessage(leader))
            .solidarityId(leader.getSolidarity().getId());
    }

    private String getLeaderMessage(SolidarityLeader leader) {
        return StringUtils.isBlank(leader.getMessage()) ? getDefaultLeaderMessage(leader) : leader.getMessage();
    }

    private String getDefaultLeaderMessage(SolidarityLeader leader) {
        return "%s님이 주주대표로 선정되었습니다.\n주주대표 한마디가 곧 등록됩니다."
            .formatted(userService.getUser(leader.getUserId()).getNickname());
    }

    private StockHomeLeaderResponse createUserAppliedStatusWithElectionInProgressStatus(String stockCode) {
        return new StockHomeLeaderResponse()
            .status(LeaderStatus.ELECTION_IN_PROGRESS.getName())
            .applied(solidarityLeaderApplicantService.isUserAppliedSolidarity(stockCode))
            .solidarityId(solidarityService.getSolidarityByStockCode(stockCode).getId());
    }
}
