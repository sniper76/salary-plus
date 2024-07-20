package ag.act.module.time;

import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class TimeDisplayFormatter {

    public String format(long seconds) {
        Duration duration = Duration.ofSeconds(seconds);
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long remainingSeconds = duration.toSecondsPart();

        StringBuilder result = new StringBuilder();
        if (hours > 0) {
            result.append(hours).append("시간 ");
        }
        if (minutes > 0) {
            result.append(minutes).append("분 ");
        }
        if (remainingSeconds > 0 || result.isEmpty()) {
            result.append(remainingSeconds).append("초");
        }

        return result.toString().trim();
    }
}
