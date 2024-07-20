package ag.act.module.digitaldocumentgenerator.document.typegenerator;

import ag.act.enums.DigitalDocumentType;
import ag.act.module.digitaldocumentgenerator.converter.OtherDocumentFillConverter;
import ag.act.module.digitaldocumentgenerator.validator.OtherDocumentFillValidator;
import org.springframework.stereotype.Service;

@Service
public class OtherDocumentGenerator extends AbstractDigitalDocumentTypeGenerator {
    OtherDocumentGenerator(
        OtherDocumentFillValidator otherDocumentFillValidator,
        OtherDocumentFillConverter otherDocumentFillConverter
    ) {
        super(
            OTHER_DOCUMENT_TEMPLATE,
            otherDocumentFillValidator,
            otherDocumentFillConverter
        );
    }

    @Override
    public boolean supports(DigitalDocumentType type) {
        return DigitalDocumentType.ETC_DOCUMENT == type;
    }
}
