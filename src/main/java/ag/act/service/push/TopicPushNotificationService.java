package ag.act.service.push;

import ag.act.dto.push.CreateFcmPushDataDto;
import ag.act.entity.Push;
import ag.act.enums.push.PushTargetType;
import ag.act.enums.push.PushTopic;
import ag.act.service.FirebaseMessagingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TopicPushNotificationService implements PushNotificationService {
    private final FirebaseMessagingService firebaseMessagingService;

    @Override
    public PushTargetType getSupportPushTargetType() {
        return PushTargetType.ALL;
    }

    @Override
    public void sendPushNotification(Push push) {
        firebaseMessagingService.sendPushNotification(
            CreateFcmPushDataDto.newInstance(push.getTitle(), push.getContent(), push.getLinkUrl()),
            PushTopic.NOTICE
        );
    }
}
