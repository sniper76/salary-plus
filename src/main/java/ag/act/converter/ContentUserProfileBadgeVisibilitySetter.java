package ag.act.converter;

import ag.act.dto.user.UserBadgeVisibilitySettings;
import ag.act.entity.ContentUserProfile;
import ag.act.service.user.UserBadgeVisibilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContentUserProfileBadgeVisibilitySetter {
    private final UserBadgeVisibilityService userBadgeVisibilityService;

    public void setBadgeVisibilities(Long userId, ContentUserProfile targetInstance) {
        final UserBadgeVisibilitySettings userBadgeVisibilitySettings = userBadgeVisibilityService.getUserBadgeVisibilitySettings(userId);
        if (!userBadgeVisibilitySettings.isTotalAssetBadgeVisible()) {
            targetInstance.setTotalAssetLabel(null);
        }
        if (!userBadgeVisibilitySettings.isStockQuantityBadgeVisible()) {
            targetInstance.setIndividualStockCountLabel(null);
        }
    }
}
