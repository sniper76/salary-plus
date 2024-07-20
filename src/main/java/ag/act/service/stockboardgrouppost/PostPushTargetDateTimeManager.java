package ag.act.service.stockboardgrouppost;

import ag.act.converter.DateTimeConverter;
import ag.act.module.cache.PushPreference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class PostPushTargetDateTimeManager {

    private final PushPreference pushPreference;

    public Instant generatePushTargetDateTime(Instant instant) {
        return getInstantWithAdditionalMinutes(instant);
    }

    public LocalDateTime generatePushTargetDateTimeForLocalDateTime(Instant instant) {
        return DateTimeConverter.convert(getInstantWithAdditionalMinutes(instant));
    }

    private Instant getInstantWithAdditionalMinutes(Instant instant) {
        final int additionalPushTimeMinutes = pushPreference.getAdditionalPushTimeMinutes();

        return instant.plus(additionalPushTimeMinutes, ChronoUnit.MINUTES);
    }
}
