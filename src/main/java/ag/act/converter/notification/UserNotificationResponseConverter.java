package ag.act.converter.notification;

import ag.act.converter.Converter;
import ag.act.converter.DateTimeConverter;
import ag.act.enums.notification.NotificationType;
import ag.act.model.UserNotificationResponse;
import ag.act.model.UserPostNotificationResponse;
import ag.act.repository.interfaces.UserNotificationDetails;
import ag.act.util.AppLinkUrlGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserNotificationResponseConverter implements Converter<UserNotificationDetails, UserNotificationResponse> {
    private final AppLinkUrlGenerator appLinkUrlGenerator;

    public UserNotificationResponseConverter(AppLinkUrlGenerator appLinkUrlGenerator) {
        this.appLinkUrlGenerator = appLinkUrlGenerator;
    }

    public UserNotificationResponse convert(UserNotificationDetails userNotificationDetails) {
        return new UserNotificationResponse()
            .id(userNotificationDetails.getId())
            .linkUrl(
                appLinkUrlGenerator.generateBoardGroupPostLinkUrl(
                    userNotificationDetails.getStockCode(),
                    userNotificationDetails.getBoardGroup().name(),
                    userNotificationDetails.getPostId()
                )
            )
            .postId(userNotificationDetails.getPostId())
            .category(userNotificationDetails.getCategory().name())
            .type(userNotificationDetails.getType().name())
            .createdAt(DateTimeConverter.convert(userNotificationDetails.getCreatedAt()))
            .activeStartDate(DateTimeConverter.convert(userNotificationDetails.getActiveStartDate()))
            .isRead(userNotificationDetails.getRead())
            .post(getPost(userNotificationDetails));
    }

    private UserPostNotificationResponse getPost(UserNotificationDetails userNotificationDetails) {
        if (userNotificationDetails.getType() != NotificationType.POST) {
            return null;
        }

        if (userNotificationDetails.getPostId() == null) {
            return null;
        }

        return new UserPostNotificationResponse()
            .id(userNotificationDetails.getPostId())
            .title(userNotificationDetails.getPostTitle())
            .boardCategory(userNotificationDetails.getBoardCategory().name())
            .boardCategoryDisplayName(userNotificationDetails.getBoardCategory().getDisplayName())
            .stockCode(userNotificationDetails.getStockCode())
            .stockName(userNotificationDetails.getStockName());
    }

    @Override
    public UserNotificationResponse apply(UserNotificationDetails userNotificationDetails) {
        return convert(userNotificationDetails);
    }
}
