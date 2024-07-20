package ag.act.exception;

public class NotHaveStockException extends ActRuntimeException {

    public NotHaveStockException(String message) {
        super(message);
    }

    public NotHaveStockException(String message, Throwable cause) {
        super(message, cause);
    }
}
