package ag.act.module.auth.web;

import ag.act.entity.WebVerification;
import ag.act.exception.ExpiredVerificationCodeException;
import ag.act.exception.NotFoundException;
import ag.act.exception.NotYetVerifiedException;
import ag.act.model.WebVerificationCodeRequest;
import ag.act.service.WebVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WebVerificationCodeVerifier {

    private static final int MAX_ATTEMPTS = 6;
    private static final int DELAY_IN_MS = 500;
    private final WebVerificationService webVerificationService;

    @Retryable(
        retryFor = NotYetVerifiedException.class,
        maxAttempts = MAX_ATTEMPTS,
        backoff = @Backoff(delay = DELAY_IN_MS)
    )
    public WebVerification getVerifiedWebVerification(WebVerificationCodeRequest webVerificationCodeRequest) {

        final WebVerification webVerification = getWebVerification(webVerificationCodeRequest);

        if (isExpired(webVerification)) {
            throw new ExpiredVerificationCodeException(webVerification);
        }

        if (!isVerified(webVerification)) {
            throw new NotYetVerifiedException(webVerification);
        }

        return webVerification;
    }

    private WebVerification getWebVerification(final WebVerificationCodeRequest webVerificationCodeRequest) {
        return webVerificationService.findFirstWebVerification(webVerificationCodeRequest)
            .orElseThrow(() -> new NotFoundException("안심번호를 찾을 수 없습니다."));
    }

    private boolean isVerified(WebVerification webVerification) {
        return webVerification.getUserId() != null
            && webVerification.getVerificationCodeRedeemedAt() != null;
    }

    private boolean isExpired(WebVerification webVerification) {
        final LocalDateTime currentDateTime = WebVerificationDateTimeProvider.getCurrentDateTime();
        final LocalDateTime verificationCodeEndDateTime = webVerification.getVerificationCodeEndDateTime();

        return currentDateTime.isAfter(verificationCodeEndDateTime);
    }
}
