package ag.act.converter;

import ag.act.entity.ContentUserProfile;
import ag.act.entity.User;
import ag.act.service.user.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ContentUserProfileAdminLabelsHider {
    private final UserRoleService userRoleService;

    public void hideAdminLabels(Long userId, ContentUserProfile contentUserProfile) {
        if (userRoleService.isAdmin(userId)) {
            hideAdminLabels(contentUserProfile);
        }
    }

    public void hideAdminLabels(User user, ContentUserProfile contentUserProfile) {
        if (user.isAdmin() || userRoleService.isAdmin(user.getId())) {
            hideAdminLabels(contentUserProfile);
        }
    }

    private void hideAdminLabels(ContentUserProfile contentUserProfile) {
        contentUserProfile.setIndividualStockCountLabel(null);
        contentUserProfile.setTotalAssetLabel(null);
    }
}
