package ag.act.module.dart;

import ag.act.exception.ActRuntimeException;
import ag.act.exception.InternalServerException;
import ag.act.external.http.DefaultHttpClientUtil;
import ag.act.module.dart.dto.DartCompany;
import ag.act.util.ObjectMapperUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;

import java.io.InputStream;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.Map;

@Slf4j
@Component
public class DartHttpClientUtil {
    private static final String COMMON_ERROR_MESSAGE = "현재 DART 서버와 연결이 원활하지 않습니다. 잠시 후 다시 시도해 주세요.";
    private static final String JSON_PROCESSING_ERROR_MESSAGE = "DART 서버의 응답을 처리하는 중에 알 수 없는 오류가 발생하였습니다.";
    private static final String COMPANY_API_CORP_CODE_KEY = "corp_code";
    private final String corpApiUrl;
    private final String companyApiUrl;
    private final DefaultHttpClientUtil defaultHttpClientUtil;
    private final DartHttpUriBuilderFactory dartHttpUriBuilderFactory;
    private final ObjectMapperUtil objectMapperUtil;

    public DartHttpClientUtil(
        @Value("${external.dart.corp-code-api-url}") final String corpCodeApiUrl,
        @Value("${external.dart.company-api-url}") final String companyApiUrl,
        DefaultHttpClientUtil defaultHttpClientUtil,
        DartHttpUriBuilderFactory dartHttpUriBuilderFactory,
        ObjectMapperUtil objectMapperUtil
    ) {
        this.corpApiUrl = corpCodeApiUrl;
        this.companyApiUrl = companyApiUrl;
        this.defaultHttpClientUtil = defaultHttpClientUtil;
        this.dartHttpUriBuilderFactory = dartHttpUriBuilderFactory;
        this.objectMapperUtil = objectMapperUtil;
    }

    public HttpResponse<InputStream> getCorpCodeZip() {
        try {
            return defaultHttpClientUtil.getInputStream(
                dartHttpUriBuilderFactory.createUri(corpApiUrl, new LinkedMultiValueMap<>()),
                Map.of(HttpHeaders.ACCEPT, MediaType.ALL_VALUE)
            );
        } catch (JsonProcessingException ex) {
            log.error("error : {}", ex.getLocalizedMessage(), ex);
            throw new InternalServerException(JSON_PROCESSING_ERROR_MESSAGE, ex);
        } catch (ActRuntimeException ex) {
            log.error("error : {}", ex.getLocalizedMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            log.error("error : {}", ex.getLocalizedMessage(), ex);
            throw new InternalServerException(COMMON_ERROR_MESSAGE, ex);
        }
    }

    public DartCompany getStockDartCompany(String corpCode) {
        try {

            final HttpResponse<String> response = defaultHttpClientUtil.get(
                dartHttpUriBuilderFactory.createUri(companyApiUrl, getLinkedMultiValueMap(corpCode)),
                Collections.emptyMap()
            );

            if (response.statusCode() != HttpStatus.SC_OK) {
                throw new InternalServerException(response.body());
            }

            final DartCompany dartCompany = objectMapperUtil.toResponse(response, DartCompany.class);
            if (dartCompany.isSuccess()) {
                return dartCompany;
            }

            throw new InternalServerException(dartCompany.getErrorMessage());

        } catch (JsonProcessingException ex) {
            log.error("error : {}", ex.getLocalizedMessage(), ex);
            throw new InternalServerException(JSON_PROCESSING_ERROR_MESSAGE, ex);
        } catch (ActRuntimeException ex) {
            log.error("error : {}", ex.getLocalizedMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            log.error("error : {}", ex.getLocalizedMessage(), ex);
            throw new InternalServerException(COMMON_ERROR_MESSAGE, ex);
        }
    }

    @NotNull
    private LinkedMultiValueMap<String, String> getLinkedMultiValueMap(String corpCode) {
        final LinkedMultiValueMap<String, String> objectObjectLinkedMultiValueMap = new LinkedMultiValueMap<>();
        objectObjectLinkedMultiValueMap.add(COMPANY_API_CORP_CODE_KEY, corpCode);
        return objectObjectLinkedMultiValueMap;
    }
}
