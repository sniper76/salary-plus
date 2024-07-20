package ag.act.module.auth.web;

import ag.act.entity.WebVerification;
import ag.act.exception.WebVerificationCodeExistException;
import ag.act.module.auth.web.converter.WebVerificationConverter;
import ag.act.module.auth.web.dto.WebVerificationCodeDto;
import ag.act.service.WebVerificationService;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class WebVerificationGenerator {

    private static final int MAX_ATTEMPTS = 20;
    private static final int DELAY_IN_MS = 100;
    private final WebVerificationCodeGenerator webVerificationCodeGenerator;
    private final WebVerificationService webVerificationService;
    private final WebVerificationConverter webVerificationConverter;

    @Retryable(
        retryFor = WebVerificationCodeExistException.class,
        maxAttempts = MAX_ATTEMPTS,
        backoff = @Backoff(delay = DELAY_IN_MS)
    )
    public WebVerification generateWebVerificationCode(UUID authenticationReference) {

        final WebVerificationCodeDto webVerificationCodeDto = webVerificationCodeGenerator.generate();

        validateExistingWebVerificationCode(webVerificationCodeDto);

        try {
            return createNewWebVerification(authenticationReference, webVerificationCodeDto);
        } catch (DataIntegrityViolationException ex) {
            if (ex.getCause() instanceof ConstraintViolationException) {
                throw getWebVerificationCodeExistException(webVerificationCodeDto.verificationCode());
            } else {
                throw ex;
            }
        }
    }

    private WebVerification createNewWebVerification(
        final UUID authenticationReference,
        final WebVerificationCodeDto webVerificationCodeDto
    ) {
        return webVerificationService.save(
            webVerificationConverter.convert(webVerificationCodeDto, authenticationReference)
        );
    }

    private void validateExistingWebVerificationCode(final WebVerificationCodeDto webVerificationCodeDto) {
        getFirstValidOneByVerificationCode(webVerificationCodeDto)
            .ifPresent(webVerification -> {
                throw getWebVerificationCodeExistException(webVerificationCodeDto.verificationCode());
            });
    }

    private Optional<WebVerification> getFirstValidOneByVerificationCode(final WebVerificationCodeDto webVerificationCodeDto) {
        return webVerificationService.findFirstValidOneByVerificationCode(
            webVerificationCodeDto.verificationCode(),
            WebVerificationDateTimeProvider.getCurrentDateTime()
        );
    }

    private WebVerificationCodeExistException getWebVerificationCodeExistException(String verificationCode) {
        return new WebVerificationCodeExistException("Web verification code(%s) 이미 존재합니다.".formatted(verificationCode));
    }
}
