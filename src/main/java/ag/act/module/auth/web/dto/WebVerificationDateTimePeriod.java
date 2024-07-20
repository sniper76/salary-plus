package ag.act.module.auth.web.dto;

import java.time.LocalDateTime;

public record WebVerificationDateTimePeriod(
    LocalDateTime verificationCodeStartDateTime,
    LocalDateTime verificationCodeEndDateTime
) {
}
