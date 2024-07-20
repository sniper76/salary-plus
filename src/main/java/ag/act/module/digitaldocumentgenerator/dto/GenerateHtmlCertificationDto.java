package ag.act.module.digitaldocumentgenerator.dto;

import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.entity.digitaldocument.IDigitalDocument;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class GenerateHtmlCertificationDto {
    private IDigitalDocument digitalDocument;
    private DigitalDocumentUser digitalDocumentUser;
}
