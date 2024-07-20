package ag.act.dto;

import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.notification.NotificationCategory;
import ag.act.enums.notification.NotificationType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class NotificationDto {
    private Long id;
    private Long postId;
    private NotificationCategory category;
    private NotificationType type;
    private LocalDateTime createdAt;
    private String postTitle;
    private BoardCategory boardCategory;
    private BoardGroup boardGroup;
    private String stockCode;
    private String stockName;

}
