package ag.act.module.digitaldocumentgenerator.freemarker;

import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor
public class ActFreeMarkerConfiguration {
    private final Configuration freeMarkerConfigurer;

    public Template getTemplate(String templateName) throws IOException {
        return freeMarkerConfigurer.getTemplate(templateName);
    }
}
