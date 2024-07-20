package ag.act.util;

import ag.act.entity.SolidarityDailySummary;
import ag.act.enums.AppPreferenceType;
import ag.act.module.cache.AppPreferenceCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SolidarityLeaderElectionFeatureActiveConditionProvider {
    private final AppPreferenceCache appPreferenceCache;

    public Float getMinThresholdStake() {
        return appPreferenceCache.getValue(AppPreferenceType.LEADER_ELECTION_FEATURE_ACTIVE_MIN_THRESHOLD_STAKE);
    }

    public Long getMinThresholdMemberCount() {
        return appPreferenceCache.getValue(AppPreferenceType.LEADER_ELECTION_FEATURE_ACTIVE_MIN_THRESHOLD_MEMBER_COUNT);
    }

    public boolean canActivateLeaderElectionFeature(SolidarityDailySummary mostRecentDailySummary) {
        final Double stake = mostRecentDailySummary.getStake();
        final Integer memberCount = mostRecentDailySummary.getMemberCount();

        return isStakeAtLeastMinThreshold(stake) || isMemberCountAtLeastMinThreshold(memberCount);
    }

    public boolean isStakeAtLeastMinThreshold(Double stake) {
        Float minThresholdStake = getMinThresholdStake();
        return stake.compareTo((double) minThresholdStake) >= 0;
    }

    public boolean isMemberCountAtLeastMinThreshold(Integer memberCount) {
        Long minThresholdMemberCount = getMinThresholdMemberCount();
        return memberCount.compareTo(minThresholdMemberCount.intValue()) >= 0;
    }
}
