package ag.act.exception;

import ag.act.enums.ActErrorCode;

public class WebVerificationCodeExpiredException extends ActWarningException {

    public WebVerificationCodeExpiredException() {
        super(ActErrorCode.WEB_VERIFICATION_CODE_EXPIRED_ERROR_CODE.getCode(), "인증 수단의 기간이 만료되어 재생성이 필요합니다.");
    }
}
