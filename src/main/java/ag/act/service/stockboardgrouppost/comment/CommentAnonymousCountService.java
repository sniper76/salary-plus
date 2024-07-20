package ag.act.service.stockboardgrouppost.comment;


import ag.act.core.configuration.AnonymousUserContentLimits;
import ag.act.entity.User;
import ag.act.entity.UserAnonymousCount;
import ag.act.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentAnonymousCountService {
    private static final int ADMIN_ANONYMOUS_FIXED_COUNT = 0;
    private final AnonymousUserContentLimits anonymousUserContentLimits;

    public UserAnonymousCount validateAndIncreaseCount(User user, UserAnonymousCount userAnonymousCount) {
        if (user.isAdmin()) {
            userAnonymousCount.setCommentCount(ADMIN_ANONYMOUS_FIXED_COUNT);
            return userAnonymousCount;
        }
        if (anonymousUserContentLimits.getCommentLimitCount() <= userAnonymousCount.getCommentCount()) {
            errorLog(userAnonymousCount);
            throw new BadRequestException("익명 댓글/답글 일일 작성횟수를 초과하였습니다.");
        }

        userAnonymousCount.setCommentCount(userAnonymousCount.getCommentCount() + 1);
        return userAnonymousCount;
    }

    private void errorLog(UserAnonymousCount userAnonymousCount) {
        log.error(
            "Exceeding the limit of anonymous comment count: {}/max:{}, user id: {}",
            userAnonymousCount.getCommentCount(),
            anonymousUserContentLimits.getCommentLimitCount(),
            userAnonymousCount.getUserId()
        );
    }
}
