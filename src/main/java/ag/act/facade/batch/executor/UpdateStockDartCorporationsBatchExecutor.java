package ag.act.facade.batch.executor;

import ag.act.dto.BatchParameter;
import ag.act.entity.StockDartCorporation;
import ag.act.facade.batch.BatchRunner;
import ag.act.facade.batch.IBatchExecutor;
import ag.act.module.dart.DartCorpCodeService;
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
public class UpdateStockDartCorporationsBatchExecutor implements IBatchExecutor {
    private final BatchRunner batchRunner;
    private final EntityManager entityManager;
    private final DartCorpCodeService dartCorpCodeService;

    @Override
    public boolean supports(BatchParameter batchParameter) {
        return BatchName.UPDATE_DART_CORPORATIONS.equals(batchParameter.getBatchName());
    }

    @Override
    public String execute(BatchParameter batchParameter) {
        final String date = getCurrentFormattedDateTime();
        final List<StockDartCorporation> oldestStockDartCorporations = dartCorpCodeService.getOldestStockDartCorporations();
        final int totalCount = oldestStockDartCorporations.size();
        final String batchName = batchParameter.getBatchName();

        return batchRunner.run(
            new BatchRunner.BatchRunnerParameter(batchParameter, date, totalCount, entityManager),
            (Consumer<Integer> batchCountLog) -> {
                final AtomicInteger count = new AtomicInteger(0);
                oldestStockDartCorporations.forEach(stockDartCorporation -> {
                    dartCorpCodeService.updateStockDartCorporations(stockDartCorporation);
                    count.incrementAndGet();
                });

                return "[Batch] %s batch successfully finished. [modification: %s / %s] on %s".formatted(batchName, count, totalCount, date);
            }
        );
    }
}
