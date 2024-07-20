package ag.act.configuration.initial.caching.stockreferencedate;

import ag.act.entity.StockReferenceDate;
import ag.act.service.stock.StockReferenceDateService;
import ag.act.service.stock.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class StockReferenceDateCachingLoader implements StockReferenceDateCaching {

    private final StockReferenceDateService stockReferenceDateService;
    private final StockService stockService;
    private final CacheManager cacheManager;

    @Async
    @Override
    public void load() {
        loadingAllStockReferenceDatesToCache();
    }

    private void loadingAllStockReferenceDatesToCache() {
        stockService.getAllSimpleStocks()
            .parallelStream()
            .forEach(stock -> {
                final String stockCode = stock.getCode();
                final var stockReferenceDates = stockReferenceDateService.getStockReferenceDatesWithinThreeMonths(stockCode);
                addToCache(stockCode, stockReferenceDates);
            });
    }

    @SuppressWarnings("DataFlowIssue")
    private void addToCache(String stockCode, List<StockReferenceDate> stockReferenceDatesWithinThreeMonths) {
        cacheManager.getCache(getCacheName())
            .putIfAbsent(stockCode, stockReferenceDatesWithinThreeMonths);
    }
}