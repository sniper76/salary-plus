package ag.act.service.admin.userholdingstockhistory;


import ag.act.entity.UserHoldingStock;
import ag.act.entity.UserHoldingStockHistoryOnDate;
import ag.act.service.user.UserHoldingStockService;
import ag.act.util.ChunkUtil;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@Transactional(Transactional.TxType.REQUIRES_NEW)
@RequiredArgsConstructor
public class UserHoldingStockToUserHoldingStockHistoryOnDateWriter {
    private static final int SAVE_CHUNK_SIZE = 100;

    private final EntityManager entityManager;
    private final ChunkUtil chunkUtil;
    private final UserHoldingStockService userHoldingStockService;

    public void createYesterdayUserHoldingStockHistories(LocalDate yesterdayLocalDate, List<Long> idList, AtomicInteger resultCount) {
        List<UserHoldingStock> allUserHoldingStocks = userHoldingStockService.findAllByIds(idList);
        final List<List<UserHoldingStock>> userHoldingStockChuckList = getContentChunks(allUserHoldingStocks);

        userHoldingStockChuckList
            .forEach(userHoldingStocks -> {
                var newRows = toUserHoldingStockHistoryOnDates(yesterdayLocalDate, userHoldingStocks);

                newRows.forEach(entityManager::persist);
                dbFlush();
                resultCount.addAndGet(newRows.size());
            });
        dbFlush();
    }

    @NotNull
    private List<UserHoldingStockHistoryOnDate> toUserHoldingStockHistoryOnDates(
        LocalDate yesterdayLocalDate,
        List<UserHoldingStock> userHoldingStocks
    ) {
        return userHoldingStocks
            .stream()
            .map(userHoldingStock -> new UserHoldingStockHistoryOnDate(
                userHoldingStock.getUserId(),
                userHoldingStock.getStockCode(),
                userHoldingStock.getQuantity(),
                yesterdayLocalDate
            ))
            .toList();
    }

    private List<List<UserHoldingStock>> getContentChunks(List<UserHoldingStock> content) {
        return chunkUtil.getChunks(content, SAVE_CHUNK_SIZE);
    }

    private void dbFlush() {
        entityManager.flush();
        entityManager.clear();
    }
}