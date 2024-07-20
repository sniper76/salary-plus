package ag.act.exception;

import lombok.Getter;

@Getter
public class ActException extends Exception {

    private final Integer errorCode;

    public ActException(String message) {
        this(null, message);
    }

    public ActException(Integer errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ActException(String message, Throwable cause) {
        this(null, message, cause);
    }

    public ActException(Integer errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
