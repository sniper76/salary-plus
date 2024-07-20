package ag.act.configuration.initial.caching.stocksearchrecommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class StockSearchRecommendationCachingScheduler implements StockSearchRecommendationCaching {

    private final CacheManager cacheManager;
    private final StockSearchRecommendationCachingLoader stockSearchRecommendationCachingLoader;

    @Scheduled(cron = "${act.caching.stock-search-recommendation.cron-expression}")
    @Override
    public void run() {
        clear();
        stockSearchRecommendationCachingLoader.load();
    }

    @Override
    public void clear() {
        Optional.ofNullable(cacheManager.getCache(getCacheName()))
            .ifPresent(Cache::clear);
    }

    @PostConstruct
    public void init() {
        run();
    }
}
