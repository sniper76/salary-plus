package ag.act.converter.user;

import ag.act.entity.UserBadgeVisibility;
import ag.act.enums.UserBadgeType;
import ag.act.model.UserBadgeVisibilityResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class UserBadgeVisibilityResponseConverter {

    private ag.act.model.UserBadgeVisibilityResponse convert(UserBadgeVisibility userBadgeVisibility) {
        return new UserBadgeVisibilityResponse()
            .label(userBadgeVisibility.getType().getLabel())
            .isVisible(userBadgeVisibility.getIsVisible())
            .name(userBadgeVisibility.getType().getBadgeName());
    }

    private UserBadgeVisibilityResponse convert(UserBadgeType type) {
        return new UserBadgeVisibilityResponse()
            .label(type.getLabel())
            .isVisible(true)
            .name(type.getBadgeName());
    }

    @NotNull
    public List<UserBadgeVisibilityResponse> convert(List<UserBadgeVisibility> userBadgeVisibilities) {
        if (userBadgeVisibilities.isEmpty()) {
            return Arrays.stream(UserBadgeType.values())
                .map(this::convert)
                .toList();
        }

        return userBadgeVisibilities.stream()
            .map(this::convert)
            .toList();
    }
}
