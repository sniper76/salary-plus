package ag.act.exception;

public class UpgradeRequiredException extends ActWarningException {

    public UpgradeRequiredException(String message) {
        this(null, message);
    }

    public UpgradeRequiredException(Integer errorCode, String message) {
        super(errorCode, message);
    }

    public UpgradeRequiredException(String message, Throwable cause) {
        this(null, message, cause);
    }

    public UpgradeRequiredException(Integer errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
