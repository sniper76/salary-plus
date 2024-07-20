package ag.act.service.digitaldocument.certification;

import ag.act.constants.DigitalDocumentTemplateNames;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.enums.DigitalDocumentType;

public interface DigitalDocumentCertificationGenerator extends DigitalDocumentTemplateNames {

    boolean supports(DigitalDocumentType digitalDocumentType);

    byte[] generate(DigitalDocument digitalDocument, DigitalDocumentUser digitalDocumentUser);
}
