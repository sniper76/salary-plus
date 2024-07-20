package ag.act.module.auth.web;

import ag.act.module.auth.web.dto.WebVerificationCodeDto;
import ag.act.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class WebVerificationCodeGenerator {
    private final WebVerificationCodeUtil webVerificationCodeUtil;

    public WebVerificationCodeDto generate() {
        final LocalDateTime baseDateTime = WebVerificationDateTimeProvider.getBaseDateTime();
        final boolean isEven = isEvenMinute(baseDateTime);

        if (isEven) {
            return new WebVerificationCodeDto(baseDateTime, webVerificationCodeUtil.generateEvenCode());
        } else {
            return new WebVerificationCodeDto(baseDateTime, webVerificationCodeUtil.generateOddCode());
        }
    }

    private boolean isEvenMinute(final LocalDateTime baseDateTime) {
        return DateTimeUtil.isEvenMinute(baseDateTime);
    }
}
