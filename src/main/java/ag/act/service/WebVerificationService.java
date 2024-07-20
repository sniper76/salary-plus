package ag.act.service;

import ag.act.entity.WebVerification;
import ag.act.model.WebVerificationCodeRequest;
import ag.act.module.auth.web.WebVerificationBase;
import ag.act.module.auth.web.WebVerificationDateTimeProvider;
import ag.act.repository.WebVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WebVerificationService implements WebVerificationBase {
    private final WebVerificationRepository webVerificationRepository;

    public WebVerification save(WebVerification webVerification) {
        return webVerificationRepository.saveAndFlush(webVerification);
    }

    public Optional<WebVerification> findFirstWebVerification(WebVerificationCodeRequest webVerificationCodeRequest) {
        return webVerificationRepository.findFirstByVerificationCodeAndAuthenticationReference(
            webVerificationCodeRequest.getVerificationCode(),
            webVerificationCodeRequest.getAuthenticationReference()
        );
    }

    public Optional<WebVerification> findFirstValidOneByVerificationCode(String verificationCode, LocalDateTime localDateTime) {
        return webVerificationRepository.findFirstByVerificationCodeAndVerificationCodeEndDateTimeGreaterThanEqual(
            verificationCode,
            localDateTime
        );
    }

    public void expireUnusedWebVerificationCodes(final UUID authenticationReference) {
        webVerificationRepository.findAllByAuthenticationReferenceAndUserIdIsNull(authenticationReference)
            .forEach(webVerification -> {
                webVerification.setVerificationCodeEndDateTime(WebVerificationDateTimeProvider.getCurrentDateTime());
                save(webVerification);
            });
    }
}
