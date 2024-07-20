package ag.act.facade.user;

import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.UserStockResponseConverter;
import ag.act.dto.SimplePageDto;
import ag.act.entity.mydata.JsonMyData;
import ag.act.entity.mydata.JsonMyDataStock;
import ag.act.exception.ActRuntimeException;
import ag.act.exception.InternalServerException;
import ag.act.facade.FileFacade;
import ag.act.model.UpdateMyDataRequest;
import ag.act.module.mydata.MyDataService;
import ag.act.service.JsonMyDataStockService;
import ag.act.service.user.UserHoldingStockOnReferenceDateSyncService;
import ag.act.util.InMemoryPaginator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MyDataFacade {

    private final FileFacade fileFacade;
    private final MyDataService myDataService;
    private final JsonMyDataStockService jsonMyDataStockService;
    private final UserHoldingStockOnReferenceDateSyncService userHoldingStockOnReferenceDateSyncService;
    private final UserStockResponseConverter userStockResponseConverter;
    private final InMemoryPaginator inMemoryPaginator;

    public void updateMyData(UpdateMyDataRequest updateMyDataRequest) {
        final Long userId = ActUserProvider.getNoneNull().getId();

        try {
            userHoldingStockOnReferenceDateSyncService.deleteAllUserHoldingStockOnReferenceDateBeforeSync(userId);
            fileFacade.uploadMyDataJson(updateMyDataRequest.getJsonData(), userId);
            myDataService.updateMyData(updateMyDataRequest.getJsonData());
            userHoldingStockOnReferenceDateSyncService.syncUserHoldingStockOnReferenceDate(
                myDataService.getMyDataSummary(userId),
                userId
            );
        } catch (ActRuntimeException e) {
            log.error("Failed to update MyDataJson. UserID:{}", userId, e);
            throw e;
        } catch (Exception e) {
            log.error("Failed to update MyDataJson. UserID:{}", userId, e);
            throw new InternalServerException("마이데이터를 업데이트하는데 실패했습니다.", e);
        }
    }

    public SimplePageDto<ag.act.model.UserStockResponse> getUserStocks(Long userId, PageRequest pageRequest) {

        final List<JsonMyDataStock> jsonMyDataStocks = jsonMyDataStockService.getJsonMyData(userId)
            .map(JsonMyData::getJsonMyDataStockList)
            .orElse(List.of());

        final Page<JsonMyDataStock> userStockPage = inMemoryPaginator.paginate(jsonMyDataStocks, pageRequest);

        return new SimplePageDto<>(userStockPage.map(userStockResponseConverter));
    }
}
