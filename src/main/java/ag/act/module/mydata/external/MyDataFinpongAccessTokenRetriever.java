package ag.act.module.mydata.external;

import ag.act.converter.mydata.MyDataConverter;
import ag.act.dto.mydata.MyDataAuthTokenRequestDto;
import ag.act.dto.mydata.MyDataAuthTokenResponseDto;
import ag.act.entity.User;
import ag.act.exception.ActRuntimeException;
import ag.act.exception.InternalServerException;
import ag.act.external.http.DefaultHttpClientUtil;
import ag.act.external.http.HttpUriBuilderFactory;
import ag.act.module.mydata.IMyDataService;
import ag.act.module.mydata.MyDataConfig;
import ag.act.util.ObjectMapperUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class MyDataFinpongAccessTokenRetriever implements IMyDataService {
    private final MyDataConfig myDataConfig;
    private final MyDataConverter myDataConverter;
    private final DefaultHttpClientUtil defaultHttpClientUtil;
    private final HttpUriBuilderFactory httpUriBuilderFactory;
    private final ObjectMapperUtil objectMapperUtil;
    private final MyDataResultValidator myDataResultValidator;

    public String retrieve(User user, String ci, String phoneNum, String accessToken) {

        final String requestBody = toFinpongAccessTokenRequestBody(user, ci, phoneNum, accessToken);
        final MyDataAuthTokenResponseDto responseDto = getMyDataFinpongAccessToken(requestBody);

        myDataResultValidator.validate(responseDto.getResult(), "[마이데이터 오류] %s");

        return responseDto.getData().getAccessToken();
    }

    private String toFinpongAccessTokenRequestBody(User user, String ci, String phoneNum, String accessToken) {
        final MyDataAuthTokenRequestDto requestDto = toRequestDto(user, ci, phoneNum, accessToken);

        return objectMapperUtil.toRequestBody(requestDto);
    }

    private MyDataAuthTokenRequestDto toRequestDto(User user, String ci, String phoneNum, String accessToken) {
        return myDataConverter.convertToRequest(
            user,
            ci,
            phoneNum,
            accessToken,
            myDataConfig.getClientId(),
            myDataConfig.getClientSecret()
        );
    }

    private MyDataAuthTokenResponseDto getMyDataFinpongAccessToken(String body) {
        try {
            final HttpResponse<String> response = callMyDataServer(body);
            return objectMapperUtil.toResponse(response, MyDataAuthTokenResponseDto.class);
        } catch (ActRuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException(UNKNOWN_GET_FINPONG_ACCESS_TOKEN_ERROR_MESSAGE, e);
        }
    }

    private HttpResponse<String> callMyDataServer(String body) {
        try {
            final URI uri = httpUriBuilderFactory.create(getMyDataServerApiUrl()).build();
            return defaultHttpClientUtil.post(uri, TIMEOUT_IN_SECONDS, body);
        } catch (Exception e) {
            throw new InternalServerException(COMMON_ERROR_MESSAGE, e);
        }
    }

    private String getMyDataServerApiUrl() {
        return myDataConfig.getBaseUrl() + "/auth/plus-token";
    }
}
