package ag.act.handler;

import ag.act.api.BatchApiDelegate;
import ag.act.core.guard.BatchApiKeyGuard;
import ag.act.core.guard.UseGuards;
import ag.act.dto.BatchParameter;
import ag.act.enums.download.matrix.UserRetentionWeeklyCsvDataType;
import ag.act.exception.ActRuntimeException;
import ag.act.facade.batch.BatchFacade;
import ag.act.facade.batch.IBatch;
import ag.act.model.BatchRequest;
import ag.act.model.SimpleStringResponse;
import ag.act.util.SimpleStringResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.function.Function;

@SuppressWarnings("checkstyle:ParameterName")
@Slf4j
@Service
@RequiredArgsConstructor
@UseGuards({BatchApiKeyGuard.class})
public class BatchApiDelegateImpl implements BatchApiDelegate {

    private final BatchFacade batchFacade;

    @Override
    public ResponseEntity<SimpleStringResponse> processDashboardStatistics(String xApiKey, BatchRequest batchRequest) {
        return processBatch(
            IBatch.BatchName.DASHBOARD_STATISTICS,
            IBatch.BatchGroupName.DASHBOARD_STATISTICS,
            batchRequest
        );
    }

    @Override
    public ResponseEntity<SimpleStringResponse> createDigitalDocumentZipFiles(String xApiKey, BatchRequest batchRequest) {
        return processBatch(
            IBatch.BatchName.DIGITAL_DOCUMENT_ZIP_FILE,
            IBatch.BatchGroupName.DIGITAL_DOCUMENT_ZIP_FILE,
            batchRequest
        );
    }

    @Override
    public ResponseEntity<SimpleStringResponse> deleteOldDigitalDocuments(String xApiKey, BatchRequest batchRequest) {
        return processBatch(
            IBatch.BatchName.DELETE_OLD_DIGITAL_DOCUMENT,
            IBatch.BatchGroupName.DELETE_OLD_DIGITAL_DOCUMENT,
            batchRequest
        );
    }

    @Override
    public ResponseEntity<SimpleStringResponse> updateStocks(String xApiKey, BatchRequest batchRequest) {
        return processBatch(
            IBatch.BatchName.UPDATE_STOCKS,
            IBatch.BatchGroupName.UPDATE_STOCKS,
            batchRequest
        );
    }

    @Override
    public ResponseEntity<SimpleStringResponse> createSolidarityDailyStatistics(String xApiKey, BatchRequest batchRequest) {
        return processBatch(
            IBatch.BatchName.CREATE_SOLIDARITY_DAILY_STATISTICS,
            IBatch.BatchGroupName.SOLIDARITY_DAILY_STATISTICS,
            batchRequest
        );
    }

    @Override
    public ResponseEntity<SimpleStringResponse> createSolidarityDailySummaries(String xApiKey, BatchRequest batchRequest) {
        return processBatch(
            IBatch.BatchName.CREATE_SOLIDARITY_DAILY_SUMMARIES,
            IBatch.BatchGroupName.SOLIDARITY_DAILY_SUMMARIES,
            batchRequest
        );
    }

    @Override
    public ResponseEntity<SimpleStringResponse> updateSolidarityDailySummaries(String xApiKey, BatchRequest batchRequest) {
        return processBatch(
            IBatch.BatchName.UPDATE_SOLIDARITY_DAILY_SUMMARIES,
            IBatch.BatchGroupName.SOLIDARITY_DAILY_SUMMARIES,
            batchRequest
        );
    }

    @Override
    public ResponseEntity<SimpleStringResponse> updateWithdrawalRequestUsers(String xApiKey, BatchRequest batchRequest) {
        return processBatch(
            IBatch.BatchName.UPDATE_WITHDRAWAL_REQUEST_USERS,
            IBatch.BatchGroupName.UPDATE_WITHDRAWAL_REQUEST_USERS,
            batchRequest
        );
    }

    @Override
    public ResponseEntity<SimpleStringResponse> sendPushes(String xApiKey, BatchRequest batchRequest) {
        return processBatch(
            IBatch.BatchName.SEND_PUSHES,
            IBatch.BatchGroupName.SEND_PUSHES,
            batchRequest
        );
    }

    @Override
    public ResponseEntity<SimpleStringResponse> createStockRanking(String xApiKey, BatchRequest batchRequest) {
        return processBatch(
            IBatch.BatchName.STOCK_RANKINGS,
            IBatch.BatchGroupName.STOCK_RANKINGS,
            batchRequest
        );
    }

    @Override
    public ResponseEntity<SimpleStringResponse> createUserHoldingStocksHistories(String xApiKey, BatchRequest batchRequest) {
        return processBatch(
            IBatch.BatchName.USER_HOLDING_STOCK_HISTORIES,
            IBatch.BatchGroupName.USER_HOLDING_STOCK_HISTORIES,
            batchRequest
        );
    }

    @Override
    public ResponseEntity<SimpleStringResponse> cleanupUnfinishedDigitalDocumentUsers(String xApiKey, BatchRequest batchRequest) {
        return processBatch(
            IBatch.BatchName.CLEANUP_UNFINISHED_DIGITAL_DOCUMENT_USERS,
            IBatch.BatchGroupName.DIGITAL_DOCUMENT_USERS,
            batchRequest
        );
    }

    @Override
    public ResponseEntity<SimpleStringResponse> updateTestStocks(String xApiKey, BatchRequest batchRequest) {
        return processBatch(
            IBatch.BatchName.UPDATE_TEST_STOCKS,
            IBatch.BatchGroupName.UPDATE_TEST_STOCKS,
            batchRequest
        );
    }

    @Override
    public ResponseEntity<SimpleStringResponse> cleanupUnfinishedBatches(String xApiKey, BatchRequest batchRequest) {
        return processBatch(
            IBatch.BatchName.CLEANUP_UNFINISHED_BATCHES,
            IBatch.BatchName.CLEANUP_UNFINISHED_BATCHES + "_" + UUID.randomUUID(),
            batchRequest
        );
    }

    @Override
    public ResponseEntity<SimpleStringResponse> updateCorpCodes(String xApiKey, BatchRequest batchRequest) {
        return processBatch(
            IBatch.BatchName.UPDATE_DART_CORP_CODE,
            IBatch.BatchGroupName.UPDATE_DART,
            batchRequest
        );
    }

    @Override
    public ResponseEntity<SimpleStringResponse> updateStockDartCorporations(String xApiKey, BatchRequest batchRequest) {
        return processBatch(
            IBatch.BatchName.UPDATE_DART_CORPORATIONS,
            IBatch.BatchGroupName.UPDATE_DART,
            batchRequest
        );
    }

    @Override
    public ResponseEntity<SimpleStringResponse> updateStocksFromDartCorporations(String xApiKey, BatchRequest batchRequest) {
        return processBatch(
            IBatch.BatchName.UPDATE_STOCKS_FROM_DART_CORPORATIONS,
            IBatch.BatchGroupName.UPDATE_STOCKS,
            batchRequest
        );
    }

    @Override
    public ResponseEntity<SimpleStringResponse> maintainSolidarityLeaderElections(String xApiKey, BatchRequest batchRequest) {
        return processBatch(
            IBatch.BatchName.MAINTAIN_SOLIDARITY_LEADER_ELECTIONS,
            IBatch.BatchGroupName.SOLIDARITY_LEADER_ELECTIONS,
            batchRequest
        );
    }

    @Override
    public ResponseEntity<SimpleStringResponse> createUserRetentionWeeklyCsv(
        String xApiKey,
        String formattedCsvDataType,
        BatchRequest batchRequest
    ) {
        final String batchName = UserRetentionWeeklyCsvDataType.fromPath(formattedCsvDataType).name();
        return processBatch(
            batchName,
            batchName,
            batchRequest
        );
    }

    private ResponseEntity<ag.act.model.SimpleStringResponse> processBatch(
        String batchName,
        String batchGroupName,
        ag.act.model.BatchRequest batchRequest
    ) {
        return processBatch(batchName, batchGroupName, batchRequest, batchFacade::execute);
    }

    private ResponseEntity<SimpleStringResponse> processBatch(
        String batchName,
        String batchGroupName,
        BatchRequest batchRequest,
        Function<BatchParameter, String> batchFunction
    ) {
        try {
            final BatchParameter batchParameter = BatchParameter.builder()
                .batchName(batchName)
                .batchGroupName(batchGroupName)
                .batchPeriod(batchRequest.getBatchPeriod())
                .periodTimeUnit(batchRequest.getPeriodTimeUnit())
                .isFirstCreateUserHoldingStockHistory(batchRequest.getIsFirstCreateUserHoldingStockHistory())
                .build();
            return SimpleStringResponseUtil.okResponse(batchFunction.apply(batchParameter));
        } catch (ActRuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ActRuntimeException("배치 처리 중에 알 수 없는 오류가 발생하였습니다.", ex);
        }
    }
}
