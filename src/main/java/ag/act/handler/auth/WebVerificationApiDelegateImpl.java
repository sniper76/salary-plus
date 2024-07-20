package ag.act.handler.auth;

import ag.act.api.WebVerificationApiDelegate;
import ag.act.core.guard.IsActiveUserGuard;
import ag.act.core.guard.UseGuards;
import ag.act.model.SimpleStringResponse;
import ag.act.model.VerificationCodeRequest;
import ag.act.model.WebVerificationCodeGenerateRequest;
import ag.act.model.WebVerificationCodeGenerateResponse;
import ag.act.model.WebVerificationCodeRequest;
import ag.act.model.WebVerificationCodeResponse;
import ag.act.module.auth.web.WebVerificationFacade;
import ag.act.util.SimpleStringResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebVerificationApiDelegateImpl implements WebVerificationApiDelegate {

    private final WebVerificationFacade webVerificationFacade;

    @Override
    public ResponseEntity<WebVerificationCodeGenerateResponse> generateWebVerificationCode(
        WebVerificationCodeGenerateRequest webVerificationCodeGenerateRequest
    ) {
        return ResponseEntity.ok(
            webVerificationFacade.generateWebVerificationCode(webVerificationCodeGenerateRequest)
        );
    }

    @Override
    public ResponseEntity<WebVerificationCodeResponse> verifyWebVerificationCode(WebVerificationCodeRequest webVerificationCodeRequest) {
        return ResponseEntity.ok(
            webVerificationFacade.verifyWebVerificationCode(webVerificationCodeRequest)
        );
    }

    @Override
    @UseGuards({IsActiveUserGuard.class})
    public ResponseEntity<SimpleStringResponse> redeemWebVerificationCode(VerificationCodeRequest verificationCodeRequest) {
        webVerificationFacade.redeemWebVerificationCode(verificationCodeRequest);
        return ResponseEntity.ok(
            SimpleStringResponseUtil.ok()
        );
    }
}
