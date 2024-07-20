package ag.act.module.auth.web;

import ag.act.configuration.security.ActUserProvider;
import ag.act.entity.User;
import ag.act.entity.WebVerification;
import ag.act.enums.verification.VerificationOperationType;
import ag.act.enums.verification.VerificationType;
import ag.act.exception.WebVerificationCodeExpiredException;
import ag.act.exception.WebVerificationCodeNotFoundException;
import ag.act.model.VerificationCodeRequest;
import ag.act.service.WebVerificationService;
import ag.act.service.user.UserVerificationHistoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Transactional
public class WebVerificationCodeRedeemer {
    private final WebVerificationService webVerificationService;
    private final UserVerificationHistoryService userVerificationHistoryService;

    public void redeem(VerificationCodeRequest verificationCodeRequest) {
        final LocalDateTime currentDateTime = WebVerificationDateTimeProvider.getCurrentDateTime();
        final WebVerification webVerification = validateAndGet(verificationCodeRequest, currentDateTime);

        redeemWebVerification(webVerification, currentDateTime);
        createUserVerificationHistory();
    }

    private void redeemWebVerification(WebVerification webVerification, LocalDateTime currentDateTime) {
        webVerification.setUserId(ActUserProvider.getNoneNull().getId());
        webVerification.setVerificationCodeRedeemedAt(currentDateTime);

        webVerificationService.save(webVerification);
    }

    private void createUserVerificationHistory() {
        final User user = ActUserProvider.getNoneNull();

        userVerificationHistoryService.create(
            user.getId(),
            VerificationType.WEB,
            VerificationOperationType.VERIFICATION
        );
    }

    private WebVerification validateAndGet(VerificationCodeRequest verificationCodeRequest, LocalDateTime currentDateTime) {
        final LocalDateTime minimumSearchStartDateTime = WebVerificationDateTimeProvider.getMinimumSearchStartDateTime();

        final WebVerification webVerification = findVerificationCode(verificationCodeRequest.getVerificationCode(), minimumSearchStartDateTime)
            .orElseThrow(WebVerificationCodeNotFoundException::new);

        if (webVerification.getVerificationCodeEndDateTime().isBefore(currentDateTime)) {
            throw new WebVerificationCodeExpiredException();
        }

        return webVerification;
    }

    private Optional<WebVerification> findVerificationCode(
        String verificationCode,
        LocalDateTime minimumSearchStartDateTime
    ) {
        return webVerificationService.findFirstValidOneByVerificationCode(
            verificationCode,
            minimumSearchStartDateTime
        );
    }
}
