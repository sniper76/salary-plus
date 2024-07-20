package ag.act.service.push;

import ag.act.dto.push.CreateFcmPushDataDto;
import ag.act.entity.Push;
import ag.act.enums.push.PushTargetType;
import ag.act.service.FirebaseMessagingService;
import ag.act.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class StockGroupPushNotificationService implements PushNotificationService {
    private final FirebaseMessagingService firebaseMessagingService;
    private final UserService userService;

    @Override
    public PushTargetType getSupportPushTargetType() {
        return PushTargetType.STOCK_GROUP;
    }

    @Override
    public void sendPushNotification(Push push) {
        final Set<String> tokens = userService.getPushTokensByStockGroupId(push.getStockGroupId());

        if (tokens.isEmpty()) {
            return;
        }

        firebaseMessagingService.sendPushNotification(
            CreateFcmPushDataDto.newInstance(push.getTitle(), push.getContent(), push.getLinkUrl()),
            new ArrayList<>(tokens)
        );
    }
}
