package ag.act.facade.batch.executor;

import ag.act.dto.BatchParameter;
import ag.act.entity.BatchLog;
import ag.act.facade.batch.BatchRunner;
import ag.act.facade.batch.IBatchExecutor;
import ag.act.service.BatchLogService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class CleanupUnfinishedBatchesBatchExecutor implements IBatchExecutor {
    private final BatchRunner batchRunner;
    private final EntityManager entityManager;
    private final BatchLogService batchLogService;

    @Override
    public boolean supports(BatchParameter batchParameter) {
        return BatchName.CLEANUP_UNFINISHED_BATCHES.equals(batchParameter.getBatchName());
    }

    @Override
    public String execute(BatchParameter batchParameter) {
        final String date = getCurrentFormattedDateTime();
        final List<BatchLog> unfinishedBatchLogs = batchLogService.getUnfinishedBatchesForCleanup();
        final int totalCount = unfinishedBatchLogs.size();
        final String batchName = batchParameter.getBatchName();

        return batchRunner.run(
            new BatchRunner.BatchRunnerParameter(batchParameter, date, totalCount, entityManager),
            (Consumer<Integer> batchCountLog) -> {
                final AtomicInteger count = new AtomicInteger(0);

                unfinishedBatchLogs.forEach(batchLog -> {
                    try {
                        batchLogService.forceToFailBatchLog(batchLog);
                        count.incrementAndGet();
                        batchCountLog.accept(count.get());
                    } catch (Exception e) {
                        log.error("[Batch] UnfinishedBatchLog 삭제 중에 오류가 발생하였습니다. unfinishedBatchLogs={}", unfinishedBatchLogs, e);
                    }
                });

                return "[Batch] %s batch successfully finished. [finished: %s / %s]".formatted(batchName, count.get(), totalCount);
            }
        );
    }
}
