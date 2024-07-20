package ag.act.service.push;

import ag.act.dto.push.CreateFcmPushDataDto;
import ag.act.entity.Push;
import ag.act.enums.push.PushTargetType;
import ag.act.service.FirebaseMessagingService;
import ag.act.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class StockPushNotificationService implements PushNotificationService {
    private final FirebaseMessagingService firebaseMessagingService;
    private final UserService userService;

    @Override
    public PushTargetType getSupportPushTargetType() {
        return PushTargetType.STOCK;
    }

    @Override
    public void sendPushNotification(Push push) {
        List<String> tokens = userService.getPushTokens(push.getStockCode());

        if (tokens.isEmpty()) {
            return;
        }

        final String pushMessage = "[%s] %s".formatted(push.getStock().getName(), push.getContent());

        firebaseMessagingService.sendPushNotification(
            CreateFcmPushDataDto.newInstance(push.getTitle(), pushMessage, push.getLinkUrl()),
            tokens
        );
    }
}
