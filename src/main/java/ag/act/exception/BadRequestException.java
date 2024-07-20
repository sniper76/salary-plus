package ag.act.exception;

import java.util.Map;

public class BadRequestException extends ActWarningException {

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadRequestException(Integer errorCode, String message) {
        super(errorCode, message);
    }

    public BadRequestException(Integer errorCode, String message, Map<String, Object> data) {
        super(errorCode, message, data);
    }

    public BadRequestException(Integer errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
