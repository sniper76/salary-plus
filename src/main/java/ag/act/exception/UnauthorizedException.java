package ag.act.exception;

public class UnauthorizedException extends ActRuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnauthorizedException(Integer errorCode, String message) {
        super(errorCode, message);
    }

    public UnauthorizedException(Integer errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
