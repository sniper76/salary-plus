package ag.act.handler;

import ag.act.api.UserNotificationApiDelegate;
import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.PageDataConverter;
import ag.act.dto.notification.GetNotificationSearchDto;
import ag.act.facade.NotificationFacade;
import ag.act.model.GetUserNotificationDataResponse;
import ag.act.model.SimpleStringResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserNotificationApiDelegateImpl implements UserNotificationApiDelegate {

    private final PageDataConverter pageDataConverter;
    private final NotificationFacade notificationFacade;

    @Override
    public ResponseEntity<GetUserNotificationDataResponse> getUserNotifications(
        String category,
        Integer page,
        Integer size,
        List<String> sorts
    ) {
        final GetNotificationSearchDto getNotificationSearchDto = new GetNotificationSearchDto(
            category,
            ActUserProvider.getNoneNull().getId(),
            pageDataConverter.convert(page, size, sorts)
        );

        return ResponseEntity.ok(
            pageDataConverter.convert(
                notificationFacade.getUserNotifications(getNotificationSearchDto),
                GetUserNotificationDataResponse.class
            )
        );
    }

    @Override
    public ResponseEntity<SimpleStringResponse> createNotificationUserView(Long notificationId) {
        return ResponseEntity.ok(notificationFacade.createNotificationUserView(notificationId));
    }
}
