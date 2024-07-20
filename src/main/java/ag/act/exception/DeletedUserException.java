package ag.act.exception;

public class DeletedUserException extends ActRuntimeException {

    public DeletedUserException(String message) {
        super(message);
    }

    public DeletedUserException(String message, Throwable cause) {
        super(message, cause);
    }
}
