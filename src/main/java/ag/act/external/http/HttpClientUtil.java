package ag.act.external.http;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpResponse;
import java.util.Map;

public interface HttpClientUtil {

    HttpResponse<String> post(URI requestUri, Map<String, Object> requestPayload) throws Exception;

    HttpResponse<String> post(URI requestUri, String body) throws Exception;

    HttpResponse<String> post(URI requestUri, Long second, String body) throws Exception;

    HttpResponse<String> post(URI requestUri, Map<String, Object> requestPayload, Map<String, String> requestHeaders) throws Exception;

    HttpResponse<String> post(URI requestUri, String body, Map<String, String> requestHeaders) throws Exception;

    HttpResponse<String> post(URI requestUri, Long second, String body, Map<String, String> requestHeaders) throws Exception;

    HttpResponse<InputStream> getInputStream(URI requestUri, Map<String, String> requestHeaders) throws Exception;

    HttpResponse<String> get(URI requestUri, Map<String, String> requestHeaders) throws Exception;

    HttpResponse<String> get(URI requestUri, Long second, Map<String, String> requestHeaders) throws Exception;

    HttpResponse<String> delete(URI requestUri, Map<String, String> requestHeaders) throws Exception;

}
