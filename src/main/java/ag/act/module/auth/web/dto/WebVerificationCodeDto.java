package ag.act.module.auth.web.dto;

import java.time.LocalDateTime;

public record WebVerificationCodeDto(LocalDateTime baseDateTime, String verificationCode) {

}
