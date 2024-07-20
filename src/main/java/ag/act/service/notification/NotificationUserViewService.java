package ag.act.service.notification;

import ag.act.configuration.security.ActUserProvider;
import ag.act.entity.User;
import ag.act.entity.notification.NotificationUserView;
import ag.act.repository.notification.NotificationUserViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationUserViewService {
    private final NotificationUserViewRepository notificationUserViewRepository;

    public void createNotificationUserView(Long notificationId) {
        final User user = ActUserProvider.getNoneNull();

        findByNotificationIdAndUserId(notificationId, user.getId())
            .orElseGet(() -> createNotificationUserView(notificationId, user.getId()));
    }

    private NotificationUserView createNotificationUserView(Long notificationId, Long userId) {
        NotificationUserView notificationUserView = new NotificationUserView();
        notificationUserView.setNotificationId(notificationId);
        notificationUserView.setUserId(userId);
        return notificationUserViewRepository.save(notificationUserView);
    }

    private Optional<NotificationUserView> findByNotificationIdAndUserId(Long notificationId, Long userId) {
        return notificationUserViewRepository.findByNotificationIdAndUserId(notificationId, userId);
    }
}
