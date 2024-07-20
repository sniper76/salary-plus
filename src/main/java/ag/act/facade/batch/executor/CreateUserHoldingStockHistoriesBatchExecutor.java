package ag.act.facade.batch.executor;

import ag.act.dto.BatchParameter;
import ag.act.facade.batch.BatchRunner;
import ag.act.facade.batch.IBatchExecutor;
import ag.act.service.admin.userholdingstockhistory.UserHoldingStockHistoryOnDateService;
import ag.act.util.KoreanDateTimeUtil;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class CreateUserHoldingStockHistoriesBatchExecutor implements IBatchExecutor {
    private static final int DEFAULT_TOTAL_COUNT = 0;
    private final BatchRunner batchRunner;
    private final EntityManager entityManager;
    private final UserHoldingStockHistoryOnDateService userHoldingStockHistoryOnDateService;

    @Override
    public boolean supports(BatchParameter batchParameter) {
        return BatchName.USER_HOLDING_STOCK_HISTORIES.equals(batchParameter.getBatchName());
    }

    @Override
    public String execute(BatchParameter batchParameter) {
        final String date = getCurrentFormattedDateTime();
        final ZonedDateTime nowInKoreanTime = KoreanDateTimeUtil.getNowInKoreanTime();
        final LocalDate yesterdayLocalDate = nowInKoreanTime.minusDays(1).toLocalDate();

        final String batchName = batchParameter.getBatchName();

        return batchRunner.run(
            new BatchRunner.BatchRunnerParameter(batchParameter, date, DEFAULT_TOTAL_COUNT, entityManager),
            (Consumer<Integer> batchCountLog) -> {
                int totalCount = createUserHoldingStockHistories(batchParameter, yesterdayLocalDate);

                return "[Batch] %s batch successfully finished. [creation: %s]".formatted(
                    batchName, totalCount
                );
            }
        );
    }

    private int createUserHoldingStockHistories(BatchParameter batchParameter, LocalDate yesterdayLocalDate) {
        if (batchParameter.getIsFirstCreateUserHoldingStockHistory()) {
            return userHoldingStockHistoryOnDateService.createFirstUserHoldingStockHistories(yesterdayLocalDate);
        } else {
            return userHoldingStockHistoryOnDateService.createYesterdayUserHoldingStockHistories(yesterdayLocalDate);
        }
    }
}
