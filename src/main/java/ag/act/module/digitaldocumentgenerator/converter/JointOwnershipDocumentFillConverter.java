package ag.act.module.digitaldocumentgenerator.converter;

import ag.act.entity.digitaldocument.IJointOwnershipDocument;
import ag.act.module.digitaldocumentgenerator.dto.IGenerateHtmlDocumentDto;
import ag.act.module.digitaldocumentgenerator.model.DigitalDocumentFill;
import ag.act.module.digitaldocumentgenerator.model.JointOwnershipDocumentFill;
import org.springframework.stereotype.Component;

@Component
public class JointOwnershipDocumentFillConverter extends BaseDigitalDocumentFillConverter {
    private final AcceptorFillConverter acceptorFillConverter;

    public JointOwnershipDocumentFillConverter(AcceptorFillConverter acceptorFillConverter) {
        this.acceptorFillConverter = acceptorFillConverter;
    }

    @Override
    public DigitalDocumentFill convert(IGenerateHtmlDocumentDto generateHtmlDocumentDto) {
        final IJointOwnershipDocument jointOwnershipDocument = (IJointOwnershipDocument) generateHtmlDocumentDto.getDigitalDocument();

        final JointOwnershipDocumentFill jointOwnershipDocumentFill = (JointOwnershipDocumentFill) getBaseDigitalDocumentFill(
            generateHtmlDocumentDto
        );

        jointOwnershipDocumentFill.setCompanyRegistrationNumber(jointOwnershipDocument.getCompanyRegistrationNumber());
        jointOwnershipDocumentFill.setAcceptor(
            acceptorFillConverter.convert(jointOwnershipDocument.getStockCode(), jointOwnershipDocument.getAcceptUserId())
        );
        jointOwnershipDocumentFill.setContent(jointOwnershipDocument.getContent());

        return jointOwnershipDocumentFill;
    }
}
