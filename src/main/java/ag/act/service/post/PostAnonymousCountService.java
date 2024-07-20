package ag.act.service.post;


import ag.act.core.configuration.AnonymousUserContentLimits;
import ag.act.entity.UserAnonymousCount;
import ag.act.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PostAnonymousCountService {
    private final AnonymousUserContentLimits anonymousUserContentLimits;

    public PostAnonymousCountService(AnonymousUserContentLimits anonymousUserContentLimits) {
        this.anonymousUserContentLimits = anonymousUserContentLimits;
    }

    public UserAnonymousCount validateAndIncreaseCount(UserAnonymousCount userAnonymousCount) {
        if (anonymousUserContentLimits.getPostLimitCount() <= userAnonymousCount.getPostCount()) {
            errorLog(userAnonymousCount);
            throw new BadRequestException("익명 게시글 일일 작성횟수를 초과하였습니다.");
        }

        userAnonymousCount.setPostCount(userAnonymousCount.getPostCount() + 1);
        return userAnonymousCount;
    }

    private void errorLog(UserAnonymousCount userAnonymousCount) {
        log.error(
            "Exceeding the limit of anonymous post count: {}/max:{}, user id: {}",
            userAnonymousCount.getPostCount(),
            anonymousUserContentLimits.getPostLimitCount(),
            userAnonymousCount.getUserId()
        );
    }
}
