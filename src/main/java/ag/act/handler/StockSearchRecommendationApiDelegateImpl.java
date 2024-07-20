package ag.act.handler;

import ag.act.api.StockSearchRecommendationApiDelegate;
import ag.act.model.StockSearchRecommendationSectionDataResponse;
import ag.act.module.stocksearchrecommendation.StockSearchRecommendationRetriever;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockSearchRecommendationApiDelegateImpl implements StockSearchRecommendationApiDelegate {

    private final StockSearchRecommendationRetriever stockSearchRecommendationRetriever;

    @Override
    public ResponseEntity<StockSearchRecommendationSectionDataResponse> getRecommendationSections() {
        return ResponseEntity.ok(
            new StockSearchRecommendationSectionDataResponse()
                .data(stockSearchRecommendationRetriever.getRecommendationSections())
        );
    }
}
