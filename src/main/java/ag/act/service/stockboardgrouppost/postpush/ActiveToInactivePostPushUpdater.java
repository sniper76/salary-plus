package ag.act.service.stockboardgrouppost.postpush;

import ag.act.entity.Post;
import ag.act.entity.Push;
import ag.act.enums.push.PushSendStatus;
import ag.act.model.PushRequest;
import ag.act.service.push.PushService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ActiveToInactivePostPushUpdater implements PostPushUpdater {

    private final PushService pushService;

    @Override
    public boolean supports(PostPushUpdaterInput input) {
        return input.postOriginalStatus() == ag.act.model.Status.ACTIVE && !input.isActiveForUpdate();
    }

    @Override
    public void update(PostPushUpdaterInput input) {

        final Optional<Push> optionalPush = input.getOptionalPush();
        final PushRequest pushRequest = input.requestPush();
        final Post post = input.post();
        if (optionalPush.isEmpty() && pushRequest == null) {
            return;
        }

        optionalPush.ifPresentOrElse(
            push -> {
                if (push.getSendStatus() == PushSendStatus.READY) {
                    pushService.deletePush(push.getId());
                    post.setPushId(null);
                }
            },
            () -> post.setPushId(null) // 푸시를 못 찾은 경우 게시글의 pushId 를 null 처리한다.
        );
    }
}
