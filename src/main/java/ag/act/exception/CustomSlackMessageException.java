package ag.act.exception;

public class CustomSlackMessageException extends ActRuntimeException {

    public CustomSlackMessageException(String message) {
        super(message);
    }

    public CustomSlackMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
