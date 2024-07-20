package ag.act.facade.batch;

import ag.act.dto.BatchParameter;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@Component
@Transactional
public class BatchRunner implements IBatch {

    public String run(BatchRunnerParameter batchRunnerParameter, Function<Consumer<Integer>, String> batchOperation) {
        final int totalCount = batchRunnerParameter.totalCount();

        startBatch(batchRunnerParameter);

        final String returnMessage = batchOperation.apply((Integer executedCount) -> {
            if (executedCount % BATCH_SIZE == 0 || executedCount >= totalCount) {
                dbFlushAndLogging(batchRunnerParameter, executedCount);
            }
        });

        dbFlush(batchRunnerParameter);

        log.info(returnMessage);
        return returnMessage;
    }

    private void startBatch(BatchRunnerParameter batchRunnerParameter) {
        final BatchParameter batchParameter = batchRunnerParameter.batchParameter();
        final String date = batchRunnerParameter.date();
        final int totalCount = batchRunnerParameter.totalCount();

        dbFlush(batchRunnerParameter);

        log.info(
            "[Batch] {} batch with period={} timeUnit={} is starting... [{}] on {}",
            batchParameter.getBatchName(),
            batchParameter.getBatchPeriod(),
            batchParameter.getPeriodTimeUnit(),
            totalCount,
            date
        );
    }

    private void dbFlushAndLogging(BatchRunnerParameter batchRunnerParameter, int count) {
        final String batchName = batchRunnerParameter.getBatchName();
        final String date = batchRunnerParameter.date();
        final int totalCount = batchRunnerParameter.totalCount();

        dbFlush(batchRunnerParameter);
        log.info("[Batch] {} batch is processing... [{} / {}] on {}", batchName, count, totalCount, date);
    }

    @SuppressWarnings("resource")
    private void dbFlush(BatchRunnerParameter batchRunnerParameter) {
        final EntityManager entityManager = batchRunnerParameter.entityManager();

        entityManager.flush();
        entityManager.clear();
    }

    public record BatchRunnerParameter(BatchParameter batchParameter, String date, int totalCount, EntityManager entityManager) {
        public String getBatchName() {
            return batchParameter.getBatchName();
        }
    }
}
