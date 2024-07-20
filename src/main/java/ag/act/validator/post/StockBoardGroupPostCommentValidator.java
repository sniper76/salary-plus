package ag.act.validator.post;

import ag.act.entity.Comment;
import ag.act.exception.BadRequestException;
import ag.act.exception.TooManyRequestsException;
import ag.act.model.Status;
import ag.act.module.post.PostAndCommentAopCurrentDateTimeProvider;
import ag.act.module.time.TimeDisplayFormatter;
import ag.act.util.StatusUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class StockBoardGroupPostCommentValidator {
    private final PostAndCommentAopCurrentDateTimeProvider postAndCommentAopCurrentDateTimeProvider;
    private final TimeDisplayFormatter timeDisplayFormatter;

    public void validateStatusNotDeleted(Status status) {
        if (StatusUtil.getDeleteStatuses().contains(status)) {
            throw new BadRequestException("이미 삭제된 댓글입니다.");
        }
    }

    public void validateCommentCooldown(Comment latestComment, int commentCooldownSeconds) {
        long remainingSeconds = getRemainingSecondsUntilCommentingEnabled(latestComment, commentCooldownSeconds);

        if (remainingSeconds > 0) {
            String remainingTimeDisplay = timeDisplayFormatter.format(remainingSeconds);

            throw new TooManyRequestsException(
                "잠시 후 다시 시도해 주세요. 댓글과 답글 등록은 도배 방지를 위해 일정 시간 간격을 두고 가능합니다.(%s 남음)".formatted(remainingTimeDisplay)
            );
        }
    }

    private long getRemainingSecondsUntilCommentingEnabled(Comment comment, final int commentCooldownSeconds) {
        LocalDateTime enableNextCommentingTime = comment.getCreatedAt().plusSeconds(commentCooldownSeconds);

        return postAndCommentAopCurrentDateTimeProvider.getSecondsUntil(enableNextCommentingTime);
    }
}
