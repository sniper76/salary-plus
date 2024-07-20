package ag.act.service.stockboardgrouppost.postpush;

import ag.act.entity.Push;
import ag.act.exception.BadRequestException;
import ag.act.model.PushRequest;
import ag.act.model.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ActiveToActivePostPushUpdater implements PostPushUpdater {

    private final ToActivePostPushUpdater toActivePostPushUpdater;

    @Override
    public boolean supports(PostPushUpdaterInput input) {
        return input.postOriginalStatus() == Status.ACTIVE && input.isActiveForUpdate();
    }

    @Override
    public void update(PostPushUpdaterInput input) {

        final Optional<Push> optionalPush = input.getOptionalPush();
        final PushRequest pushRequest = input.requestPush();
        final boolean isActiveStartDateOfPostInFuture = input.isActiveStartDateOfPostInFuture();
        if (optionalPush.isEmpty() && pushRequest == null) {
            return;
        }
        if (!isActiveStartDateOfPostInFuture && pushRequest != null) {
            throw new BadRequestException("게시 시작된 게시글의 푸시는 변경할 수 없습니다.");
        }
        toActivePostPushUpdater.update(input);
    }
}
