package ag.act.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class ActRuntimeException extends RuntimeException {

    private final Integer errorCode;
    private final Map<String, Object> errorData;

    public ActRuntimeException(String message) {
        this(null, message);
    }

    public ActRuntimeException(Integer errorCode, String message) {
        this(errorCode, message, (Map<String, Object>) null);
    }

    public ActRuntimeException(Integer errorCode, String message, Map<String, Object> errorData) {
        super(message);
        this.errorCode = errorCode;
        this.errorData = errorData;
    }

    public ActRuntimeException(String message, Throwable cause) {
        this(null, message, cause);
    }

    public ActRuntimeException(Integer errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.errorData = null;
    }
}
