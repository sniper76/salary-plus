package ag.act.exception;

public class ActEmailException extends ActRuntimeException {

    public ActEmailException(String message) {
        super(message);
    }

    public ActEmailException(String message, Throwable cause) {
        super(message, cause);
    }

    public ActEmailException(Integer errorCode, String message) {
        super(errorCode, message);
    }

    public ActEmailException(Integer errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
