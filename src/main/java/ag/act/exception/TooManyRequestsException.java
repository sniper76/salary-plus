package ag.act.exception;

public class TooManyRequestsException extends ActWarningException {

    public TooManyRequestsException(String message) {
        super(message);
    }

    public TooManyRequestsException(String message, Throwable cause) {
        super(message, cause);
    }

    public TooManyRequestsException(Integer errorCode, String message) {
        super(errorCode, message);
    }

    public TooManyRequestsException(Integer errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
