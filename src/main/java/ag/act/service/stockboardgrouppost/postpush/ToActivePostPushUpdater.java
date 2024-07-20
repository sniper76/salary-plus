package ag.act.service.stockboardgrouppost.postpush;

import ag.act.converter.DateTimeConverter;
import ag.act.entity.Post;
import ag.act.entity.Push;
import ag.act.enums.push.PushSendStatus;
import ag.act.model.PushRequest;
import ag.act.service.push.PushService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ToActivePostPushUpdater {

    private final PushService pushService;

    public void update(PostPushUpdaterInput input) {

        final Optional<Push> optionalPush = input.getOptionalPush();
        final PushRequest pushRequest = input.requestPush();
        final Post post = input.post();
        final Instant activeStartDate = getActiveStartDate(input, post);

        if (optionalPush.isPresent()) {
            final Push push = optionalPush.get();
            if (push.getSendStatus() == PushSendStatus.READY) {
                pushService.updatePush(push, pushRequest, activeStartDate);
            }
        } else {
            if (pushRequest != null) {
                final Optional<Push> pushOptional = pushService.createPushForGlobalEvent(
                    pushRequest,
                    post.getId(),
                    activeStartDate
                );
                pushOptional.ifPresent(push -> post.setPushId(push.getId()));
            } else {
                post.setPushId(null);
            }
        }
    }

    private Instant getActiveStartDate(PostPushUpdaterInput input, Post post) {
        return Optional.ofNullable(input.activeStartDate())
            .orElseGet(() -> DateTimeConverter.convert(post.getActiveStartDate()));
    }
}
