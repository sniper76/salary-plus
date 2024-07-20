package ag.act.core.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class AnonymousUserContentLimits {
    private final int postLimitCount;
    private final int commentLimitCount;

    public AnonymousUserContentLimits(
        @Value("${act.user.anonymous-limit.post-count:3}") int postLimitCount,
        @Value("${act.user.anonymous-limit.comment-count:5}") int commentLimitCount
    ) {
        this.postLimitCount = postLimitCount;
        this.commentLimitCount = commentLimitCount;
    }
}
