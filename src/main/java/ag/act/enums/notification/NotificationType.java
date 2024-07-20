package ag.act.enums.notification;


import lombok.Getter;

@Getter
public enum NotificationType {

    POST;

    public static NotificationType fromValue(String notificationTypeName) {
        try {
            return NotificationType.valueOf(notificationTypeName.toUpperCase());
        } catch (Exception e) {
            return POST;
        }
    }
}
