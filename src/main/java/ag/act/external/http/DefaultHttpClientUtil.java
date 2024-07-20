package ag.act.external.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

import static java.net.http.HttpResponse.BodyHandlers.ofInputStream;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static java.util.Collections.emptyMap;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
@Slf4j
public class DefaultHttpClientUtil implements HttpClientUtil {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public DefaultHttpClientUtil(ObjectMapper objectMapper, HttpClient httpClient) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public HttpResponse<String> post(URI requestUri, Map<String, Object> requestPayload) throws Exception {
        return post(requestUri, requestPayload, emptyMap());
    }

    @Override
    public HttpResponse<String> post(URI requestUri, String body) throws Exception {
        return post(requestUri, body, emptyMap());
    }

    @Override
    public HttpResponse<String> post(URI requestUri, Long second, String body) throws Exception {
        return post(requestUri, second, body, emptyMap());
    }

    @Override
    public HttpResponse<String> post(URI requestUri, Map<String, Object> requestPayload, Map<String, String> requestHeaders) throws Exception {
        var request = createPostRequest(requestUri, requestPayload, requestHeaders);

        return httpClient.send(request, ofString());
    }

    @Override
    public HttpResponse<String> post(URI requestUri, String body, Map<String, String> requestHeaders) throws Exception {
        var request = createPostRequest(requestUri, body, requestHeaders);

        return httpClient.send(request, ofString());
    }

    @Override
    public HttpResponse<String> post(URI requestUri, Long second, String body, Map<String, String> requestHeaders) throws Exception {
        var request = createPostRequest(requestUri, second, body, requestHeaders);

        return httpClient.send(request, ofString());
    }

    @Override
    public HttpResponse<InputStream> getInputStream(URI requestUri, Map<String, String> requestHeaders) throws Exception {
        var request = createGetRequest(requestUri, 0L, requestHeaders);

        return httpClient.send(request, ofInputStream());
    }

    @Override
    public HttpResponse<String> get(URI requestUri, Map<String, String> requestHeaders) throws Exception {
        var request = createGetRequest(requestUri, 0L, requestHeaders);

        return httpClient.send(request, ofString());
    }

    @Override
    public HttpResponse<String> get(URI requestUri, Long second, Map<String, String> requestHeaders) throws Exception {
        var request = createGetRequest(requestUri, second, requestHeaders);

        return httpClient.send(request, ofString());
    }

    @Override
    public HttpResponse<String> delete(URI requestUri, Map<String, String> requestHeaders) throws Exception {
        var builder = createRequestBuilder(requestUri, requestHeaders);

        return httpClient.send(builder.DELETE().build(), ofString());
    }

    private HttpRequest createPostRequest(URI requestUri, Map<String, Object> requestPayload, Map<String, String> requestHeaders)
        throws Exception {
        var builder = createRequestBuilder(requestUri, requestHeaders);

        return builder.POST(BodyPublishers.ofString(serialiseRequestPayload(requestPayload))).build();
    }

    private HttpRequest createPostRequest(URI requestUri, String body, Map<String, String> requestHeaders) {
        var builder = createRequestBuilder(requestUri, requestHeaders);

        return builder.POST(BodyPublishers.ofString(body)).build();
    }

    private HttpRequest createPostRequest(URI requestUri, Long second, String body, Map<String, String> requestHeaders) {
        var builder = createRequestBuilder(requestUri, requestHeaders);
        if (second > 0L) {
            builder = builder.timeout(Duration.ofSeconds(second));
        }

        return builder.POST(BodyPublishers.ofString(body)).build();
    }

    private HttpRequest createGetRequest(URI requestUri, Long second, Map<String, String> requestHeaders) {
        var builder = createRequestBuilder(requestUri, requestHeaders);
        if (second > 0L) {
            builder = builder.timeout(Duration.ofSeconds(second));
        }

        return builder.GET().build();
    }

    private Builder createRequestBuilder(URI requestUri, Map<String, String> requestHeaders) {
        var builder = HttpRequest.newBuilder();
        builder.header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE);
        builder.header(HttpHeaders.ACCEPT, APPLICATION_JSON_VALUE);
        requestHeaders.forEach(builder::header);

        builder.uri(requestUri);

        return builder;
    }

    private String serialiseRequestPayload(Map<String, Object> requestPayload) throws Exception {
        return objectMapper.writeValueAsString(requestPayload);
    }

}
