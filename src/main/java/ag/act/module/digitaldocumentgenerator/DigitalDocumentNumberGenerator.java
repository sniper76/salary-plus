package ag.act.module.digitaldocumentgenerator;

import ag.act.enums.DigitalDocumentType;
import ag.act.module.digitaldocumentgenerator.datetimeprovider.DigitalDocumentFillCurrentDateTimeProvider;
import ag.act.util.DateTimeFormatUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DigitalDocumentNumberGenerator {
    private final DigitalDocumentFillCurrentDateTimeProvider digitalDocumentFillCurrentDateTimeProvider;

    public String generate(DigitalDocumentType type, Long digitalDocumentId, Long issuedNumber) {
        return type.generateDocumentNo(
            digitalDocumentId,
            digitalDocumentFillCurrentDateTimeProvider.getKoreanDateTime().format(DateTimeFormatUtil.yyMMdd()),
            issuedNumber
        );
    }
}
