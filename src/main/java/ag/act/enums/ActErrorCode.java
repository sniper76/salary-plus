package ag.act.enums;

import lombok.Getter;

@Getter
public enum ActErrorCode {
    IS_CHANGE_PASSWORD_REQUIRED(4031),
    FORBIDDEN_USER_HOLDING_STOCK(4032),
    PIN_VERIFICATION_FAILED(4011),
    KCB_OK_CERT_FAILED(4012),
    APP_VERSION_VERIFICATION_FAILED(4261),
    DUPLICATE_INACTIVE_STOP_WORD_ERROR_CODE(4001),
    WEB_VERIFICATION_CODE_NOT_FOUND_ERROR_CODE(4002),
    WEB_VERIFICATION_CODE_EXPIRED_ERROR_CODE(4003)
    ;

    private final Integer code;

    ActErrorCode(Integer code) {
        this.code = code;
    }
}
