package ag.act.module.cache;

import ag.act.enums.AppPreferenceType;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.IntegerRange;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PushPreference {
    private final AppPreferenceCache appPreferenceCache;

    public IntegerRange getPushSafeTimeRangeInHours() {
        return appPreferenceCache.getValue(AppPreferenceType.PUSH_SAFE_TIME_RANGE_IN_HOURS);
    }

    public int getAdditionalPushTimeMinutes() {
        return appPreferenceCache.getValue(AppPreferenceType.ADDITIONAL_PUSH_TIME_MINUTES);
    }
}
