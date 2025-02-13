package ag.act.configuration.initial.caching.stockreferencedate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class StockReferenceDateCachingScheduler  {

    private final CacheManager cacheManager;
    private final StockReferenceDateCachingLoader stockReferenceDateCachingLoader;

    //    @Scheduled(fixedRateString = "${act.caching.user-holding-stock-on-reference.ttl}")
    //    @Override
    //    public void run() {
    //        clear();
    //        stockReferenceDateCachingLoader.load();
    //    }
    //
    //    @Override
    //    public void clear() {
    //        Optional.ofNullable(cacheManager.getCache(getCacheName()))
    //            .ifPresent(Cache::clear);
    //    }
}
