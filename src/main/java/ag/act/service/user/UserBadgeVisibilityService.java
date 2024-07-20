package ag.act.service.user;

import ag.act.dto.user.UserBadgeVisibilitySettings;
import ag.act.entity.UserBadgeVisibility;
import ag.act.enums.UserBadgeType;
import ag.act.model.Status;
import ag.act.model.UpdateMyProfileRequest;
import ag.act.repository.UserBadgeVisibilityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserBadgeVisibilityService {
    private final UserBadgeVisibilityRepository userBadgeVisibilityRepository;

    public UserBadgeVisibilitySettings getUserBadgeVisibilitySettings(Long userId) {
        final List<UserBadgeVisibility> userBadgeVisibilities = getUserBadgeVisibilities(userId);

        return new UserBadgeVisibilitySettings(
            isBadgeVisible(userBadgeVisibilities, UserBadgeType.TOTAL_ASSET),
            isBadgeVisible(userBadgeVisibilities, UserBadgeType.STOCK_QUANTITY)
        );
    }

    public List<UserBadgeVisibility> getUserBadgeVisibilities(Long userId) {
        return userBadgeVisibilityRepository.findAllByUserId(userId);
    }

    public UserBadgeVisibility save(UserBadgeVisibility userBadgeVisibility) {
        return userBadgeVisibilityRepository.save(userBadgeVisibility);
    }

    public void create(Long userId, UpdateMyProfileRequest updateMyProfileRequest) {
        save(makeUserBadgeVisibility(userId, UserBadgeType.STOCK_QUANTITY, updateMyProfileRequest.getIsVisibilityStockQuantity()));
        save(makeUserBadgeVisibility(userId, UserBadgeType.TOTAL_ASSET, updateMyProfileRequest.getIsVisibilityTotalAsset()));
    }

    public Optional<UserBadgeVisibility> getByUserIdAndType(Long userId, UserBadgeType userBadgeType) {
        return userBadgeVisibilityRepository.findByUserIdAndType(userId, userBadgeType);
    }

    private UserBadgeVisibility makeUserBadgeVisibility(Long userId, UserBadgeType userBadgeType, boolean isVisible) {
        UserBadgeVisibility userBadgeVisibility = getByUserIdAndType(userId, userBadgeType)
            .orElse(new UserBadgeVisibility());

        userBadgeVisibility.setUserId(userId);
        userBadgeVisibility.setType(userBadgeType);
        userBadgeVisibility.setStatus(Status.ACTIVE);
        userBadgeVisibility.setIsVisible(isVisible);

        return userBadgeVisibility;
    }

    private boolean isBadgeVisible(List<UserBadgeVisibility> userBadgeVisibilities, UserBadgeType type) {
        return userBadgeVisibilities
            .stream()
            .filter(it -> it.getType() == type)
            .map(UserBadgeVisibility::getIsVisible)
            .findFirst()
            .orElse(true);
    }
}
