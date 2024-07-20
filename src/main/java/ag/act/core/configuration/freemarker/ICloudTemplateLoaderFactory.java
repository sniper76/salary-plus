package ag.act.core.configuration.freemarker;

import freemarker.cache.TemplateLoader;

import java.io.IOException;

public interface ICloudTemplateLoaderFactory {

    TemplateLoader create(String baseUrl, String relativePath) throws IOException;
}
