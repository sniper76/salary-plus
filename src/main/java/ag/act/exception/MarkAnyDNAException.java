package ag.act.exception;

public class MarkAnyDNAException extends ActWarningException {
    public MarkAnyDNAException(String message) {
        super(message);
    }

    public MarkAnyDNAException(String message, Throwable cause) {
        super(message, cause);
    }

    public MarkAnyDNAException(Integer errorCode, String message) {
        super(errorCode, message);
    }

    public MarkAnyDNAException(Integer errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
