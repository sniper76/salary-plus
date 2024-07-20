package ag.act.exception;

import ag.act.enums.ActErrorCode;

public class WebVerificationCodeNotFoundException extends ActWarningException {

    public WebVerificationCodeNotFoundException() {
        super(ActErrorCode.WEB_VERIFICATION_CODE_NOT_FOUND_ERROR_CODE.getCode(), "개인안심번호를 다시 입력해주세요.");
    }
}
