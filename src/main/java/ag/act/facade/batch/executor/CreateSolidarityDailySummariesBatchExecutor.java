package ag.act.facade.batch.executor;

import ag.act.dto.BatchParameter;
import ag.act.entity.Solidarity;
import ag.act.facade.batch.BatchRunner;
import ag.act.facade.batch.IBatchExecutor;
import ag.act.service.solidarity.SolidarityDailySummaryService;
import ag.act.service.solidarity.SolidarityService;
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
public class CreateSolidarityDailySummariesBatchExecutor implements IBatchExecutor {
    private final BatchRunner batchRunner;
    private final EntityManager entityManager;
    private final SolidarityService solidarityService;
    private final SolidarityDailySummaryService solidarityDailySummaryService;

    @Override
    public boolean supports(BatchParameter batchParameter) {
        return BatchName.CREATE_SOLIDARITY_DAILY_SUMMARIES.equals(batchParameter.getBatchName());
    }

    @Override
    public String execute(BatchParameter batchParameter) {
        final List<Solidarity> solidarities = solidarityService.getAllSolidarities();
        final String date = getCurrentFormattedDateTime();
        final int totalCount = solidarities.size();
        final String batchName = batchParameter.getBatchName();

        return batchRunner.run(
            new BatchRunner.BatchRunnerParameter(batchParameter, date, totalCount, entityManager),
            (Consumer<Integer> batchCountLog) -> {
                final AtomicInteger count = new AtomicInteger(0);

                solidarities.forEach(solidarity -> {
                    if (solidarity.getMostRecentDailySummary() != null) {
                        solidarity.setSecondMostRecentDailySummary(solidarity.getMostRecentDailySummary());
                    }

                    solidarity.setMostRecentDailySummary(
                        solidarityDailySummaryService.createSolidarityDailySummary(solidarity)
                    );
                    solidarityService.saveSolidarity(solidarity);

                    count.incrementAndGet();
                    batchCountLog.accept(count.get());
                });

                return "[Batch] %s batch successfully finished. [creation: %s / %s]".formatted(batchName, count.get(), totalCount);
            }
        );
    }
}
