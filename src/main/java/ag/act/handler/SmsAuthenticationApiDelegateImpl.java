package ag.act.handler;

import ag.act.api.SmsAuthenticationApiDelegate;
import ag.act.facade.auth.SmsAuthFacade;
import ag.act.model.AuthUserResponse;
import ag.act.model.ResendAuthRequest;
import ag.act.model.ResendAuthResponse;
import ag.act.model.SendAuthRequest;
import ag.act.model.SendAuthResponse;
import ag.act.model.VerifyAuthCodeRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class SmsAuthenticationApiDelegateImpl implements SmsAuthenticationApiDelegate {

    private final SmsAuthFacade smsAuthFacade;

    public SmsAuthenticationApiDelegateImpl(SmsAuthFacade smsAuthFacade) {
        this.smsAuthFacade = smsAuthFacade;
    }

    @Override
    public ResponseEntity<SendAuthResponse> sendAuthRequest(SendAuthRequest sendAuthRequest) {
        return ResponseEntity.ok(smsAuthFacade.sendAuthRequest(sendAuthRequest));
    }

    @Override
    public ResponseEntity<AuthUserResponse> verifyAuthCode(VerifyAuthCodeRequest verifyAuthCodeRequest) {
        return ResponseEntity.ok(smsAuthFacade.verifyAuthCode(verifyAuthCodeRequest));
    }

    @Override
    public ResponseEntity<ResendAuthResponse> resendAuthRequest(ResendAuthRequest resendAuthRequest) {
        return ResponseEntity.ok(smsAuthFacade.resendAuthRequest(resendAuthRequest));
    }
}
