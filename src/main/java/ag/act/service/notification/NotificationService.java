package ag.act.service.notification;

import ag.act.core.configuration.GlobalBoardManager;
import ag.act.dto.NotificationDto;
import ag.act.dto.notification.GetNotificationSearchDto;
import ag.act.dto.notification.NotificationSearchDto;
import ag.act.entity.Post;
import ag.act.entity.notification.Notification;
import ag.act.enums.notification.NotificationCategory;
import ag.act.enums.notification.NotificationType;
import ag.act.model.Status;
import ag.act.repository.interfaces.UserNotificationDetails;
import ag.act.repository.notification.NotificationRepository;
import ag.act.repository.notification.NotificationRepositoryCustom;
import ag.act.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private static final int RECENT_NOTIFICATION_TIME_PERIOD_MONTHS = 1;
    private final NotificationRepository notificationRepository;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private final NotificationRepositoryCustom notificationRepositoryCustom;
    private final GlobalBoardManager globalBoardManager;

    public Page<UserNotificationDetails> getUserNotifications(GetNotificationSearchDto getNotificationSearchDto) {
        return notificationRepository.findAllUserNotifications(
            getNotificationSearchDto.getUserId(),
            getNotificationSearchDto.getNullableCategoryName(),
            calculateRecentMonthStartDateTime(),
            globalBoardManager.getStockCode(),
            getNotificationSearchDto.getPageRequest()
        );
    }

    public long getUnreadNotificationsCount(Long userId) {
        return notificationRepository.countAllUserUnreadNotifications(
            userId,
            globalBoardManager.getStockCode(),
            calculateRecentMonthStartDateTime()
        );
    }

    private LocalDateTime calculateRecentMonthStartDateTime() {
        return DateTimeUtil.getPastMonthFromCurrentLocalDateTime(RECENT_NOTIFICATION_TIME_PERIOD_MONTHS);
    }

    public void createNotification(Post post) {
        if (!post.getIsNotification()) {
            return;
        }
        upsertNotification(post);
    }

    public void updateNotification(Post post, boolean isNotification) {
        if (isNotification == post.getIsNotification()) {
            return;
        }

        post.setIsNotification(isNotification);
        upsertNotification(post);
    }

    private void upsertNotification(Post post) {
        Notification notification = notificationRepository.findByPostId(post.getId())
            .orElseGet(Notification::new);

        notification.setStatus(post.getIsNotification() ? Status.ACTIVE : Status.DELETED);
        notification.setPostId(post.getId());
        notification.setCategory(NotificationCategory.STOCKHOLDER_ACTION);
        notification.setType(NotificationType.POST);

        if (post.getActiveStartDate() != null) {
            notification.setActiveStartDate(post.getActiveStartDate());
        }
        notification.setActiveEndDate(post.getActiveEndDate());

        notificationRepository.save(notification);
    }

    public Page<NotificationDto> getNotifications(NotificationSearchDto notificationSearchDto) {
        return notificationRepositoryCustom.findAllBySearchConditions(
            notificationSearchDto.getNullableCategoryName(),
            notificationSearchDto.getPostTitle(),
            calculateRecentMonthStartDateTime(),
            notificationSearchDto.getPageRequest()
        );
    }

    public void updateNotificationStatusByPostIdIfExist(Long postId, Status status) {
        notificationRepository.findByPostId(postId)
            .map(notification -> {
                notification.setStatus(status);
                return notificationRepository.save(notification);
            });
    }
}
