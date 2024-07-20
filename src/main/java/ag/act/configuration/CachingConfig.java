package ag.act.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import static ag.act.configuration.initial.caching.CachingType.STOCK_SEARCH_RECOMMENDATION_SECTIONS;
import static ag.act.configuration.initial.caching.CachingType.USER_HOLDING_STOCK_ON_REFERENCE_WITHIN_THREE_MONTHS;

@Configuration
@EnableCaching
@Slf4j
@EnableScheduling
@RequiredArgsConstructor
public class CachingConfig {

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
            USER_HOLDING_STOCK_ON_REFERENCE_WITHIN_THREE_MONTHS.name(),
            STOCK_SEARCH_RECOMMENDATION_SECTIONS.name()
        );
    }
}
