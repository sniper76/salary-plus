package ag.act.module.auth.web;

import ag.act.converter.DateTimeConverter;
import ag.act.entity.User;
import ag.act.entity.WebVerification;
import ag.act.exception.ExpiredVerificationCodeException;
import ag.act.exception.NotYetVerifiedException;
import ag.act.model.VerificationCodeRequest;
import ag.act.model.WebVerificationCodeGenerateRequest;
import ag.act.model.WebVerificationCodeGenerateResponse;
import ag.act.model.WebVerificationCodeRequest;
import ag.act.model.WebVerificationCodeResponse;
import ag.act.model.WebVerificationStatus;
import ag.act.module.auth.web.converter.WebVerificationCodeResponseConverter;
import ag.act.service.WebVerificationService;
import ag.act.service.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class WebVerificationFacade {
    private final WebVerificationCodeVerifier webVerificationCodeVerifier;
    private final WebVerificationGenerator webVerificationGenerator;
    private final WebVerificationService webVerificationService;
    private final WebVerificationCodeResponseConverter webVerificationCodeResponseConverter;
    private final UserService userService;
    private final WebVerificationCodeRedeemer webVerificationCodeRedeemer;

    public WebVerificationCodeGenerateResponse generateWebVerificationCode(
        WebVerificationCodeGenerateRequest webVerificationCodeGenerateRequest
    ) {
        final UUID authenticationReference = webVerificationCodeGenerateRequest.getAuthenticationReference();
        webVerificationService.expireUnusedWebVerificationCodes(authenticationReference);

        final WebVerification webVerification = webVerificationGenerator.generateWebVerificationCode(authenticationReference);

        return new WebVerificationCodeGenerateResponse()
            .verificationCode(webVerification.getVerificationCode())
            .expirationDateTime(DateTimeConverter.convert(webVerification.getVerificationCodeEndDateTime()));
    }

    public WebVerificationCodeResponse verifyWebVerificationCode(WebVerificationCodeRequest webVerificationCodeRequest) {
        final WebVerificationDto webVerificationDto = getWebVerificationDto(webVerificationCodeRequest);

        return webVerificationCodeResponseConverter.convert(webVerificationDto);
    }

    private WebVerificationDto getWebVerificationDto(WebVerificationCodeRequest webVerificationCodeRequest) {
        try {
            final WebVerification webVerification = webVerificationCodeVerifier.getVerifiedWebVerification(webVerificationCodeRequest);
            final User user = userService.getUser(webVerification.getUserId());

            return new WebVerificationDto(
                webVerification,
                WebVerificationStatus.VERIFIED,
                user
            );
        } catch (NotYetVerifiedException ex) {
            return WebVerificationDto.of(ex.getWebVerification(), WebVerificationStatus.WAITING);
        } catch (ExpiredVerificationCodeException ex) {
            return WebVerificationDto.of(ex.getWebVerification(), WebVerificationStatus.EXPIRED);
        }
    }

    public void redeemWebVerificationCode(VerificationCodeRequest verificationCodeRequest) {
        webVerificationCodeRedeemer.redeem(verificationCodeRequest);
    }
}
