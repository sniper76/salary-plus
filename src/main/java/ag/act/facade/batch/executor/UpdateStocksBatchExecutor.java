package ag.act.facade.batch.executor;

import ag.act.dto.BatchParameter;
import ag.act.entity.Stock;
import ag.act.facade.batch.BatchRunner;
import ag.act.facade.batch.IBatchExecutor;
import ag.act.service.IBatchProcessor;
import ag.act.service.stock.UpdateStockBatchProcessor;
import ag.act.util.DateTimeUtil;
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
public class UpdateStocksBatchExecutor implements IBatchExecutor {
    private final BatchRunner batchRunner;
    private final EntityManager entityManager;
    private final UpdateStockBatchProcessor updateStockBatchProcessor;

    @Override
    public boolean supports(BatchParameter batchParameter) {
        return BatchName.UPDATE_STOCKS.equals(batchParameter.getBatchName());
    }

    @Override
    public String execute(BatchParameter batchParameter) {
        final String date = DateTimeUtil.getLatestStockMarketClosingDate();
        final List<Stock> sourceStocks = updateStockBatchProcessor.getSourceStocks(date);
        final int totalCount = sourceStocks.size();
        final String batchName = batchParameter.getBatchName();

        return batchRunner.run(
            new BatchRunner.BatchRunnerParameter(batchParameter, date, totalCount, entityManager),
            (Consumer<Integer> batchCountLog) -> {
                final AtomicInteger updateCount = new AtomicInteger(0);
                final AtomicInteger createCount = new AtomicInteger(0);

                updateStockBatchProcessor.updateStocks(
                    sourceStocks,
                    new IBatchProcessor.BatchProcessorParameters(batchCountLog, updateCount, createCount, BATCH_SIZE)
                );

                return "[Batch] %s batch successfully finished. [creation: %s, modification: %s / %s] on %s".formatted(
                    batchName, createCount.get(), updateCount.get(), totalCount, date
                );
            }
        );
    }
}
