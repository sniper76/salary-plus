package ag.act.facade.batch.executor;

import ag.act.dto.BatchParameter;
import ag.act.enums.download.matrix.UserRetentionWeeklyCsvDataType;
import ag.act.facade.batch.BatchRunner;
import ag.act.facade.batch.IBatchExecutor;
import ag.act.service.download.datamatrix.UserRetentionWeeklyCsvDownloadService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class CreateUserRetentionWeeklyCsvBatchExecutor implements IBatchExecutor {
    private static final int DEFAULT_TOTAL_COUNT = 1;
    private final BatchRunner batchRunner;
    private final EntityManager entityManager;
    private final UserRetentionWeeklyCsvDownloadService userRetentionWeeklyCsvDownloadService;

    @Override
    public boolean supports(BatchParameter batchParameter) {
        return UserRetentionWeeklyCsvDataType.fromValue(batchParameter.getBatchName()).isPresent();
    }

    @Override
    public String execute(BatchParameter batchParameter) {
        final String date = getCurrentFormattedDateTime();
        final String batchName = batchParameter.getBatchName();

        return batchRunner.run(
            new BatchRunner.BatchRunnerParameter(batchParameter, date, DEFAULT_TOTAL_COUNT, entityManager),
            (Consumer<Integer> batchCountLog) -> {
                final String uploadPath = userRetentionWeeklyCsvDownloadService.createUserRetentionWeeklyCsv(
                    UserRetentionWeeklyCsvDataType.valueOf(batchName)
                );
                return "[Batch] %s batch successfully finished. [upload path: %s]".formatted(batchName, uploadPath);
            }
        );
    }
}
