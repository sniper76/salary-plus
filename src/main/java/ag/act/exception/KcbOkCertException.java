package ag.act.exception;

public class KcbOkCertException extends ActWarningException {
    public KcbOkCertException(String message) {
        super(message);
    }

    public KcbOkCertException(String message, Throwable cause) {
        super(message, cause);
    }

    public KcbOkCertException(Integer errorCode, String message) {
        super(errorCode, message);
    }

    public KcbOkCertException(Integer errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
