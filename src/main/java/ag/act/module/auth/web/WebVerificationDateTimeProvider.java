package ag.act.module.auth.web;

import ag.act.module.auth.web.dto.WebVerificationDateTimePeriod;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class WebVerificationDateTimeProvider implements WebVerificationBase {

    private WebVerificationDateTimeProvider() {
    }

    public static LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }

    public static LocalDateTime getBaseDateTime() {
        final LocalDateTime currentDateTime = getCurrentDateTime();
        final int minute = currentDateTime.getMinute();

        return currentDateTime
            .withMinute(roundToNearestFive(minute))
            .truncatedTo(ChronoUnit.MINUTES);
    }

    public static LocalDateTime getMinimumSearchStartDateTime() {
        return getBaseDateTime().minusMinutes(FIVE_MINUTES);
    }

    private static int roundToNearestFive(final int minute) {
        return minute / FIVE_MINUTES * FIVE_MINUTES;
    }

    public static WebVerificationDateTimePeriod getPeriod() {
        final LocalDateTime verificationCodeStartDateTime = getCurrentDateTime();
        final LocalDateTime verificationCodeEndDateTime = verificationCodeStartDateTime.plusMinutes(VERIFICATION_CODE_DURATION_MINUTES);

        return new WebVerificationDateTimePeriod(
            verificationCodeStartDateTime,
            verificationCodeEndDateTime
        );
    }
}
