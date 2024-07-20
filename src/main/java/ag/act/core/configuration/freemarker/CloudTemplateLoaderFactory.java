package ag.act.core.configuration.freemarker;

import freemarker.cache.TemplateLoader;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Profile({"default", "local", "dev", "prod"})
public class CloudTemplateLoaderFactory implements ICloudTemplateLoaderFactory {

    @Override
    public TemplateLoader create(String baseUrl, String relativePath) throws IOException {
        return new CloudTemplateLoader(baseUrl, relativePath);
    }
}