package ag.act.service.user;

import ag.act.entity.MyDataSummary;
import ag.act.entity.StockReferenceDate;
import ag.act.entity.UserHoldingStockOnReferenceDate;
import ag.act.entity.mydata.JsonMyData;
import ag.act.entity.mydata.JsonMyDataStock;
import ag.act.repository.UserHoldingStockOnReferenceDateRepository;
import ag.act.service.stock.StockReferenceDateService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static ag.act.model.Status.ACTIVE;

@Service
@Transactional
@RequiredArgsConstructor
public class UserHoldingStockOnReferenceDateSyncService {
    private final StockReferenceDateService stockReferenceDateService;
    private final UserHoldingStockOnReferenceDateRepository userHoldingStockOnReferenceDateRepository;

    public void syncUserHoldingStockOnReferenceDate(MyDataSummary myDataSummary, Long userId) {

        final List<JsonMyDataStock> jsonMyDataStockToSave = findJsonMyDataStockForSave(myDataSummary.getJsonMyData(), userId);

        final List<UserHoldingStockOnReferenceDate> userHoldingStockOnReferenceDatesForCreate = jsonMyDataStockToSave.stream()
            .filter(jsonMyDataStock -> jsonMyDataStock.getQuantity() > 0L)
            .map(jsonMyDataStock -> createUserHoldingStockOnReferenceDate(userId, jsonMyDataStock))
            .toList();

        userHoldingStockOnReferenceDateRepository.saveAll(userHoldingStockOnReferenceDatesForCreate);
    }

    public void deleteAllUserHoldingStockOnReferenceDateBeforeSync(Long userId) {
        final List<Long> idsForDelete = userHoldingStockOnReferenceDateRepository.findAllIdsWithActiveHoldingStocks(userId);
        userHoldingStockOnReferenceDateRepository.deleteAllByIdInBatch(idsForDelete);
    }

    private List<JsonMyDataStock> findJsonMyDataStockForSave(JsonMyData jsonMyData, Long userId) {
        Map<String, List<StockReferenceDate>> stockReferenceDateMap = getStockReferenceDateMap(jsonMyData);
        List<JsonMyDataStock> filteredJsonMyDataStockByReferenceDate = filterJsonMyDataStockByReferenceDate(
            jsonMyData,
            stockReferenceDateMap
        );

        return filterAndCollectUniqueStocks(userId, filteredJsonMyDataStockByReferenceDate);
    }

    private List<JsonMyDataStock> filterAndCollectUniqueStocks(Long userId, List<JsonMyDataStock> jsonDataStockList) {
        final Map<String, UserHoldingStockOnReferenceDate> userHoldingStockOnReferenceDateMap = getUserHoldingStockOnReferenceDateMap(userId);

        return jsonDataStockList.stream()
            .filter(jsonDataStock -> !hasJsonMyStock(jsonDataStock, userHoldingStockOnReferenceDateMap))
            .collect(getJsonMyDataStockMapCollector())
            .values()
            .stream()
            .toList();
    }

    private Boolean hasJsonMyStock(
        JsonMyDataStock jsonMyDataStock,
        Map<String, UserHoldingStockOnReferenceDate> userHoldingStockOnReferenceDateMap
    ) {
        final String onReferenceMapKey = userHoldingStockOnReferenceMapKey(jsonMyDataStock);
        return Optional
            .ofNullable(userHoldingStockOnReferenceDateMap.get(onReferenceMapKey))
            .isPresent();
    }

    private Map<String, UserHoldingStockOnReferenceDate> getUserHoldingStockOnReferenceDateMap(Long userId) {
        return userHoldingStockOnReferenceDateRepository.findAllByUserId(userId)
            .stream()
            .collect(Collectors.toMap(this::userHoldingStockOnReferenceMapKey, Function.identity()));
    }

    private String userHoldingStockOnReferenceMapKey(UserHoldingStockOnReferenceDate userHoldingStockOnReferenceDate) {
        return userHoldingStockOnReferenceMapKey(
            userHoldingStockOnReferenceDate.getStockCode(),
            userHoldingStockOnReferenceDate.getReferenceDate()
        );
    }

    private String userHoldingStockOnReferenceMapKey(JsonMyDataStock jsonMyDataStock) {
        return userHoldingStockOnReferenceMapKey(
            jsonMyDataStock.getCode(),
            jsonMyDataStock.getReferenceDate()
        );
    }

    private String userHoldingStockOnReferenceMapKey(String stockCode, LocalDate referenceDate) {
        return "%s-%s".formatted(stockCode, referenceDate);
    }

    @NotNull
    private Collector<JsonMyDataStock, ?, Map<String, JsonMyDataStock>> getJsonMyDataStockMapCollector() {
        return Collectors.toMap(
            this::getStockReferenceDateKey,
            Function.identity(),
            (v1, v2) -> v2
        );
    }

    private List<JsonMyDataStock> filterJsonMyDataStockByReferenceDate(
        JsonMyData jsonMyData,
        Map<String, List<StockReferenceDate>> stockReferenceDateMap
    ) {
        return jsonMyData.getJsonMyDataStockList().stream()
            .filter(jsonMyDataStock -> stockReferenceDateMap.containsKey(getStockReferenceDateKey(jsonMyDataStock)))
            .toList();
    }

    private Map<String, List<StockReferenceDate>> getStockReferenceDateMap(JsonMyData jsonMyData) {
        final List<String> stockCodes = jsonMyData.getJsonMyDataStockList().stream()
            .map(JsonMyDataStock::getCode)
            .toList();

        return stockReferenceDateService.getAllStockReferenceDatesWithinThreeMonths(stockCodes).stream()
            .collect(Collectors.groupingBy(this::getStockReferenceDateKey));
    }

    private UserHoldingStockOnReferenceDate createUserHoldingStockOnReferenceDate(Long userId, JsonMyDataStock jsonMyDataStock) {
        final var userHoldingStockOnReferenceDate = new UserHoldingStockOnReferenceDate();
        userHoldingStockOnReferenceDate.setUserId(userId);
        userHoldingStockOnReferenceDate.setStockCode(jsonMyDataStock.getCode());
        userHoldingStockOnReferenceDate.setReferenceDate(jsonMyDataStock.getReferenceDate());
        userHoldingStockOnReferenceDate.setQuantity(jsonMyDataStock.getQuantity());
        userHoldingStockOnReferenceDate.setStatus(ACTIVE);

        return userHoldingStockOnReferenceDate;
    }

    private String getStockReferenceDateKey(JsonMyDataStock it) {
        return getStockReferenceDateKey(it.getCode(), it.getReferenceDate());
    }

    private String getStockReferenceDateKey(StockReferenceDate it) {
        return getStockReferenceDateKey(it.getStockCode(), it.getReferenceDate());
    }

    private String getStockReferenceDateKey(String stockCode, LocalDate referenceDate) {
        return "%s_%s".formatted(stockCode, referenceDate);
    }
}
