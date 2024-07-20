package ag.act.facade.batch.executor;

import ag.act.dto.BatchParameter;
import ag.act.dto.admin.StockRankingDto;
import ag.act.entity.admin.StockRanking;
import ag.act.facade.batch.BatchRunner;
import ag.act.facade.batch.IBatchExecutor;
import ag.act.service.admin.stock.StockRankingService;
import ag.act.util.KoreanDateTimeUtil;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class CreateStockRankingBatchExecutor implements IBatchExecutor {
    private final BatchRunner batchRunner;
    private final EntityManager entityManager;
    private final StockRankingService stockRankingService;

    @Override
    public boolean supports(BatchParameter batchParameter) {
        return BatchName.STOCK_RANKINGS.equals(batchParameter.getBatchName());
    }

    @Override
    public String execute(BatchParameter batchParameter) {
        final String date = getCurrentFormattedDateTime();
        final ZonedDateTime nowInKoreanTime = KoreanDateTimeUtil.getNowInKoreanTime();
        final LocalDate yesterdayLocalDate = nowInKoreanTime.minusDays(1).toLocalDate();
        final List<StockRankingDto> stockRankingDtoList = stockRankingService.findStockRankingsByDate(yesterdayLocalDate);
        final int totalCount = stockRankingDtoList.size();
        final String batchName = batchParameter.getBatchName();

        final LocalDate twoDaysAgoLocalDate = nowInKoreanTime.minusDays(2).toLocalDate();
        final Map<String, StockRanking> yesterdayStockRankingMap = stockRankingService.getStockRankingMap(yesterdayLocalDate);
        final Map<String, StockRanking> twoDaysAgoStockRankingMap = stockRankingService.getStockRankingMap(twoDaysAgoLocalDate);

        return batchRunner.run(
            new BatchRunner.BatchRunnerParameter(batchParameter, date, totalCount, entityManager),
            (Consumer<Integer> batchCountLog) -> {
                final AtomicInteger count = new AtomicInteger(0);

                stockRankingDtoList.forEach(stockRankingDto -> {
                    stockRankingService.createStockRanking(stockRankingDto, yesterdayStockRankingMap, twoDaysAgoStockRankingMap);
                    count.incrementAndGet();
                    batchCountLog.accept(count.get());
                });

                return "[Batch] %s batch successfully finished. [creation: %s / %s]".formatted(batchName, count.get(), totalCount);
            }
        );
    }
}
