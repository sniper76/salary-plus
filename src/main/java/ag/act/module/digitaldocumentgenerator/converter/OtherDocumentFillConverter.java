package ag.act.module.digitaldocumentgenerator.converter;

import ag.act.entity.digitaldocument.IOtherDocument;
import ag.act.module.digitaldocumentgenerator.dto.IGenerateHtmlDocumentDto;
import ag.act.module.digitaldocumentgenerator.model.DigitalDocumentFill;
import ag.act.module.digitaldocumentgenerator.model.OtherDocumentFill;
import org.springframework.stereotype.Component;

@Component
public class OtherDocumentFillConverter extends BaseDigitalDocumentFillConverter {
    @Override
    public DigitalDocumentFill convert(IGenerateHtmlDocumentDto generateHtmlDocumentDto) {
        final IOtherDocument otherDocument = (IOtherDocument) generateHtmlDocumentDto.getDigitalDocument();

        final OtherDocumentFill otherDocumentFill = (OtherDocumentFill) getBaseDigitalDocumentFill(
            generateHtmlDocumentDto
        );

        otherDocumentFill.setTitle(otherDocument.getTitle());
        otherDocumentFill.setContent(otherDocument.getContent());

        return otherDocumentFill;
    }
}
