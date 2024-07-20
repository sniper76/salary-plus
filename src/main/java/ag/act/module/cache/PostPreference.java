package ag.act.module.cache;

import ag.act.entity.Post;
import ag.act.enums.AppPreferenceType;
import ag.act.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PostPreference {
    private final AppPreferenceCache appPreferenceCache;

    public boolean isNew(Post post) {
        return !DateTimeUtil.isBeforeInHours(post.getCreatedAt(), getNewPostThresholdHours());
    }

    public int getNewPostThresholdHours() {
        return appPreferenceCache.getValue(AppPreferenceType.NEW_POST_THRESHOLD_HOURS);
    }

}
