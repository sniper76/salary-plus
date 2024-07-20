package ag.act.core.configuration.freemarker;

import freemarker.cache.URLTemplateLoader;

import java.net.MalformedURLException;
import java.net.URL;

public class CloudTemplateLoader extends URLTemplateLoader {
    private final URL baseUrl;
    private final String relativePath;

    public CloudTemplateLoader(
        String baseUrl,
        String relativePath
    ) throws MalformedURLException {
        super();
        this.baseUrl = new URL(baseUrl);
        this.relativePath = relativePath;
    }

    @Override
    protected URL getURL(String template) {
        try {
            return new URL(baseUrl, "/" + relativePath + "/" + template);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
