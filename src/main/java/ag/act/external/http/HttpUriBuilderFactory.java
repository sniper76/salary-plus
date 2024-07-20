package ag.act.external.http;

import org.springframework.stereotype.Component;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class HttpUriBuilderFactory {

    public UriBuilder create(String baseUrl) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl);
    }

}
