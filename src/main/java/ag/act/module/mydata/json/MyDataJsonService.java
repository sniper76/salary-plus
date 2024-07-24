package ag.act.module.mydata.json;

import ag.act.dto.mydata.AccountTransactionDto;
import ag.act.dto.mydata.IntermediateUserHoldingStockDto;
import ag.act.entity.StockReferenceDate;
import ag.act.entity.mydata.JsonMyDataStock;
import ag.act.service.stock.StockReferenceDateService;
import ag.act.util.DateTimeUtil;
import ag.act.util.NumberUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ag.act.util.KoreanDateTimeUtil.getEndOfLastYearLocalDate;
import static ag.act.util.KoreanDateTimeUtil.getTodayLocalDate;
import static ag.act.util.KoreanDateTimeUtil.getYesterdayLocalDate;

@Service
public class MyDataJsonService {
    private final StockReferenceDateService stockReferenceDateService;

    public MyDataJsonService(StockReferenceDateService stockReferenceDateService) {
        this.stockReferenceDateService = stockReferenceDateService;
    }

    public JsonMyDataStock createEndOfLastYearJsonMyDataStock(IntermediateUserHoldingStockDto stockInfoDto, Long quantity) {
        return createJsonMyDataStock(stockInfoDto, quantity, getEndOfLastYearLocalDate());
    }

    public JsonMyDataStock createYesterdayJsonMyDataStock(IntermediateUserHoldingStockDto stockInfoDto, Long quantity) {
        return createJsonMyDataStock(stockInfoDto, quantity, getYesterdayLocalDate());
    }

    public JsonMyDataStock createTodayJsonMyDataStock(IntermediateUserHoldingStockDto stockInfoDto, Long quantity) {
        return createJsonMyDataStock(stockInfoDto, quantity, getTodayLocalDate());
    }

    private JsonMyDataStock createJsonMyDataStock(IntermediateUserHoldingStockDto stockInfoDto, Long quantity, LocalDate referenceDate) {
        return JsonMyDataStock.builder()
            .name(stockInfoDto.getStockName())
            .code(stockInfoDto.getStockCode())
            .myDataProdCode(stockInfoDto.getMyDataProdCode())
            .quantity(quantity)
            .referenceDate(referenceDate)
            .registerDate(DateTimeUtil.getTodayLocalDate())
            .updatedAt(DateTimeUtil.getTodayLocalDateTime())
            .build();
    }

    public List<JsonMyDataStock> createNewJsonMyDataStocks(
        List<AccountTransactionDto> accountTransactionDtos,
        IntermediateUserHoldingStockDto stockInfoDto,
        LocalDateTime theDayBeforeLastUpdatedAt,
        AtomicLong quantity
    ) {
        final boolean isReverse = accountTransactionDtos.stream().anyMatch(AccountTransactionDto::isReverse);

        final List<JsonMyDataStock> jsonMyDataStocks = accountTransactionDtos.stream()
            .filter(accountTransactionDto -> accountTransactionDto.getTransactionLocalDateTime().isAfter(theDayBeforeLastUpdatedAt))
            .map(accountTransactionDto -> {
                final Long currentQuantity = quantity.get();
                quantity.set(getNextQuantity(accountTransactionDto, currentQuantity));
                final LocalDate referenceDate = accountTransactionDto.getTransactionLocalDateTime().toLocalDate();

                return createJsonMyDataStock(
                    stockInfoDto,
                    isReverse ? quantity.get() : currentQuantity,
                    referenceDate
                );
            })
            .collect(Collectors.toList());


        // 연산한 결과에 따라 마지막 데이터를 추가해준다.
        if (!jsonMyDataStocks.isEmpty()) {
            if (isReverse) {
                final JsonMyDataStock mostEarliestJsonMyDataStock = getMostEarliestJsonMyDataStock(jsonMyDataStocks);
                jsonMyDataStocks.add(
                    mostEarliestJsonMyDataStock.copyOf(
                        getTodayLocalDate(),
                        0L,
                        Boolean.TRUE
                    )
                );
            } else {
                final JsonMyDataStock mostEarliestJsonMyDataStock = getMostEarliestJsonMyDataStock(jsonMyDataStocks);
                jsonMyDataStocks.add(
                    mostEarliestJsonMyDataStock.copyOf(
                        mostEarliestJsonMyDataStock.getReferenceDate().minusDays(1L),
                        quantity.get(),
                        Boolean.TRUE
                    )
                );
            }
        }

        return mergeJsonMyDataStocksInTheSameReferenceDate(jsonMyDataStocks);
    }

    private JsonMyDataStock getMostEarliestJsonMyDataStock(List<JsonMyDataStock> jsonMyDataStocks) {
        return jsonMyDataStocks.get(jsonMyDataStocks.size() - 1);
    }

    public List<JsonMyDataStock> mergeJsonMyDataStocksInTheSameReferenceDate(List<JsonMyDataStock> jsonMyDataStockList) {
        // 같은 날 여러번 거래한 내역은 필요 없기 때문에, 같은 날짜의 데이터는 하나로 합친다.
        return jsonMyDataStockList
            .stream()
            .collect(Collectors.toMap(
                JsonMyDataStock::getReferenceDate,
                Function.identity(),
                (v1, v2) -> v1 // 가장 최근 데이터를 사용
            ))
            .values()
            .stream()
            .sorted(Comparator.comparing(JsonMyDataStock::getReferenceDate).reversed())
            .toList();
    }

    public void forceCreateMyDataForReferenceDates(String stockCode, List<JsonMyDataStock> jsonMyDataStocks) {
        final List<StockReferenceDate> referenceDateList = stockReferenceDateService.getStockReferenceDatesWithinThreeMonths(stockCode);

        if (referenceDateList.isEmpty()) {
            return;
        }

        referenceDateList.forEach(referenceDateEntity -> {
            final LocalDate referenceDate = referenceDateEntity.getReferenceDate();

            if (isAlreadyHaveThatReferenceDate(jsonMyDataStocks, referenceDate)) {
                return;
            }

            jsonMyDataStocks.add(jsonMyDataStocks.get(0).copyOf(referenceDate));
        });
    }

    public void createMyDataForReferenceDates(String stockCode, List<JsonMyDataStock> jsonMyDataStocks) {
        final List<StockReferenceDate> referenceDateList = stockReferenceDateService.getStockReferenceDatesWithinThreeMonths(stockCode);

        if (referenceDateList.isEmpty()) {
            return;
        }

        referenceDateList.forEach(referenceDateEntity -> {
            final LocalDate referenceDate = referenceDateEntity.getReferenceDate();

            if (isAlreadyHaveThatReferenceDate(jsonMyDataStocks, referenceDate)) {
                return;
            }

            createNewDataAndAddToList(jsonMyDataStocks, referenceDate);
        });
    }

    private void createNewDataAndAddToList(List<JsonMyDataStock> jsonMyDataStocks, LocalDate referenceDate) {
        if (jsonMyDataStocks.isEmpty()) {
            return;
        }

        final boolean isReverse = jsonMyDataStocks.get(0).getQuantity() == 0L;

        if (isReverse) {
            // 기준 날짜 이전의 데이터 중 가장 최신 데이터를 찾아서 복사해서 넣어준다.
            jsonMyDataStocks.stream()
                .filter(jsonMyDataStock -> jsonMyDataStock.getReferenceDate().isAfter(referenceDate))
                .min(Comparator.comparing(JsonMyDataStock::getReferenceDate))
                .ifPresentOrElse(
                    jsonMyDataStock -> jsonMyDataStocks.add(jsonMyDataStock.copyOf(referenceDate)),
                    () -> addMyDataStockOnReferenceDateIfHasMoreThanOneQuantity(jsonMyDataStocks, referenceDate)
                );
        } else {
            // 기준 날짜 이전의 데이터 중 가장 최신 데이터를 찾아서 복사해서 넣어준다.
            jsonMyDataStocks.stream()
                .filter(jsonMyDataStock -> jsonMyDataStock.getReferenceDate().isBefore(referenceDate))
                .max(Comparator.comparing(JsonMyDataStock::getReferenceDate))
                .ifPresentOrElse(
                    jsonMyDataStock -> jsonMyDataStocks.add(jsonMyDataStock.copyOf(referenceDate)),
                    () -> addMyDataStockOnReferenceDateIfHasMoreThanOneQuantity(jsonMyDataStocks, referenceDate)
                );
        }
    }

    private void addMyDataStockOnReferenceDateIfHasMoreThanOneQuantity(List<JsonMyDataStock> jsonMyDataStocks, LocalDate referenceDate) {
        final JsonMyDataStock mostEarliestJsonMyDataStock = getMostEarliestJsonMyDataStock(jsonMyDataStocks);

        if (mostEarliestJsonMyDataStock.getQuantity() > 0L) {
            jsonMyDataStocks.add(mostEarliestJsonMyDataStock.copyOf(referenceDate));
        }
    }

    private boolean isAlreadyHaveThatReferenceDate(List<JsonMyDataStock> jsonMyDataStocks, LocalDate referenceDate) {
        return jsonMyDataStocks.stream()
            .anyMatch(jsonMyDataStock -> jsonMyDataStock.getReferenceDate().isEqual(referenceDate));
    }

    private Long getNextQuantity(AccountTransactionDto accountTransactionDto, Long currentQuantity) {
        final Long transactionStockNumber = NumberUtil.toLong(accountTransactionDto.getTransNum(), 0L);

        if (accountTransactionDto.getTransactionType().isBuyTransaction()) {
            return currentQuantity - transactionStockNumber;
        }

        return currentQuantity + transactionStockNumber;
    }
}
