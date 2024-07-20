package ag.act.module.digitaldocumentgenerator.validator;

import ag.act.module.digitaldocumentgenerator.dto.GenerateHtmlCertificationDto;

public interface IDigitalDocumentCertificationFillValidator {
    void validate(GenerateHtmlCertificationDto dto);
}
