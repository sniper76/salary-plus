package ag.act.repository.interfaces;

import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.notification.NotificationCategory;
import ag.act.enums.notification.NotificationType;

import java.time.LocalDateTime;

public interface UserNotificationDetails {

    Long getId();

    Long getPostId();

    NotificationCategory getCategory();

    NotificationType getType();

    LocalDateTime getCreatedAt();

    LocalDateTime getActiveStartDate();

    Boolean getRead();

    String getPostTitle();

    BoardCategory getBoardCategory();

    BoardGroup getBoardGroup();

    String getStockCode();

    String getStockName();
}
