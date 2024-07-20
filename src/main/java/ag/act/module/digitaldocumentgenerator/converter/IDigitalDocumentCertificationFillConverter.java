package ag.act.module.digitaldocumentgenerator.converter;

import ag.act.module.digitaldocumentgenerator.dto.GenerateHtmlCertificationDto;
import ag.act.module.digitaldocumentgenerator.model.DigitalDocumentCertificationFill;

public interface IDigitalDocumentCertificationFillConverter {
    DigitalDocumentCertificationFill convert(GenerateHtmlCertificationDto dto);
}
