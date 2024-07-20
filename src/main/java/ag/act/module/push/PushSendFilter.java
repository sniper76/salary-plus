package ag.act.module.push;

import ag.act.entity.Push;
import ag.act.module.cache.PushPreference;
import ag.act.util.KoreanDateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PushSendFilter {
    private final PushPreference pushPreference;

    public List<Push> filter(List<Push> pushes) {
        return pushes.stream()
            .filter(this::canSend)
            .toList();
    }

    private boolean canSend(Push push) {
        return isCurrentTimeInPushSafeTime();
    }

    private boolean isCurrentTimeInPushSafeTime() {
        return pushPreference.getPushSafeTimeRangeInHours()
            .contains(getCurrentKoreanHour());
    }

    private int getCurrentKoreanHour() {
        return KoreanDateTimeUtil.getNowInKoreanTime().getHour();
    }
}
