package ag.act.exception;

import ag.act.entity.WebVerification;
import lombok.Getter;

@Getter
public class ExpiredVerificationCodeException extends ActWarningException {

    private final WebVerification webVerification;

    public ExpiredVerificationCodeException(WebVerification webVerification) {
        super("Expired verification code.");
        this.webVerification = webVerification;
    }
}