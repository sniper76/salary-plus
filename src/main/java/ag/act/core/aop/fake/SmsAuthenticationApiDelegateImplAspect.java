package ag.act.core.aop.fake;

import ag.act.configuration.security.CryptoHelper;
import ag.act.configuration.security.TokenProvider;
import ag.act.converter.user.AuthUserResponseConverter;
import ag.act.entity.User;
import ag.act.exception.NotFoundException;
import ag.act.model.AuthUserResponse;
import ag.act.model.ResendAuthRequest;
import ag.act.model.ResendAuthResponse;
import ag.act.model.SendAuthRequest;
import ag.act.model.SendAuthResponse;
import ag.act.model.VerifyAuthCodeRequest;
import ag.act.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class SmsAuthenticationApiDelegateImplAspect {

    private static final String BY_PASS_PHONE_NUMBER = "01098782868";
    private static final String BY_PASS_PIN_NUMBER = "999999";
    private static final String BY_PASS_USERNAME = "apptester";
    private static final String BY_PASS_TX_SEQ_NO = "230112102659KC687332";

    private final boolean fakeUserEnabled;
    private final TokenProvider tokenProvider;
    private final CryptoHelper cryptoHelper;
    private final UserRepository userRepository;
    private final AuthUserResponseConverter authUserResponseConverter;

    public SmsAuthenticationApiDelegateImplAspect(
        @Value("${security.fake-user.enabled:false}") boolean fakeUserEnabled,
        TokenProvider tokenProvider,
        CryptoHelper cryptoHelper,
        UserRepository userRepository,
        AuthUserResponseConverter authUserResponseConverter
    ) {
        this.fakeUserEnabled = fakeUserEnabled;
        this.tokenProvider = tokenProvider;
        this.cryptoHelper = cryptoHelper;
        this.userRepository = userRepository;
        this.authUserResponseConverter = authUserResponseConverter;
    }

    @Around("execution(* ag.act.handler.SmsAuthenticationApiDelegateImpl.verifyAuthCode(..))")
    public Object aroundVerifyAuthCode(ProceedingJoinPoint joinPoint) throws Throwable {

        final VerifyAuthCodeRequest requestParameter = (VerifyAuthCodeRequest) joinPoint.getArgs()[0];

        if (!isByPassRequest(requestParameter)) {
            return joinPoint.proceed();
        }

        return makeFakeSuccessResponse(requestParameter);
    }

    @Around("execution(* ag.act.handler.SmsAuthenticationApiDelegateImpl.sendAuthRequest(..))")
    public Object aroundSendAuthRequest(ProceedingJoinPoint joinPoint) throws Throwable {

        final SendAuthRequest requestParameter = (SendAuthRequest) joinPoint.getArgs()[0];

        if (!isByPassRequest(requestParameter)) {
            return joinPoint.proceed();
        }

        return makeFakeSuccessResponse(requestParameter);
    }

    @Around("execution(* ag.act.handler.SmsAuthenticationApiDelegateImpl.resendAuthRequest(..))")
    public Object aroundResendAuthRequest(ProceedingJoinPoint joinPoint) throws Throwable {

        final ResendAuthRequest requestParameter = (ResendAuthRequest) joinPoint.getArgs()[0];

        if (!isByPassRequest(requestParameter)) {
            return joinPoint.proceed();
        }

        return makeFakeSuccessResponse(requestParameter);
    }

    private ResponseEntity<AuthUserResponse> makeFakeSuccessResponse(VerifyAuthCodeRequest requestParameter) throws Exception {
        final String hashedPhoneNumber = cryptoHelper.encrypt(requestParameter.getPhoneNumber());
        final User user = userRepository.findByHashedPhoneNumber(hashedPhoneNumber)
            .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));
        return ResponseEntity.ok(
            authUserResponseConverter.convert(user, tokenProvider.createAppToken(user.getId().toString()), null)
        );
    }

    @SuppressWarnings("unused")
    private ResponseEntity<SendAuthResponse> makeFakeSuccessResponse(SendAuthRequest requestParameter) {
        return ResponseEntity.ok(
            new SendAuthResponse()
                .txSeqNo(BY_PASS_TX_SEQ_NO)
                .telComResCd("0000")
                .resultCode("B000")
                .resultMessage("성공")
        );
    }

    @SuppressWarnings("unused")
    private ResponseEntity<ResendAuthResponse> makeFakeSuccessResponse(ResendAuthRequest requestParameter) {
        return ResponseEntity.ok(
            new ResendAuthResponse()
                .txSeqNo(BY_PASS_TX_SEQ_NO)
                .resendCount(1)
                .resultCode("B000")
                .resultMessage("성공")
        );
    }

    private boolean isByPassRequest(VerifyAuthCodeRequest requestParameter) {
        return fakeUserEnabled
            && BY_PASS_PIN_NUMBER.equals(requestParameter.getCode())
            && BY_PASS_USERNAME.equals(requestParameter.getName())
            && BY_PASS_PHONE_NUMBER.equals(requestParameter.getPhoneNumber());
    }

    private boolean isByPassRequest(SendAuthRequest requestParameter) {
        return fakeUserEnabled
            && BY_PASS_USERNAME.equals(requestParameter.getName())
            && BY_PASS_PHONE_NUMBER.equals(requestParameter.getPhoneNumber());
    }

    private boolean isByPassRequest(ResendAuthRequest requestParameter) {
        return fakeUserEnabled
            && BY_PASS_TX_SEQ_NO.equals(requestParameter.getTxSeqNo())
            && BY_PASS_PHONE_NUMBER.equals(requestParameter.getPhoneNumber());
    }

}
