package ag.act.external.http;

import org.springframework.stereotype.Component;

import java.net.http.HttpClient;

@Component
public class HttpClientFactory {

    public HttpClient create() {
        return HttpClient.newBuilder().build();
    }

}