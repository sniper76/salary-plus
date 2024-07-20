package ag.act.test.module.digitaldocumentgenerator.freemarker;

import ag.act.core.configuration.freemarker.ICloudTemplateLoaderFactory;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Profile({"test-persistence", "test"})
@Component
public class TestCloudTemplateLoaderFactory implements ICloudTemplateLoaderFactory {
    @Override
    public TemplateLoader create(String baseUrl, String relativePath) throws IOException {
        return new FileTemplateLoader(new ClassPathResource(relativePath).getFile());
    }
}