package ag.act.enums.notification;


import lombok.Getter;

import java.util.Optional;

@Getter
public enum NotificationCategory {

    STOCKHOLDER_ACTION("주주행동");

    private final String displayName;

    NotificationCategory(String displayName) {
        this.displayName = displayName;
    }

    public static Optional<NotificationCategory> fromValue(String notificationCategoryName) {
        try {
            return Optional.of(NotificationCategory.valueOf(notificationCategoryName.toUpperCase()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
