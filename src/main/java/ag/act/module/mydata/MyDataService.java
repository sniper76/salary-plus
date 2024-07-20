package ag.act.module.mydata;

import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.mydata.MyDataConverter;
import ag.act.dto.mydata.IntermediateUserHoldingStockDto;
import ag.act.dto.mydata.MyDataAuthTokenRequestDto;
import ag.act.dto.mydata.MyDataAuthTokenResponseDto;
import ag.act.dto.mydata.MyDataResultDto;
import ag.act.dto.mydata.MyDataStockInfoDto;
import ag.act.dto.mydata.MyDataWithdrawResponseDto;
import ag.act.entity.MyDataSummary;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.enums.MyDataError;
import ag.act.exception.InternalServerException;
import ag.act.external.http.DefaultHttpClientUtil;
import ag.act.external.http.HttpUriBuilderFactory;
import ag.act.service.user.UserHoldingStockService;
import ag.act.service.user.UserService;
import ag.act.util.ObjectMapperUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional
public class MyDataService {

    private static final String COMMON_ERROR_MESSAGE = "현재 마이데이터 서버와 연결이 원활하지 않습니다. 잠시 후 다시 시도해 주세요.";
    private static final String MY_DATA_SUCCESS_CODE = "FP-00000";
    private static final long TIMEOUT_IN_SECONDS = 5L;
    private final String myDataBaseUrl;
    private final String clientId;
    private final String clientSecret;
    private final MyDataConverter myDataConverter;
    private final DefaultHttpClientUtil defaultHttpClientUtil;
    private final HttpUriBuilderFactory httpUriBuilderFactory;
    private final ObjectMapperUtil objectMapperUtil;
    private final UserService userService;
    private final UserHoldingStockService userHoldingStockService;
    private final MyDataLoadService myDataLoadService;
    private final MyDataSummaryService myDataSummaryService;

    public MyDataService(
        @Value("${external.mydata.baseUrl}") String myDataBaseUrl,
        @Value("${external.mydata.client.id}") String clientId,
        @Value("${external.mydata.client.secret}") String clientSecret,
        MyDataConverter myDataConverter,
        DefaultHttpClientUtil defaultHttpClientUtil,
        HttpUriBuilderFactory httpUriBuilderFactory,
        ObjectMapperUtil objectMapperUtil,
        UserService userService,
        UserHoldingStockService userHoldingStockService,
        MyDataLoadService myDataLoadService,
        MyDataSummaryService myDataSummaryService
    ) {
        this.myDataBaseUrl = myDataBaseUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.myDataConverter = myDataConverter;
        this.defaultHttpClientUtil = defaultHttpClientUtil;
        this.httpUriBuilderFactory = httpUriBuilderFactory;
        this.objectMapperUtil = objectMapperUtil;
        this.userService = userService;
        this.userHoldingStockService = userHoldingStockService;
        this.myDataLoadService = myDataLoadService;
        this.myDataSummaryService = myDataSummaryService;
    }

    public String getFinpongAccessToken(User user, String ci, String phoneNum, String accessToken) throws JsonProcessingException {
        final MyDataAuthTokenRequestDto requestDto = myDataConverter.convertToRequest(user, ci, phoneNum, accessToken, clientId, clientSecret);
        final HttpResponse<String> response = callMyDataServer(objectMapperUtil.toRequestBody(requestDto));
        final MyDataAuthTokenResponseDto responseDto = objectMapperUtil.toResponse(response, MyDataAuthTokenResponseDto.class);

        validate(responseDto.getResult(), "[마이데이터 오류] %s");
        return responseDto.getData().getAccessToken();
    }

    public void withdraw(String finpongAccessToken) throws JsonProcessingException {
        final HttpResponse<String> response = callMyDataServerForWithdraw(finpongAccessToken);
        final MyDataWithdrawResponseDto responseDto = objectMapperUtil.toResponse(response, MyDataWithdrawResponseDto.class);

        validate(responseDto.getResult(), "마이데이터 연동 취소 요청 중에 오류가 발생하였습니다. (%s)");
    }

    public void updateMyData(String jsonContent) {

        final User user = ActUserProvider.getNoneNull();
        final Long userId = user.getId();

        final Map<String, UserHoldingStock> userHoldingStockMap = userService.getActiveUserHoldingStocksMap(userId);
        final MyDataStockInfoDto myDataStockInfoDto = myDataLoadService.getMyDataStocks(jsonContent);
        final List<IntermediateUserHoldingStockDto> myDataStocks = myDataStockInfoDto.getIntermediateUserHoldingStockDtoList();

        final List<UserHoldingStock> listForInsertUpdate
            = myDataLoadService.getUserHoldingStocksForInsertUpdate(userId, myDataStocks, userHoldingStockMap);
        final List<UserHoldingStock> listForDelete = myDataLoadService.getUserHoldingStocksForDelete(userHoldingStockMap);

        final boolean userAlreadyHasStocks = userHoldingStockService.hasAnyUserHoldingStock(userId);

        userHoldingStockService.deleteAll(listForDelete);
        userHoldingStockService.saveAll(listForInsertUpdate);

        if (!userAlreadyHasStocks) {
            userHoldingStockService.setActiveStocksDisplayOrderByPurchasedAmountDesc(user);
        }

        myDataSummaryService.updateMyDataSummary(userId, myDataStockInfoDto);
    }

    private HttpResponse<String> callMyDataServer(String body) {
        try {
            final URI uri = httpUriBuilderFactory.create(myDataBaseUrl + "/auth/plus-token").build();
            return defaultHttpClientUtil.post(uri, TIMEOUT_IN_SECONDS, body);
        } catch (Exception e) {
            throw new InternalServerException(COMMON_ERROR_MESSAGE, e);
        }
    }

    private HttpResponse<String> callMyDataServerForWithdraw(String finpongAccessToken) {
        try {
            final URI uri = httpUriBuilderFactory.create(myDataBaseUrl + "/user/partner-delete").build();
            return defaultHttpClientUtil.delete(uri, Map.of("Authorization", "Bearer " + finpongAccessToken));
        } catch (Exception e) {
            throw new InternalServerException(COMMON_ERROR_MESSAGE, e);
        }
    }

    private void validate(MyDataResultDto responseDto, String errorMessageFormat) {
        if (isSuccess(responseDto.getCode())) {
            return;
        }

        MyDataError mydataError = MyDataError.fromValue(responseDto.getCode());

        log.error("MyDataError: {} :: {} :: {} :: UserID:{}",
            mydataError, responseDto.getCode(), responseDto.getMessage(), ActUserProvider.getNoneNull().getId());

        throw new InternalServerException(errorMessageFormat.formatted(mydataError.getMessage()));
    }

    private boolean isSuccess(String code) {
        return MY_DATA_SUCCESS_CODE.equals(code);
    }

    public MyDataSummary getMyDataSummary(Long id) {
        return myDataSummaryService.findByUserIdNoneNull(id);
    }
}
