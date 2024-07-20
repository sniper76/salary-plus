package ag.act.handler.admin;

import ag.act.api.AdminNotificationApiDelegate;
import ag.act.converter.PageDataConverter;
import ag.act.core.guard.IsAdminGuard;
import ag.act.core.guard.UseGuards;
import ag.act.dto.notification.NotificationSearchDto;
import ag.act.facade.NotificationFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@UseGuards(IsAdminGuard.class)
public class AdminNotificationApiDelegateImpl implements AdminNotificationApiDelegate {

    private final PageDataConverter pageDataConverter;
    private final NotificationFacade notificationFacade;

    @Override
    public ResponseEntity<ag.act.model.GetNotificationDataResponse> getNotifications(
        String category, Integer page, Integer size, List<String> sorts, String postTitle
    ) {
        final NotificationSearchDto notificationSearchDto = new NotificationSearchDto(
            category, pageDataConverter.convert(page, size, sorts), postTitle);

        return ResponseEntity.ok(
            pageDataConverter.convert(
                notificationFacade.getNotifications(notificationSearchDto),
                ag.act.model.GetNotificationDataResponse.class));
    }

}
