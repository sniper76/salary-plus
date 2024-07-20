package ag.act.facade.batch.executor;

import ag.act.dto.BatchParameter;
import ag.act.entity.Solidarity;
import ag.act.entity.SolidarityDailyStatistics;
import ag.act.entity.SolidarityDailySummary;
import ag.act.facade.batch.BatchRunner;
import ag.act.facade.batch.IBatchExecutor;
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
public class CreateSolidarityDailyStatisticsBatchExecutor implements IBatchExecutor {
    private final BatchRunner batchRunner;
    private final EntityManager entityManager;
    private final SolidarityService solidarityService;

    @Override
    public boolean supports(BatchParameter batchParameter) {
        return BatchName.CREATE_SOLIDARITY_DAILY_STATISTICS.equals(batchParameter.getBatchName());
    }

    @Override
    public String execute(BatchParameter batchParameter) {
        final List<Solidarity> allSolidarities = solidarityService.getAllSolidarities()
            .stream()
            .filter(solidarity -> solidarity.getSecondMostRecentDailySummary() != null)
            .toList();

        final String date = getCurrentFormattedDateTime();
        final int totalCount = allSolidarities.size();
        final String batchName = batchParameter.getBatchName();

        return batchRunner.run(
            new BatchRunner.BatchRunnerParameter(batchParameter, date, totalCount, entityManager),
            (Consumer<Integer> batchCountLog) -> {
                final AtomicInteger count = new AtomicInteger(0);

                allSolidarities.forEach(solidarity -> {
                    final SolidarityDailySummary secondMostRecentDailySummary = solidarity.getSecondMostRecentDailySummary();
                    final SolidarityDailyStatistics solidarityDailyStatistics = new SolidarityDailyStatistics();
                    solidarityDailyStatistics.setStockCode(solidarity.getStockCode());
                    solidarityDailyStatistics.setSolidarityId(solidarity.getId());
                    solidarityDailyStatistics.setDate(secondMostRecentDailySummary.getUpdatedAt().toLocalDate());
                    solidarityDailyStatistics.setStockQuantity(secondMostRecentDailySummary.getStockQuantity());
                    solidarityDailyStatistics.setStake(secondMostRecentDailySummary.getStake());
                    solidarityDailyStatistics.setMarketValue(secondMostRecentDailySummary.getMarketValue());
                    solidarityDailyStatistics.setMemberCount(secondMostRecentDailySummary.getMemberCount());

                    entityManager.persist(solidarityDailyStatistics);

                    count.incrementAndGet();
                    batchCountLog.accept(count.get());
                });

                return "[Batch] %s batch successfully finished. [creation: %s / %s]".formatted(batchName, count.get(), totalCount);
            }
        );
    }
}
