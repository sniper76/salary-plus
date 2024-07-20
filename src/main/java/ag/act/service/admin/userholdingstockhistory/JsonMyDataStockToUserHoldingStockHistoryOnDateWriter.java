package ag.act.service.admin.userholdingstockhistory;


import ag.act.entity.MyDataSummary;
import ag.act.entity.UserHoldingStockHistoryOnDate;
import ag.act.entity.mydata.JsonMyDataStock;
import ag.act.util.AppRenewalDateProvider;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(Transactional.TxType.REQUIRES_NEW)
@RequiredArgsConstructor
public class JsonMyDataStockToUserHoldingStockHistoryOnDateWriter {
    private static final int SAVE_CHUNK_SIZE = 100;
    private static final int ONE_DAY = 1;

    private final EntityManager entityManager;
    private final AppRenewalDateProvider appRenewalDateProvider;

    public int generateFillDataUntilYesterday(MyDataSummary myDataSummary, LocalDate endDate) {
        final Map<String, List<JsonMyDataStock>> groupedData = myDataSummary.getJsonMyData().getJsonMyDataStocksMapByStockCode();
        final Long userId = myDataSummary.getUserId();

        int sumCount = 0;
        for (List<JsonMyDataStock> groupInStock : groupedData.values()) {
            final Map<LocalDate, List<JsonMyDataStock>> dateJsonStockMap = groupInStock.stream()
                .collect(Collectors.groupingBy(JsonMyDataStock::getReferenceDate));

            final List<Map.Entry<LocalDate, List<JsonMyDataStock>>> entryList = dateJsonStockMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .toList();

            LocalDate currentDate = entryList.get(0).getKey();
            UserHoldingStockHistoryOnDate currentUserHoldingStockHistoryOnDate = null;
            while (!currentDate.isAfter(endDate)) {
                if (dateJsonStockMap.containsKey(currentDate)) {
                    currentUserHoldingStockHistoryOnDate = generateUserHoldingStockHistoryOnDate(userId, dateJsonStockMap, currentDate);
                }

                Objects.requireNonNull(currentUserHoldingStockHistoryOnDate, "currentUserHoldingStockHistoryOnDate is null");
                if (isEqualOrAfterAppRenewalDate(currentDate)) {
                    entityManager.persist(generateUserHoldingStockHistoryOnDate(currentUserHoldingStockHistoryOnDate, currentDate));
                    sumCount++;
                }

                if ((sumCount % SAVE_CHUNK_SIZE) == 0) {
                    dbFlush();
                }
                currentDate = currentDate.plusDays(ONE_DAY);
            }

            dbFlush();
        }

        return sumCount;
    }

    private boolean isEqualOrAfterAppRenewalDate(LocalDate currentDate) {
        return !appRenewalDateProvider.get().isAfter(currentDate);
    }

    private void dbFlush() {
        entityManager.flush();
        entityManager.clear();
    }

    @NotNull
    private UserHoldingStockHistoryOnDate generateUserHoldingStockHistoryOnDate(
        Long userId,
        Map<LocalDate, List<JsonMyDataStock>> dateJsonStockMap,
        LocalDate currentDate
    ) {
        final JsonMyDataStock jsonMyDataStock = dateJsonStockMap.get(currentDate).get(0);
        return generateUserHoldingStockHistoryOnDate(
            userId,
            jsonMyDataStock.getCode(),
            jsonMyDataStock.getQuantity(),
            currentDate
        );
    }

    @NotNull
    private UserHoldingStockHistoryOnDate generateUserHoldingStockHistoryOnDate(
        UserHoldingStockHistoryOnDate source,
        LocalDate currentDate
    ) {
        return generateUserHoldingStockHistoryOnDate(
            source.getUserId(),
            source.getStockCode(),
            source.getQuantity(),
            currentDate
        );
    }

    @NotNull
    private UserHoldingStockHistoryOnDate generateUserHoldingStockHistoryOnDate(
        Long userId,
        String stockCode,
        Long quantity,
        LocalDate currentDate
    ) {
        return new UserHoldingStockHistoryOnDate(
            userId,
            stockCode,
            quantity,
            currentDate
        );
    }
}