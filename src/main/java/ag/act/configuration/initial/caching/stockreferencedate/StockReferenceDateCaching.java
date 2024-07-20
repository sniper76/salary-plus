package ag.act.configuration.initial.caching.stockreferencedate;

import ag.act.configuration.initial.caching.CachingLoader;
import ag.act.configuration.initial.caching.CachingScheduler;

import static ag.act.configuration.initial.caching.CachingType.USER_HOLDING_STOCK_ON_REFERENCE_WITHIN_THREE_MONTHS;

public interface StockReferenceDateCaching extends CachingScheduler, CachingLoader {

    @Override
    default String getLogName() {
        return USER_HOLDING_STOCK_ON_REFERENCE_WITHIN_THREE_MONTHS.getDescription();
    }

    @Override
    default String getCacheName() {
        return USER_HOLDING_STOCK_ON_REFERENCE_WITHIN_THREE_MONTHS.name();
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
