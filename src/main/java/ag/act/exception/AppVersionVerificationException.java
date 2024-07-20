package ag.act.exception;

import ag.act.enums.ActErrorCode;

public class AppVersionVerificationException extends UpgradeRequiredException {

    public AppVersionVerificationException() {
        super(ActErrorCode.APP_VERSION_VERIFICATION_FAILED.getCode(), "최신앱으로 업데이트해야 서비스를 이용할 수 있습니다");
    }
}
