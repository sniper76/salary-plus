package ag.act.module.mydata;

import ag.act.configuration.security.ActUserProvider;
import ag.act.dto.mydata.IntermediateUserHoldingStockDto;
import ag.act.dto.mydata.MyDataStockInfoDto;
import ag.act.entity.MyDataSummary;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.module.mydata.external.MyDataFinpongAccessTokenRetriever;
import ag.act.module.mydata.external.MyDataPartnerUnregisterer;
import ag.act.module.mydata.load.MyDataLoadService;
import ag.act.service.user.UserHoldingStockService;
import ag.act.service.user.UserService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional
public class MyDataService implements IMyDataService {

    private final UserService userService;
    private final UserHoldingStockService userHoldingStockService;
    private final MyDataLoadService myDataLoadService;
    private final MyDataSummaryService myDataSummaryService;
    private final MyDataFinpongAccessTokenRetriever myDataFinpongAccessTokenRetriever;
    private final MyDataPartnerUnregisterer myDataPartnerUnregisterer;

    @SuppressWarnings("DefaultAnnotationParam")
    @CircuitBreaker(name = "getFinpongAccessToken")
    @Retryable(retryFor = Exception.class, maxAttempts = MY_DATA_MAX_RETRY_COUNT, backoff = @Backoff(delay = MY_DATA_BACKOFF_DELAY_MS))
    public String getFinpongAccessToken(User user, String ci, String phoneNum, String accessToken) {
        return myDataFinpongAccessTokenRetriever.retrieve(user, ci, phoneNum, accessToken);
    }

    @SuppressWarnings("DefaultAnnotationParam")
    @CircuitBreaker(name = "withdrawMyData")
    @Retryable(retryFor = Exception.class, maxAttempts = MY_DATA_MAX_RETRY_COUNT, backoff = @Backoff(delay = MY_DATA_BACKOFF_DELAY_MS))
    public void withdrawMyData(String finpongAccessToken) {
        myDataPartnerUnregisterer.unregister(finpongAccessToken);
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

    public MyDataSummary getMyDataSummary(Long id) {
        return myDataSummaryService.getByUserId(id);
    }
}
