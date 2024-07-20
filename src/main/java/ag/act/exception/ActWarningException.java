package ag.act.exception;

import java.util.Map;

public class ActWarningException extends ActRuntimeException {

    public ActWarningException(String message) {
        super(message);
    }

    public ActWarningException(String message, Throwable cause) {
        super(message, cause);
    }

    public ActWarningException(Integer errorCode, String message) {
        super(errorCode, message);
    }

    public ActWarningException(Integer errorCode, String message, Map<String, Object> data) {
        super(errorCode, message, data);
    }

    public ActWarningException(Integer errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
