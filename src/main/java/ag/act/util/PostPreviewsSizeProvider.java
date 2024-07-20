package ag.act.util;

import ag.act.enums.AppPreferenceType;
import ag.act.module.cache.AppPreferenceCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PostPreviewsSizeProvider {
    private final AppPreferenceCache appPreferenceCache;

    public Integer get() {
        return appPreferenceCache.getValue(AppPreferenceType.POST_PREVIEWS_SIZE);
    }
}
