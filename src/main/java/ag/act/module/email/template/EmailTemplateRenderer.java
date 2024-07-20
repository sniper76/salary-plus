package ag.act.module.email.template;

import ag.act.exception.ActEmailException;
import ag.act.module.digitaldocumentgenerator.freemarker.ActFreeMarkerConfiguration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailTemplateRenderer {

    private final ActFreeMarkerConfiguration defaultFileBasedFreeMarkerConfiguration;

    public String render(String templateName, Map<String, Object> templateData) {
        try {
            Template template = defaultFileBasedFreeMarkerConfiguration.getTemplate(templateName);
            StringWriter writer = new StringWriter();
            template.process(templateData, writer);

            return writer.toString();

        } catch (IOException | TemplateException e) {
            throw new ActEmailException("Fail to render email body with freemarker", e);
        }
    }
}
