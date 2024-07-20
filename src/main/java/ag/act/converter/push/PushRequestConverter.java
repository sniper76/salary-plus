package ag.act.converter.push;

import ag.act.converter.DateTimeConverter;
import ag.act.entity.Post;
import ag.act.entity.Push;
import ag.act.enums.AppLinkType;
import ag.act.enums.push.PushSendStatus;
import ag.act.enums.push.PushSendType;
import ag.act.enums.push.PushTargetType;
import ag.act.model.CreatePushRequest;
import ag.act.util.AppLinkUrlManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class PushRequestConverter {
    private final AppLinkUrlManager appLinkUrlManager;

    public Push convert(CreatePushRequest createPushRequest, Post post) {
        final Push push = new Push();
        final PushTargetType stockTargetType = PushTargetType.fromValue(createPushRequest.getStockTargetType());
        final PushSendType pushSendType = PushSendType.fromValue(createPushRequest.getSendType());
        final AppLinkType linkType = AppLinkType.fromValue(createPushRequest.getLinkType());

        push.setPushTargetType(stockTargetType);
        stockTargetType.setDataByTargetType(push, createPushRequest);

        push.setSendType(pushSendType);
        push.setTargetDatetime(getTargetDatetime(pushSendType, createPushRequest));

        push.setTitle(createPushRequest.getTitle().trim());
        push.setContent(createPushRequest.getContent().trim());
        push.setSendStatus(PushSendStatus.READY);

        push.setPostId(createPushRequest.getPostId());
        push.setLinkType(linkType);
        push.setLinkUrl(appLinkUrlManager.getLinkToSaveByLinkType(push, post));

        return push;
    }

    private LocalDateTime getTargetDatetime(PushSendType pushSendType, CreatePushRequest createPushRequest) {
        if (pushSendType == PushSendType.IMMEDIATELY) {
            return LocalDateTime.now();
        }

        return DateTimeConverter.convert(createPushRequest.getTargetDatetime());
    }
}
