package ag.act.service.digitaldocument.certification;

import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.enums.DigitalDocumentType;
import ag.act.module.digitaldocumentgenerator.DigitalDocumentHtmlGenerator;
import ag.act.module.digitaldocumentgenerator.converter.OtherDocumentCertificationFillConverter;
import ag.act.module.digitaldocumentgenerator.openhtmltopdf.PDFRenderService;
import ag.act.module.digitaldocumentgenerator.validator.OtherDocumentCertificationFillValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Slf4j
@RequiredArgsConstructor
@Component
public class OtherDocumentCertificationGenerator implements DigitalDocumentCertificationGenerator {
    private final PDFRenderService pdfRenderService;
    private final DigitalDocumentHtmlGenerator digitalDocumentHtmlGenerator;
    private final OtherDocumentCertificationFillConverter digitalDocumentCertificationFillConverter;
    private final OtherDocumentCertificationFillValidator digitalDocumentCertificationFillValidator;

    @Override
    public byte[] generate(DigitalDocument digitalDocument, DigitalDocumentUser digitalDocumentUser) {
        log.info("not implemented: OtherDocumentCertificationGenerator");
        return null;
    }

    @Override
    public boolean supports(DigitalDocumentType digitalDocumentType) {
        return DigitalDocumentType.ETC_DOCUMENT == digitalDocumentType;
    }
}
