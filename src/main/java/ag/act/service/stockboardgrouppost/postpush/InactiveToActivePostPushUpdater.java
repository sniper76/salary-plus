package ag.act.service.stockboardgrouppost.postpush;

import ag.act.entity.Push;
import ag.act.exception.BadRequestException;
import ag.act.model.PushRequest;
import ag.act.util.StatusUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class InactiveToActivePostPushUpdater implements PostPushUpdater {

    private final ToActivePostPushUpdater toActivePostPushUpdater;

    @Override
    public boolean supports(PostPushUpdaterInput input) {
        return StatusUtil.getInactiveStatuses().contains(input.postOriginalStatus()) && input.isActiveForUpdate();
    }

    @Override
    public void update(PostPushUpdaterInput input) {

        final Optional<Push> optionalPush = input.getOptionalPush();
        final PushRequest pushRequest = input.requestPush();
        final boolean isActiveStartDateOfPostInFuture = input.isActiveStartDateOfPostInFuture();
        if (optionalPush.isEmpty() && pushRequest == null) {
            return;
        }

        if (!isActiveStartDateOfPostInFuture) {
            throw new BadRequestException("게시시작일이 이미 지난 미노출 게시글을 다시 노출로 변경할 수 없습니다.");
        }
        toActivePostPushUpdater.update(input);
    }
}
