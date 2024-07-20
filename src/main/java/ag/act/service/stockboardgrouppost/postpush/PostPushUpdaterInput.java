package ag.act.service.stockboardgrouppost.postpush;

import ag.act.entity.Post;
import ag.act.entity.Push;
import ag.act.model.PushRequest;
import ag.act.model.Status;

import java.time.Instant;
import java.util.Optional;

public record PostPushUpdaterInput(
    Status postOriginalStatus,
    Boolean isActiveForUpdate,
    Post post,
    PushRequest requestPush,
    Push existingPush,
    boolean isActiveStartDateOfPostInFuture,
    Instant activeStartDate
) {

    public Optional<Push> getOptionalPush() {
        return Optional.ofNullable(existingPush);
    }
}
