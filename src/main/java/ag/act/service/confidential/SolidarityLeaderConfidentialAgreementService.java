package ag.act.service.confidential;

import ag.act.dto.user.SolidarityLeaderConfidentialAgreementDataModel;
import ag.act.entity.User;
import ag.act.exception.InternalServerException;
import ag.act.facade.FileFacade;
import ag.act.module.digitaldocumentgenerator.freemarker.ActFreeMarkerConfiguration;
import ag.act.module.digitaldocumentgenerator.openhtmltopdf.PDFRenderService;
import ag.act.module.markany.dna.MarkAnyConfidentialAgreement;
import ag.act.module.markany.dna.MarkAnyService;
import ag.act.service.user.UserService;
import ag.act.util.XHTMLFormatUtil;
import freemarker.template.Template;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.StringWriter;

@Service
@RequiredArgsConstructor
@Transactional
public class SolidarityLeaderConfidentialAgreementService {
    private static final String DOCUMENT_TEMPLATE_NAME = "solidarity-leader-confidential-agreement-v1.ftlh";

    private final ActFreeMarkerConfiguration solidarityLeaderConfidentialAgreementFreeMarkerConfiguration;
    private final FileFacade fileFacade;
    private final UserService userService;
    private final PDFRenderService pdfRenderService;
    private final MarkAnyService markAnyService;

    public String getSolidarityLeaderConfidentialAgreementDocumentForm(User user) {
        return fillAndGetHtmlBodyString(new SolidarityLeaderConfidentialAgreementDataModel(user));
    }

    public void createSolidarityLeaderConfidentialAgreementDocument(User user) {
        final String htmlString = fillAndGetHtmlString(new SolidarityLeaderConfidentialAgreementDataModel(user));
        final byte[] renderPdf = pdfRenderService.renderPdf(htmlString);

        final byte[] markAnyDnaPdfBytes = markAnyService.makeDna(
            new MarkAnyConfidentialAgreement(
                user.getId(),
                renderPdf
            )
        );

        fileFacade.uploadSolidarityLeaderConfidentialAgreement(
            markAnyDnaPdfBytes,
            user
        );
        user.setIsSolidarityLeaderConfidentialAgreementSigned(Boolean.TRUE);
        userService.saveUser(user);
    }

    private String fillAndGetHtmlBodyString(Object dataModel) {
        final StringWriter out = fillTemplate(dataModel, getTemplate());

        return XHTMLFormatUtil.getHtmlById(out.toString());
    }

    private String fillAndGetHtmlString(Object dataModel) {
        final StringWriter out = fillTemplate(dataModel, getTemplate());

        return XHTMLFormatUtil.convertHtmlToXhtml(out.toString());
    }

    private StringWriter fillTemplate(Object dataModel, Template template) {
        final StringWriter out = new StringWriter();
        try {
            template.process(dataModel, out);
        } catch (Exception e) {
            throw new InternalServerException("Failed to fill template", e);
        }
        return out;
    }

    private Template getTemplate() {
        try {
            return solidarityLeaderConfidentialAgreementFreeMarkerConfiguration.getTemplate(DOCUMENT_TEMPLATE_NAME);
        } catch (Exception e) {
            throw new InternalServerException(String.format("Failed to get template name %s", DOCUMENT_TEMPLATE_NAME), e);
        }
    }
}
