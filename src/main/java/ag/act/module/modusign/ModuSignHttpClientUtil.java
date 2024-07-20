package ag.act.module.modusign;

import ag.act.exception.InternalServerException;
import ag.act.external.http.DefaultHttpClientUtil;
import ag.act.util.ObjectMapperUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpResponse;
import java.util.Map;

@Slf4j
@Component
public class ModuSignHttpClientUtil {
    private final Map<String, String> apiHeaders;
    private final DefaultHttpClientUtil defaultHttpClientUtil;
    private final ObjectMapperUtil objectMapperUtil;
    private final String baseUrl;

    public ModuSignHttpClientUtil(
        @Value("${external.modusign.url}") String baseUrl,
        @Value("${external.modusign.token}") String token,
        DefaultHttpClientUtil defaultHttpClientUtil,
        ObjectMapperUtil objectMapperUtil
    ) {
        this.baseUrl = baseUrl;
        this.defaultHttpClientUtil = defaultHttpClientUtil;
        this.objectMapperUtil = objectMapperUtil;

        apiHeaders = Map.of("Authorization", "Basic " + token);
    }

    public ModuSignEmbeddedUrlResponse get(String url) {
        return callApi(
            ModuSignApiRequest.<ModuSignEmbeddedUrlResponse>builder()
                .isGetType(true)
                .responseType(ModuSignEmbeddedUrlResponse.class)
                .url(url)
                .build()
        );
    }

    public ModuSignDocument post(String url, Map<String, Object> request) {
        return callApi(
            ModuSignApiRequest.<ModuSignDocument>builder()
                .isGetType(false)
                .responseType(ModuSignDocument.class)
                .request(request)
                .url(url)
                .build()
        );
    }

    public <T> T callApi(ModuSignApiRequest<T> request) {
        try {
            final URI uri = URI.create(baseUrl + request.getUrl());
            final HttpResponse<String> response = request.isGetType()
                ? defaultHttpClientUtil.get(uri, apiHeaders)
                : defaultHttpClientUtil.post(uri, request.getRequest(), apiHeaders);
            return objectMapperUtil.toResponse(response, request.getResponseType());
        } catch (JsonProcessingException ex) {
            log.error("error occurred while parsing response : {}", ex.getLocalizedMessage(), ex);
            throw new InternalServerException("모두싸인 응답을 처리하는 중에 알 수 없는 오류가 발생하였습니다.", ex);
        } catch (Exception ex) {
            log.error("error occurred while calling modusign api : {}", ex.getLocalizedMessage(), ex);
            throw new InternalServerException("현재 모두싸인 서버와 연결이 원활하지 않습니다. 잠시 후 다시 시도해 주세요.", ex);
        }
    }
}
