package ag.act.core.configuration.freemarker;

import ag.act.core.infra.S3Environment;
import ag.act.module.digitaldocumentgenerator.freemarker.ActFreeMarkerConfiguration;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.TemplateExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Locale;
import java.util.TimeZone;

@Configuration
public class FreeMarkerConfiguration {

    @Bean
    public ActFreeMarkerConfiguration digitalDocumentFreeMarkerConfiguration(
        S3Environment s3Environment,
        @Value("${act.free-marker-templates.digital-document.relative-path}") String relativePath,
        ICloudTemplateLoaderFactory cloudTemplateLoaderFactory
    ) throws IOException {
        final TemplateLoader templateLoader = cloudTemplateLoaderFactory.create(
            s3Environment.getBaseUrl(),
            relativePath
        );
        return getActFreeMarkerConfiguration(templateLoader);
    }

    @Bean
    public ActFreeMarkerConfiguration solidarityLeaderConfidentialAgreementFreeMarkerConfiguration(
        S3Environment s3Environment,
        @Value("${act.free-marker-templates.solidarity-leader-confidential-agreement.relative-path}") String relativePath,
        ICloudTemplateLoaderFactory cloudTemplateLoaderFactory
    ) throws IOException {
        final TemplateLoader templateLoader = cloudTemplateLoaderFactory.create(
            s3Environment.getBaseUrl(),
            relativePath
        );
        return getActFreeMarkerConfiguration(templateLoader);
    }

    @Bean
    public ActFreeMarkerConfiguration defaultFileBasedFreeMarkerConfiguration() {
        final TemplateLoader templateLoader = new ClassTemplateLoader(getClass(), "/templates");
        return getActFreeMarkerConfiguration(templateLoader);
    }

    private ActFreeMarkerConfiguration getActFreeMarkerConfiguration(
        TemplateLoader templateLoader
    ) {
        freemarker.template.Configuration cfg = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_32);
        cfg.setTemplateLoader(templateLoader);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        cfg.setFallbackOnNullLoopVariable(false);
        cfg.setSQLDateAndTimeTimeZone(TimeZone.getDefault());
        cfg.setLocale(Locale.KOREA);

        return new ActFreeMarkerConfiguration(cfg);
    }
}
