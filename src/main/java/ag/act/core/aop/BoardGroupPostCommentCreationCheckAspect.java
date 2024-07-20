package ag.act.core.aop;

import ag.act.configuration.security.ActUserProvider;
import ag.act.entity.Comment;
import ag.act.entity.User;
import ag.act.enums.AppPreferenceType;
import ag.act.module.cache.AppPreferenceCache;
import ag.act.module.post.PostAndCommentAopCurrentDateTimeProvider;
import ag.act.service.stockboardgrouppost.comment.CommentService;
import ag.act.validator.post.StockBoardGroupPostCommentValidator;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@SuppressWarnings("linelength")
@Aspect
@RequiredArgsConstructor
@Component
public class BoardGroupPostCommentCreationCheckAspect {

    private final CommentService commentService;
    private final StockBoardGroupPostCommentValidator stockBoardGroupPostCommentValidator;
    private final AppPreferenceCache appPreferenceCache;
    private final PostAndCommentAopCurrentDateTimeProvider postAndCommentAopCurrentDateTimeProvider;

    @Before(value = "execution(* ag.act.handler.stockboardgrouppost.StockBoardGroupPostCommentApiDelegateImpl.createBoardGroupPostComment(..)) "
                    + "&& args(stockCode, boardGroupName, postId, ..) "
                    + "|| execution(* ag.act.handler.stockboardgrouppost.StockBoardGroupPostCommentReplyApiDelegateImpl.createBoardGroupPostCommentReply(..)) "
                    + "&& args(stockCode, boardGroupName, postId, ..) ", argNames = "stockCode,boardGroupName,postId")
    public void checkCommentCreation(String stockCode, String boardGroupName, Long postId) {
        User user = ActUserProvider.getNoneNull();
        if (user.isAdmin()) {
            return;
        }

        validateCommentCooldown(user, postId);
    }

    private void validateCommentCooldown(User user, Long postId) {
        final int commentCooldownSeconds = appPreferenceCache.getValue(AppPreferenceType.COMMENT_RESTRICTION_INTERVAL_SECONDS);

        Optional<Comment> latestCommentOptional = findLatestCommentWithin(user, postId, commentCooldownSeconds);

        latestCommentOptional.ifPresent(latestComment ->
            stockBoardGroupPostCommentValidator.validateCommentCooldown(latestComment, commentCooldownSeconds)
        );
    }

    private Optional<Comment> findLatestCommentWithin(User user, Long postId, final int commentCooldownSeconds) {
        final LocalDateTime startTime = postAndCommentAopCurrentDateTimeProvider.get().minusSeconds(commentCooldownSeconds);

        return commentService.findLatestCommentFrom(user.getId(), postId, startTime);
    }
}