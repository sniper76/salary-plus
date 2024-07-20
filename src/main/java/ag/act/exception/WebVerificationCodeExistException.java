package ag.act.exception;

import lombok.Getter;

@Getter
public class WebVerificationCodeExistException extends ActWarningException {
    public WebVerificationCodeExistException(String message) {
        super(message);
    }

    public WebVerificationCodeExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public WebVerificationCodeExistException(Integer errorCode, String message) {
        super(errorCode, message);
    }

    public WebVerificationCodeExistException(Integer errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
