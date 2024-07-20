package ag.act.module.digitaldocumentgenerator.document;

import ag.act.dto.digitaldocument.PdfDataDto;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.enums.DigitalDocumentType;
import ag.act.module.digitaldocumentgenerator.dto.GenerateDigitalDocumentDto;

interface IDigitalDocumentGenerator {

    boolean supports(DigitalDocumentType digitalDocumentType);

    PdfDataDto generate(GenerateDigitalDocumentDto dto, DigitalDocument digitalDocument, DigitalDocumentUser digitalDocumentUser);

    default boolean isHolderListReadAndCopy(DigitalDocumentType digitalDocumentType) {
        return digitalDocumentType == DigitalDocumentType.HOLDER_LIST_READ_AND_COPY_DOCUMENT;
    }
}
