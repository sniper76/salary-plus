package ag.act.facade.batch.executor;

import ag.act.dto.BatchParameter;
import ag.act.entity.TestStock;
import ag.act.facade.admin.AdminQualityAssuranceFacade;
import ag.act.facade.batch.BatchRunner;
import ag.act.facade.batch.IBatchExecutor;
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
public class UpdateTestStocksBatchExecutor implements IBatchExecutor {
    private final BatchRunner batchRunner;
    private final EntityManager entityManager;
    private final AdminQualityAssuranceFacade adminQualityAssuranceFacade;

    @Override
    public boolean supports(BatchParameter batchParameter) {
        return BatchName.UPDATE_TEST_STOCKS.equals(batchParameter.getBatchName());
    }

    @Override
    public String execute(BatchParameter batchParameter) {
        final String date = getCurrentFormattedDateTime();
        final List<TestStock> testStocks = adminQualityAssuranceFacade.getTestStocks();
        final int totalCount = testStocks.size();
        final String batchName = batchParameter.getBatchName();

        return batchRunner.run(
            new BatchRunner.BatchRunnerParameter(batchParameter, date, totalCount, entityManager),
            (Consumer<Integer> batchCountLog) -> {
                final AtomicInteger count = new AtomicInteger(0);
                testStocks.forEach(testStock -> {
                    if (adminQualityAssuranceFacade.syncSomeDataByTestStock(testStock)) {
                        count.incrementAndGet();
                    }
                });

                return "[Batch] %s batch successfully finished. [sent: %s / %s]".formatted(batchName, count, totalCount);
            }
        );
    }
}
