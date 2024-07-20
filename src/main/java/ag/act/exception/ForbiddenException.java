package ag.act.exception;

public class ForbiddenException extends ActRuntimeException {

    public ForbiddenException(String message) {
        this(null, message);
    }

    public ForbiddenException(Integer errorCode, String message) {
        super(errorCode, message);
    }

    public ForbiddenException(String message, Throwable cause) {
        this(null, message, cause);
    }

    public ForbiddenException(Integer errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
