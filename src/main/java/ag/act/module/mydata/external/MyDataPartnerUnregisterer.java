package ag.act.module.mydata.external;

import ag.act.dto.mydata.MyDataWithdrawResponseDto;
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
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class MyDataPartnerUnregisterer implements IMyDataService {
    private final MyDataConfig myDataConfig;
    private final ObjectMapperUtil objectMapperUtil;
    private final DefaultHttpClientUtil defaultHttpClientUtil;
    private final HttpUriBuilderFactory httpUriBuilderFactory;
    private final MyDataResultValidator myDataResultValidator;

    public void unregister(String finpongAccessToken) {
        final MyDataWithdrawResponseDto responseDto = unregisterMyDataUser(finpongAccessToken);

        myDataResultValidator.validate(responseDto.getResult(), "마이데이터 연동 취소 요청 중에 오류가 발생하였습니다. (%s)");
    }

    private MyDataWithdrawResponseDto unregisterMyDataUser(String finpongAccessToken) {
        try {
            final HttpResponse<String> response = callMyDataServer(finpongAccessToken);
            return objectMapperUtil.toResponse(response, MyDataWithdrawResponseDto.class);
        } catch (ActRuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException(UNKNOWN_WITHDRAW_ERROR_MESSAGE, e);
        }
    }

    private HttpResponse<String> callMyDataServer(String finpongAccessToken) {
        try {
            final URI uri = httpUriBuilderFactory.create(getMyDataServerApiUrl()).build();
            return defaultHttpClientUtil.delete(uri, Map.of("Authorization", "Bearer " + finpongAccessToken));
        } catch (Exception e) {
            throw new InternalServerException(COMMON_ERROR_MESSAGE, e);
        }
    }

    private String getMyDataServerApiUrl() {
        return myDataConfig.getBaseUrl() + "/user/partner-delete";
    }
}
