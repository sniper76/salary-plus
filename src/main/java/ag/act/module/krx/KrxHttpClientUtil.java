package ag.act.module.krx;

import ag.act.enums.KrxServiceType;
import ag.act.exception.ActRuntimeException;
import ag.act.exception.InternalServerException;
import ag.act.external.http.DefaultHttpClientUtil;
import ag.act.external.http.HttpUriBuilderFactory;
import ag.act.util.ObjectMapperUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpResponse;
import java.util.Map;

@Slf4j
@Component
public class KrxHttpClientUtil {
    private static final String COMMON_ERROR_MESSAGE = "현재 한국거래소 서버와 연결이 원활하지 않습니다. 잠시 후 다시 시도해 주세요.";
    private static final String JSON_PROCESSING_ERROR_MESSAGE = "한국거래소 %s 응답을 처리하는 중에 알 수 없는 오류가 발생하였습니다.";
    private static final String QUERY_KEY = "basDd";
    private final String krxBaseUrl;
    private final Long krxTimeoutSecond;
    private final Map<String, String> krxApiHeaders;
    private final DefaultHttpClientUtil defaultHttpClientUtil;
    private final HttpUriBuilderFactory httpUriBuilderFactory;
    private final ObjectMapperUtil objectMapperUtil;

    public KrxHttpClientUtil(
        @Value("${external.krx.baseUrl}") final String krxBaseUrl,
        @Value("${external.krx.authName}") final String krxAuthName,
        @Value("${external.krx.authValue}") final String krxAuthValue,
        @Value("${external.krx.timeoutSecond}") final Long krxTimeoutSecond,
        DefaultHttpClientUtil defaultHttpClientUtil,
        HttpUriBuilderFactory httpUriBuilderFactory,
        ObjectMapperUtil objectMapperUtil
    ) {
        this.krxBaseUrl = krxBaseUrl;
        this.krxTimeoutSecond = krxTimeoutSecond;
        this.defaultHttpClientUtil = defaultHttpClientUtil;
        this.httpUriBuilderFactory = httpUriBuilderFactory;
        this.objectMapperUtil = objectMapperUtil;

        krxApiHeaders = Map.of(krxAuthName, krxAuthValue);
    }

    private URI createUri(String krxBaseUrl, String segment, String queryParam) {
        return httpUriBuilderFactory
            .create(krxBaseUrl)
            .pathSegment(segment)
            .queryParam(QUERY_KEY, queryParam)
            .build();
    }

    private HttpResponse<String> callApi(URI uri, Long delaySecond, Map<String, String> headers) throws Exception {
        return defaultHttpClientUtil.get(uri, delaySecond, headers);
    }

    public <T> T callApi(KrxServiceType krxServiceType, String queryValue, Class<T> responseClass) {
        try {
            final URI uri = createUri(krxBaseUrl, krxServiceType.getServiceType(), queryValue);
            final HttpResponse<String> response = callApi(uri, krxTimeoutSecond, krxApiHeaders);
            if (response.statusCode() == HttpStatus.SC_OK) {
                return objectMapperUtil.toResponse(response, responseClass);
            }
            throw new InternalServerException(response.body());
        } catch (JsonProcessingException ex) {
            log.error("error : {}", ex.getLocalizedMessage(), ex);
            throw new InternalServerException(JSON_PROCESSING_ERROR_MESSAGE.formatted(krxServiceType.getServiceName()), ex);
        } catch (ActRuntimeException ex) {
            log.error("error : {}", ex.getLocalizedMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            log.error("error : {}", ex.getLocalizedMessage(), ex);
            throw new InternalServerException(COMMON_ERROR_MESSAGE, ex);
        }
    }
}
