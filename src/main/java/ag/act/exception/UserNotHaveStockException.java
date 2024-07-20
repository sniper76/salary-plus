package ag.act.exception;

import ag.act.enums.ActErrorCode;

public class UserNotHaveStockException extends ActWarningException {

    public UserNotHaveStockException(String message) {
        super(ActErrorCode.FORBIDDEN_USER_HOLDING_STOCK.getCode(), message);
    }

    public UserNotHaveStockException(String message, Throwable cause) {
        super(ActErrorCode.FORBIDDEN_USER_HOLDING_STOCK.getCode(), message, cause);
    }
}
