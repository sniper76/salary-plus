package ag.act.exception;

public class BatchExecutionException extends ActRuntimeException {

    public BatchExecutionException(String message) {
        super(message);
    }

    public BatchExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
