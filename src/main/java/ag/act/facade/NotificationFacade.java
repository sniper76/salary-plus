package ag.act.facade;

import ag.act.converter.notification.NotificationResponseConverter;
import ag.act.converter.notification.UserNotificationResponseConverter;
import ag.act.dto.SimplePageDto;
import ag.act.dto.notification.GetNotificationSearchDto;
import ag.act.dto.notification.NotificationSearchDto;
import ag.act.model.NotificationResponse;
import ag.act.model.SimpleStringResponse;
import ag.act.model.UserNotificationResponse;
import ag.act.service.notification.NotificationService;
import ag.act.service.notification.NotificationUserViewService;
import ag.act.util.SimpleStringResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationFacade {
    private final NotificationService notificationService;
    private final NotificationUserViewService notificationUserViewService;
    private final UserNotificationResponseConverter userNotificationResponseConverter;
    private final NotificationResponseConverter notificationResponseConverter;

    public SimplePageDto<UserNotificationResponse> getUserNotifications(GetNotificationSearchDto getNotificationSearchDto) {
        return new SimplePageDto<>(
            notificationService.getUserNotifications(getNotificationSearchDto)
                .map(userNotificationResponseConverter::convert)
        );
    }

    public SimplePageDto<NotificationResponse> getNotifications(NotificationSearchDto notificationSearchDto) {
        return new SimplePageDto<>(
            notificationService.getNotifications(notificationSearchDto)
                .map(notificationResponseConverter)
        );
    }

    public SimpleStringResponse createNotificationUserView(Long notificationId) {
        notificationUserViewService.createNotificationUserView(notificationId);
        return SimpleStringResponseUtil.ok();
    }
}
