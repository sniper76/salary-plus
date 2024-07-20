package ag.act.service.digitaldocument.certification;

import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.enums.DigitalDocumentType;
import ag.act.module.digitaldocumentgenerator.DigitalDocumentHtmlGenerator;
import ag.act.module.digitaldocumentgenerator.converter.DigitalProxyCertificationFillConverter;
import ag.act.module.digitaldocumentgenerator.dto.GenerateHtmlCertificationDto;
import ag.act.module.digitaldocumentgenerator.model.DigitalDocumentCertificationFill;
import ag.act.module.digitaldocumentgenerator.openhtmltopdf.PDFRenderService;
import ag.act.module.digitaldocumentgenerator.validator.DigitalProxyCertificationFillValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DigitalProxyCertificationGenerator implements DigitalDocumentCertificationGenerator {
    private final PDFRenderService pdfRenderService;
    private final DigitalDocumentHtmlGenerator digitalDocumentHtmlGenerator;
    private final DigitalProxyCertificationFillConverter digitalDocumentCertificationFillConverter;
    private final DigitalProxyCertificationFillValidator digitalDocumentCertificationFillValidator;

    @Override
    public boolean supports(DigitalDocumentType digitalDocumentType) {
        return DigitalDocumentType.DIGITAL_PROXY == digitalDocumentType;
    }

    @Override
    public byte[] generate(DigitalDocument digitalDocument, DigitalDocumentUser digitalDocumentUser) {
        GenerateHtmlCertificationDto dto = GenerateHtmlCertificationDto.builder()
            .digitalDocument(digitalDocument)
            .digitalDocumentUser(digitalDocumentUser)
            .build();

        digitalDocumentCertificationFillValidator.validate(dto);
        DigitalDocumentCertificationFill fill = digitalDocumentCertificationFillConverter.convert(dto);
        return pdfRenderService.renderPdf(generateHtmlString(fill));
    }

    private String generateHtmlString(DigitalDocumentCertificationFill fill) {
        return digitalDocumentHtmlGenerator.fillAndGetHtmlString(fill, DIGITAL_PROXY_CERTIFICATION_TEMPLATE);
    }
}
