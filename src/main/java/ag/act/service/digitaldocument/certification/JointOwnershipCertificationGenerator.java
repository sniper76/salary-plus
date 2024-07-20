package ag.act.service.digitaldocument.certification;

import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.enums.DigitalDocumentType;
import ag.act.module.digitaldocumentgenerator.DigitalDocumentHtmlGenerator;
import ag.act.module.digitaldocumentgenerator.converter.JointOwnershipCertificationFillConverter;
import ag.act.module.digitaldocumentgenerator.openhtmltopdf.PDFRenderService;
import ag.act.module.digitaldocumentgenerator.validator.JointOwnershipCertificationFillValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Slf4j
@RequiredArgsConstructor
@Component
public class JointOwnershipCertificationGenerator implements DigitalDocumentCertificationGenerator {
    private final PDFRenderService pdfRenderService;
    private final DigitalDocumentHtmlGenerator digitalDocumentHtmlGenerator;
    private final JointOwnershipCertificationFillConverter digitalDocumentCertificationFillConverter;
    private final JointOwnershipCertificationFillValidator digitalDocumentCertificationFillValidator;

    @Override
    public byte[] generate(DigitalDocument digitalDocument, DigitalDocumentUser digitalDocumentUser) {
        log.info("not implemented: JointOwnershipCertificationGenerator");
        return null;
    }

    @Override
    public boolean supports(DigitalDocumentType digitalDocumentType) {
        return DigitalDocumentType.JOINT_OWNERSHIP_DOCUMENT == digitalDocumentType;
    }
}
