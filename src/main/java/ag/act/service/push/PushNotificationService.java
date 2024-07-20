package ag.act.service.push;

import ag.act.entity.Push;
import ag.act.enums.push.PushTargetType;

public interface PushNotificationService {
    void sendPushNotification(Push push);

    PushTargetType getSupportPushTargetType();
}
