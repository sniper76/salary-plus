package ag.act.handler;

import ag.act.api.AuthenticationApiDelegate;
import ag.act.core.guard.IsActiveUserGuard;
import ag.act.core.guard.UseGuards;
import ag.act.facade.auth.AuthFacade;
import ag.act.model.CheckEmailRequest;
import ag.act.model.CheckEmailResponse;
import ag.act.model.CheckNicknameRequest;
import ag.act.model.CheckNicknameResponse;
import ag.act.model.MyDataTokenResponse;
import ag.act.model.PinNumberRequest;
import ag.act.model.RegisterUserInfoRequest;
import ag.act.model.SimpleStringResponse;
import ag.act.model.UserResponse;
import ag.act.util.TokenUtil;
import ag.act.validator.DefaultRequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationApiDelegateImpl implements AuthenticationApiDelegate {

    private final AuthFacade authFacade;
    private final TokenUtil tokenUtil;
    private final DefaultRequestValidator defaultRequestValidator;

    @Override
    public ResponseEntity<UserResponse> registerUserInfo(RegisterUserInfoRequest registerUserInfoRequest) {
        return ResponseEntity.ok(authFacade.registerUserInfo(registerUserInfoRequest));
    }

    @Override
    @UseGuards({IsActiveUserGuard.class})
    public ResponseEntity<UserResponse> registerPinNumber(PinNumberRequest pinNumberRequest) {
        return ResponseEntity.ok(authFacade.registerPinNumber(pinNumberRequest));
    }

    @Override
    @UseGuards({IsActiveUserGuard.class})
    public ResponseEntity<UserResponse> verifyPinNumber(PinNumberRequest pinNumberRequest) {
        return ResponseEntity.ok(authFacade.verifyPinNumber(pinNumberRequest));
    }

    @Override
    @UseGuards({IsActiveUserGuard.class})
    public ResponseEntity<SimpleStringResponse> resetPinNumber() {
        return ResponseEntity.ok(authFacade.resetPinNumber());
    }

    @Override
    @UseGuards({IsActiveUserGuard.class})
    public ResponseEntity<MyDataTokenResponse> requestMyDataToken(String authorization) {
        return ResponseEntity.ok(authFacade.requestMyDataToken(tokenUtil.parse(authorization)));
    }

    @Override
    public ResponseEntity<Void> logout() {
        authFacade.logout();
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<CheckEmailResponse> checkEmail(CheckEmailRequest checkEmailRequest) {
        defaultRequestValidator.validateNotNull(checkEmailRequest.getEmail(), "형식에 맞지 않는 이메일입니다. ex) act123@naver.com");
        return ResponseEntity.ok(authFacade.checkEmail(checkEmailRequest.getEmail()));
    }

    @Override
    public ResponseEntity<CheckNicknameResponse> checkNickname(CheckNicknameRequest checkNicknameRequest) {
        defaultRequestValidator.validateNotNull(checkNicknameRequest.getNickname(), "닉네임을 확인해주세요.");
        return ResponseEntity.ok(authFacade.checkNickname(checkNicknameRequest.getNickname()));
    }

}
