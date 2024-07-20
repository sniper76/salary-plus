package ag.act.module.digitaldocumentgenerator.document.typegenerator;

import ag.act.enums.DigitalDocumentType;
import ag.act.module.digitaldocumentgenerator.converter.JointOwnershipDocumentFillConverter;
import ag.act.module.digitaldocumentgenerator.validator.JointOwnershipDocumentFillValidator;
import org.springframework.stereotype.Service;

@Service
public class JointOwnershipDocumentGenerator extends AbstractDigitalDocumentTypeGenerator {
    JointOwnershipDocumentGenerator(
        JointOwnershipDocumentFillValidator jointOwnershipDocumentFillValidator,
        JointOwnershipDocumentFillConverter jointOwnershipDocumentFillConverter
    ) {
        super(
            JOINT_OWNERSHIP_DOCUMENT_TEMPLATE,
            jointOwnershipDocumentFillValidator,
            jointOwnershipDocumentFillConverter
        );
    }

    @Override
    public boolean supports(DigitalDocumentType type) {
        return DigitalDocumentType.JOINT_OWNERSHIP_DOCUMENT == type;
    }
}
