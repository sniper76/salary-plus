package ag.act.converter.notification;

import ag.act.converter.Converter;
import ag.act.converter.DateTimeConverter;
import ag.act.dto.NotificationDto;
import ag.act.enums.notification.NotificationType;
import ag.act.model.UserPostNotificationResponse;
import ag.act.util.AppLinkUrlGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationResponseConverter implements Converter<NotificationDto, ag.act.model.NotificationResponse> {
    private final AppLinkUrlGenerator appLinkUrlGenerator;

    public NotificationResponseConverter(AppLinkUrlGenerator appLinkUrlGenerator) {
        this.appLinkUrlGenerator = appLinkUrlGenerator;
    }

    public ag.act.model.NotificationResponse apply(NotificationDto notificationDto) {
        return new ag.act.model.NotificationResponse()
            .id(notificationDto.getId())
            .linkUrl(appLinkUrlGenerator.generateBoardGroupPostLinkUrl(
                notificationDto.getStockCode(),
                notificationDto.getBoardGroup().name(),
                notificationDto.getPostId()
            ))
            .postId(notificationDto.getPostId())
            .category(notificationDto.getCategory().name())
            .type(notificationDto.getType().name())
            .createdAt(DateTimeConverter.convert(notificationDto.getCreatedAt()))
            .post(getPost(notificationDto));
    }

    private UserPostNotificationResponse getPost(NotificationDto notificationDto) {
        if (notificationDto.getType() != NotificationType.POST) {
            return null;
        }

        if (notificationDto.getPostId() == null) {
            return null;
        }

        return new UserPostNotificationResponse()
            .id(notificationDto.getPostId())
            .title(notificationDto.getPostTitle())
            .boardCategory(notificationDto.getBoardCategory().name())
            .boardCategoryDisplayName(notificationDto.getBoardCategory().getDisplayName())
            .stockCode(notificationDto.getStockCode())
            .stockName(notificationDto.getStockName());
    }

}
