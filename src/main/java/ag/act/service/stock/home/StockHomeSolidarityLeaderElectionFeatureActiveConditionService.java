package ag.act.service.stock.home;

import ag.act.entity.Solidarity;
import ag.act.entity.SolidarityDailySummary;
import ag.act.model.LeaderElectionFeatureActiveConditionResponse;
import ag.act.service.solidarity.SolidarityService;
import ag.act.service.solidarity.election.SolidarityLeaderElectionService;
import ag.act.util.SolidarityLeaderElectionFeatureActiveConditionProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class StockHomeSolidarityLeaderElectionFeatureActiveConditionService {
    private static final float DEFAULT_STAKE = 0.0f;
    private static final long DEFAULT_MEMBER_COUNT = 0L;
    private final SolidarityService solidarityService;
    private final SolidarityLeaderElectionService solidarityLeaderElectionService;
    private final SolidarityLeaderElectionFeatureActiveConditionProvider solidarityLeaderElectionFeatureActiveConditionProvider;

    public LeaderElectionFeatureActiveConditionResponse getLeaderElectionFeatureActiveCondition(String stockCode) {
        final Solidarity solidarity = solidarityService.getSolidarityByStockCode(stockCode);
        final Optional<SolidarityDailySummary> mostRecentDailySummary = Optional.ofNullable(solidarity.getMostRecentDailySummary());

        return new LeaderElectionFeatureActiveConditionResponse()
            .minThresholdStake(solidarityLeaderElectionFeatureActiveConditionProvider.getMinThresholdStake())
            .minThresholdMemberCount(solidarityLeaderElectionFeatureActiveConditionProvider.getMinThresholdMemberCount())
            .stake(mostRecentDailySummary.map(this::getStake).orElse(DEFAULT_STAKE))
            .memberCount(mostRecentDailySummary.map(this::getMemberCount).orElse(DEFAULT_MEMBER_COUNT))
            .isVisible(
                mostRecentDailySummary.map(solidarityDailySummary -> isVisible(stockCode, solidarity, solidarityDailySummary)).orElse(false)
            );
    }

    private long getMemberCount(SolidarityDailySummary solidarityDailySummary) {
        return solidarityDailySummary.getMemberCount().longValue();
    }

    private float getStake(SolidarityDailySummary solidarityDailySummary) {
        return solidarityDailySummary.getStake().floatValue();
    }

    private boolean isVisible(String stockCode, Solidarity solidarity, SolidarityDailySummary solidarityDailySummary) {
        return !solidarity.isHasEverHadLeader()
            && !hasOngoingSolidarityLeaderElection(stockCode)
            && !solidarityLeaderElectionFeatureActiveConditionProvider.canActivateLeaderElectionFeature(solidarityDailySummary);
    }

    private boolean hasOngoingSolidarityLeaderElection(String stockCode) {
        return solidarityLeaderElectionService.existsOngoingSolidarityLeaderElection(stockCode);
    }
}
