package ag.act.module.captcha;

import ag.act.external.http.DefaultHttpClientUtil;
import ag.act.util.ObjectMapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.stream.Collectors;

import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

@Slf4j
@Component
public class RecaptchaHttpClientUtil {
    private final String baseUrl;
    private final Map<String, Object> apiHeaders;
    private final Map<String, String> apiParams;
    private final DefaultHttpClientUtil defaultHttpClientUtil;
    private final ObjectMapperUtil objectMapperUtil;

    public RecaptchaHttpClientUtil(
        @Value("${external.google.recaptcha.url}") final String baseUrl,
        @Value("${external.google.recaptcha.secret-key}") final String secretKey,
        DefaultHttpClientUtil defaultHttpClientUtil,
        ObjectMapperUtil objectMapperUtil
    ) {
        this.baseUrl = baseUrl;
        this.defaultHttpClientUtil = defaultHttpClientUtil;
        this.objectMapperUtil = objectMapperUtil;

        apiHeaders = Map.of(HttpHeaders.CONTENT_TYPE, APPLICATION_FORM_URLENCODED_VALUE);
        apiParams = Map.of("secret", secretKey);
    }

    public Map<String, Object> callApi(Map<String, String> queryStringMap) {
        try {
            final URI uri = createUri(baseUrl, queryStringMap);
            final HttpResponse<String> response = defaultHttpClientUtil.post(uri, apiHeaders);
            return objectMapperUtil.readValue(response.body(), Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Recaptcha 연결중 오류가 있습니다.", e);
        }
    }

    public URI createUri(String baseUrl, Map<String, String> queryStringMap) {
        queryStringMap.putAll(apiParams);

        String queryString = queryStringMap.entrySet().stream()
            .map(this::createQueryString)
            .collect(Collectors.joining("&"));

        return URI.create(
            baseUrl + (StringUtils.isBlank(queryString) ? "" : "?" + queryString)
        );
    }

    private String createQueryString(Map.Entry<String, String> entry) {
        return String.format("%s=%s", entry.getKey().trim(), encodeValue(entry.getValue().trim()));
    }

    private String encodeValue(String value) {
        return encode(value, UTF_8);
    }
}
