package ag.act.service.push;

import ag.act.entity.Push;
import ag.act.enums.push.PushTargetType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class DefaultPushNotificationService implements PushNotificationService {
    @Override
    public PushTargetType getSupportPushTargetType() {
        return PushTargetType.UNKNOWN;
    }

    @Override
    public void sendPushNotification(Push push) {
        log.error(
            "Not found push notification service for push target type: {}, linkUrl: {}, title: {}, content: {}",
            push.getPushTargetType(),
            push.getTitle(),
            push.getLinkUrl(),
            push.getContent()
        );
    }
}
