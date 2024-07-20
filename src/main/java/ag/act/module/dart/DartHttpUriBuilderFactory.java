package ag.act.module.dart;

import ag.act.external.http.HttpUriBuilderFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;

@Component
public class DartHttpUriBuilderFactory {
    private static final String API_KEY_NAME = "crtfc_key";
    private final String dartApiKey;
    private final HttpUriBuilderFactory httpUriBuilderFactory;

    public DartHttpUriBuilderFactory(
        @Value("${external.dart.api-key}") final String dartApiKey,
        HttpUriBuilderFactory httpUriBuilderFactory
    ) {
        this.dartApiKey = dartApiKey;
        this.httpUriBuilderFactory = httpUriBuilderFactory;
    }

    public URI createUri(String dartBaseUrl, MultiValueMap<String, String> multiValueParameterMap) {
        final LinkedMultiValueMap<String, String> linkedMultiValueParameterMap = new LinkedMultiValueMap<>(multiValueParameterMap);

        linkedMultiValueParameterMap.add(API_KEY_NAME, dartApiKey);

        return httpUriBuilderFactory
            .create(dartBaseUrl)
            .queryParams(linkedMultiValueParameterMap)
            .build();
    }
}
