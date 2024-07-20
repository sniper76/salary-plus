package ag.act.exception;

public class VerificationPinNumberException extends ActWarningException {
    public VerificationPinNumberException(String message) {
        super(message);
    }

    public VerificationPinNumberException(String message, Throwable cause) {
        super(message, cause);
    }

    public VerificationPinNumberException(Integer errorCode, String message) {
        super(errorCode, message);
    }

    public VerificationPinNumberException(Integer errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
