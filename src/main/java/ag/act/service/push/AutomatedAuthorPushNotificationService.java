package ag.act.service.push;

import ag.act.dto.push.CreateFcmPushDataDto;
import ag.act.entity.AutomatedAuthorPush;
import ag.act.entity.Push;
import ag.act.enums.push.PushSendStatus;
import ag.act.enums.push.PushTargetType;
import ag.act.service.FirebaseMessagingService;
import ag.act.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AutomatedAuthorPushNotificationService implements PushNotificationService {
    private final FirebaseMessagingService firebaseMessagingService;
    private final UserService userService;
    private final AutomatedAuthorPushService automatedAuthorPushService;
    private final PushService pushService;
    private final AutomatedAuthorPushSearchTimeFactory automatedAuthorPushSearchTimeFactory;

    @Override
    public PushTargetType getSupportPushTargetType() {
        return PushTargetType.AUTOMATED_AUTHOR;
    }

    @Override
    public void sendPushNotification(Push push) {
        final AutomatedAuthorPush automatedAuthorPush = automatedAuthorPushService.findByPushId(push.getId());
        final LocalDateTime fiveMinuteAge = automatedAuthorPushSearchTimeFactory.getFiveMinuteAge();
        final List<Push> pushes = pushService.getPushes(
            automatedAuthorPush.getContentId(), automatedAuthorPush.getContentType(), automatedAuthorPush.getCriteria(), fiveMinuteAge
        );

        if (existAlreadyCompleteByContentIdAndContentTypeAndCriteria(pushes)) {
            return;
        }

        List<String> tokens = getTokens(push, automatedAuthorPush);

        if (tokens.isEmpty()) {
            return;
        }

        firebaseMessagingService.sendPushNotification(
            CreateFcmPushDataDto.newInstance(push.getTitle(), push.getContent(), push.getLinkUrl()),
            tokens
        );
    }

    private List<String> getTokens(Push push, AutomatedAuthorPush automatedAuthorPush) {
        return userService.findAllTokens(push.getId(), automatedAuthorPush)
            .stream()
            .filter(StringUtils::isNotBlank)
            .toList();
    }

    private boolean existAlreadyCompleteByContentIdAndContentTypeAndCriteria(List<Push> pushes) {
        return pushes.stream()
            .anyMatch(this::isComplete);
    }

    private boolean isComplete(Push push) {
        return push.getSendStatus() == PushSendStatus.COMPLETE;
    }
}
