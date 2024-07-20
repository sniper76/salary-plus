package ag.act.module.digitaldocumentgenerator;

import ag.act.exception.InternalServerException;
import ag.act.module.digitaldocumentgenerator.freemarker.ActFreeMarkerConfiguration;
import freemarker.template.Template;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.StringWriter;

@Service
@RequiredArgsConstructor
public class DigitalDocumentHtmlGenerator {

    private final ActFreeMarkerConfiguration digitalDocumentFreeMarkerConfiguration;

    public String fillAndGetHtmlString(Object dataModel, String templateName) {
        final Template template = getTemplate(templateName);

        final StringWriter out = new StringWriter();
        fillTemplate(dataModel, template, out);

        StringBuffer stringBuffer = out.getBuffer();
        return stringBuffer.toString();
    }

    private void fillTemplate(Object dataModel, Template template, StringWriter out) {
        try {
            template.process(dataModel, out);
        } catch (Exception e) {
            throw new InternalServerException("Failed to fill template", e);
        }
    }

    private Template getTemplate(String templateName) {
        try {
            return digitalDocumentFreeMarkerConfiguration.getTemplate(templateName);
        } catch (Exception e) {
            throw new InternalServerException(String.format("Failed to get digital document template name %s", templateName), e);
        }
    }

}
