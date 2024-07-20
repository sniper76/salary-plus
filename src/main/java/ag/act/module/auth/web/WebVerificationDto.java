package ag.act.module.auth.web;

import ag.act.entity.User;
import ag.act.entity.WebVerification;
import ag.act.model.WebVerificationStatus;

public record WebVerificationDto(
    WebVerification webVerification,
    WebVerificationStatus status,
    User user
) {
    public static WebVerificationDto of(WebVerification webVerification, WebVerificationStatus status) {
        return new WebVerificationDto(
            webVerification,
            status,
            null
        );
    }
}
