package ag.act.configuration.initial.caching.stocksearchrecommendation;

import ag.act.configuration.initial.caching.CachingLoader;
import ag.act.configuration.initial.caching.CachingScheduler;

import static ag.act.configuration.initial.caching.CachingType.STOCK_SEARCH_RECOMMENDATION_SECTIONS;

public interface StockSearchRecommendationCaching extends CachingScheduler, CachingLoader {

    @Override
    default String getLogName() {
        return STOCK_SEARCH_RECOMMENDATION_SECTIONS.getDescription();
    }

    @Override
    default String getCacheName() {
        return STOCK_SEARCH_RECOMMENDATION_SECTIONS.name();
    }

    @Override
    default void load() {
    }

    @Override
    default void run() {
    }

    @Override
    default void clear() {
    }
}
