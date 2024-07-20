package ag.act.facade.batch.executor;

import ag.act.dto.BatchParameter;
import ag.act.facade.batch.BatchRunner;
import ag.act.facade.batch.IBatchExecutor;
import ag.act.module.dashboard.statistics.DashboardStatisticsProcessor;
import ag.act.module.dashboard.statistics.DashboardStatisticsStateCollector;
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
public class CreateDashboardStatisticsBatchExecutor implements IBatchExecutor {
    private static final int DEFAULT_TOTAL_COUNT = 1;
    private final BatchRunner batchRunner;
    private final EntityManager entityManager;
    private final DashboardStatisticsProcessor dashboardStatisticsProcessor;

    @Override
    public boolean supports(BatchParameter batchParameter) {
        return BatchName.DASHBOARD_STATISTICS.equals(batchParameter.getBatchName());
    }

    @Override
    public String execute(BatchParameter batchParameter) {
        final String date = getCurrentFormattedDateTime();
        final String batchName = batchParameter.getBatchName();

        return batchRunner.run(
            new BatchRunner.BatchRunnerParameter(batchParameter, date, DEFAULT_TOTAL_COUNT, entityManager),
            (Consumer<Integer> batchCountLog) -> {
                DashboardStatisticsStateCollector stateCollector = new DashboardStatisticsStateCollector();
                dashboardStatisticsProcessor.processBatch(stateCollector);
                return "[Batch] %s batch successfully finished. %s".formatted(batchName, stateCollector.getFailMessage());
            }
        );
    }
}
