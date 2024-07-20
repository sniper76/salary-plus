package ag.act.module.post;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class PostAndCommentAopCurrentDateTimeProvider {

    public LocalDateTime get() {
        return LocalDateTime.now();
    }

    public long getSecondsUntil(LocalDateTime endDateTime) {
        return Duration.between(get(), endDateTime).getSeconds();
    }
}
