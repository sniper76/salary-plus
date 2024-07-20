package ag.act.module.auth.web.converter;

import ag.act.entity.WebVerification;
import ag.act.module.auth.web.WebVerificationBase;
import ag.act.module.auth.web.WebVerificationDateTimeProvider;
import ag.act.module.auth.web.dto.WebVerificationCodeDto;
import ag.act.module.auth.web.dto.WebVerificationDateTimePeriod;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class WebVerificationConverter implements WebVerificationBase {

    public WebVerification convert(WebVerificationCodeDto webVerificationCodeDto, UUID authenticationReference) {
        final WebVerificationDateTimePeriod webVerificationDateTimePeriod = WebVerificationDateTimeProvider.getPeriod();

        return WebVerification.of(
            webVerificationCodeDto,
            authenticationReference,
            webVerificationDateTimePeriod
        );
    }
}
