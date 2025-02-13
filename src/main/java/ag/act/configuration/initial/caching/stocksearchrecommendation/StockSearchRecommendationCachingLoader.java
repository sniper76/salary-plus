package ag.act.configuration.initial.caching.stocksearchrecommendation;

import ag.act.module.stocksearchrecommendation.StockSearchRecommendationRetriever;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class StockSearchRecommendationCachingLoader implements StockSearchRecommendationCaching {

    private final StockSearchRecommendationRetriever stockSearchRecommendationRetriever;

    //    @Async
    //    @Override
    //    public void load() {
    //        loadRecommendationSections();
    //    }
    //
    //    private void loadRecommendationSections() {
    //        stockSearchRecommendationRetriever.getRecommendationSections();
    //    }
}
