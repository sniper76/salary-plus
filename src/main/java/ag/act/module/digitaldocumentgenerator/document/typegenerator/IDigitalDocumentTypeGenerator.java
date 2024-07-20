package ag.act.module.digitaldocumentgenerator.document.typegenerator;

import ag.act.dto.digitaldocument.PdfDataDto;
import ag.act.enums.DigitalDocumentType;
import ag.act.module.digitaldocumentgenerator.dto.IGenerateHtmlDocumentDto;

public interface IDigitalDocumentTypeGenerator {
    boolean supports(DigitalDocumentType type);

    PdfDataDto generateDigitalDocumentPdf(IGenerateHtmlDocumentDto dto);
}
