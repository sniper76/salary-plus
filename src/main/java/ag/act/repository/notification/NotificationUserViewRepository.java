package ag.act.repository.notification;

import ag.act.entity.notification.NotificationUserView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationUserViewRepository extends JpaRepository<NotificationUserView, Long> {

    Optional<NotificationUserView> findByNotificationIdAndUserId(Long notificationId, Long userId);
}
