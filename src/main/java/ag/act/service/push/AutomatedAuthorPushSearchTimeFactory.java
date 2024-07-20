package ag.act.service.push;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AutomatedAuthorPushSearchTimeFactory {

    private static final int FIVE_MINUTES = 5;

    public LocalDateTime getFiveMinuteAge() {
        return LocalDateTime.now().minusMinutes(FIVE_MINUTES);
    }
}
