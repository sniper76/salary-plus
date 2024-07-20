package ag.act.module.digitaldocumentgenerator.document.typegenerator;

import ag.act.enums.DigitalDocumentType;
import ag.act.module.digitaldocumentgenerator.converter.DigitalProxyFillConverter;
import ag.act.module.digitaldocumentgenerator.validator.DigitalProxyFillValidator;
import org.springframework.stereotype.Service;

@Service
public class DigitalProxyGenerator extends AbstractDigitalDocumentTypeGenerator {
    DigitalProxyGenerator(
        DigitalProxyFillValidator digitalProxyFillValidator,
        DigitalProxyFillConverter digitalProxyFillConverter
    ) {
        super(
            DIGITAL_PROXY_TEMPLATE,
            digitalProxyFillValidator,
            digitalProxyFillConverter
        );
    }

    @Override
    public boolean supports(DigitalDocumentType type) {
        return DigitalDocumentType.DIGITAL_PROXY == type;
    }
}
