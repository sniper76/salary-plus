package ag.act.service.push;

import ag.act.enums.push.PushTargetType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class PushNotificationServiceResolver {
    private final List<PushNotificationService> pushNotificationServices;
    private final DefaultPushNotificationService defaultPushNotificationService;
    private Map<PushTargetType, PushNotificationService> pushNotificationServiceMap;

    @PostConstruct
    public void init() {
        pushNotificationServiceMap = pushNotificationServices
            .stream()
            .collect(
                Collectors.toMap(
                    PushNotificationService::getSupportPushTargetType,
                    Function.identity()
                )
            );
    }

    public PushNotificationService resolve(PushTargetType pushTargetType) {
        return pushNotificationServiceMap.getOrDefault(pushTargetType, defaultPushNotificationService);
    }
}
