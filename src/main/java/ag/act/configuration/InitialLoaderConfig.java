package ag.act.configuration;

import ag.act.configuration.initial.InitialLoader;
import ag.act.core.annotation.AnnotationOrderSorter;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class InitialLoaderConfig {

    private final List<InitialLoader> initialLoaders;

    @Autowired
    public InitialLoaderConfig(AnnotationOrderSorter annotationOrderSorter, List<InitialLoader> initialLoaders) {
        this.initialLoaders = annotationOrderSorter.sort(initialLoaders);
    }

    @PostConstruct
    public void loadData() {
        for (InitialLoader initialLoader : initialLoaders) {
            initialLoader.load();
        }
    }
}
