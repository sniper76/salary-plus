package ag.act.service;

import ag.act.dto.push.CreateFcmPushDataDto;
import ag.act.enums.push.PushTopic;
import ag.act.util.ChunkUtil;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class FirebaseMessagingService {
    private static final int CHUNK_SIZE = 500;
    private final ChunkUtil chunkUtil;
    private final FirebaseMessaging firebaseMessaging;

    public Boolean sendPushNotification(CreateFcmPushDataDto createFcmPushDataDto, List<String> tokens) {
        Notification notification = Notification
            .builder()
            .setTitle(createFcmPushDataDto.getTitle())
            .setBody(createFcmPushDataDto.getContent())
            .build();

        List<List<String>> chunks = getTokenChunks(tokens);

        for (List<String> chunk : chunks) {
            firebaseMessaging.sendEachForMulticastAsync(
                MulticastMessage.builder()
                    .setNotification(notification)
                    .addAllTokens(chunk)
                    .putAllData(createFcmPushDataMap(createFcmPushDataDto))
                    .build()
            );
        }

        return Boolean.TRUE;
    }

    public Boolean sendPushNotification(CreateFcmPushDataDto createFcmPushDataDto, PushTopic pushTopic) {
        Notification notification = Notification
            .builder()
            .setTitle(createFcmPushDataDto.getTitle())
            .setBody(createFcmPushDataDto.getContent())
            .build();

        Message message = Message
            .builder()
            .setTopic(pushTopic.name())
            .setNotification(notification)
            .putAllData(createFcmPushDataMap(createFcmPushDataDto))
            .build();

        firebaseMessaging.sendAsync(message);

        return Boolean.TRUE;
    }

    private List<List<String>> getTokenChunks(List<String> tokens) {
        return chunkUtil.getChunks(tokens, CHUNK_SIZE);
    }

    private Map<String, String> createFcmPushDataMap(CreateFcmPushDataDto createFcmPushDataDto) {
        Map<String, String> map = new HashMap<>();

        // [CAUTION] Firebase Message Object SHOULD NOT have map with null keys / values at data field
        if (createFcmPushDataDto.getLinkUrl() != null) {
            map.put("linkUrl", createFcmPushDataDto.getLinkUrl());
        }
        return map;
    }
}
