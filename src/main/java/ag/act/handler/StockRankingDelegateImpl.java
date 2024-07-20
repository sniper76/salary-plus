package ag.act.handler;

import ag.act.api.StockRankingApiDelegate;
import ag.act.converter.PageDataConverter;
import ag.act.core.annotation.PageableOverrider;
import ag.act.facade.stock.StockRankingFacade;
import ag.act.model.StockRankingDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockRankingDelegateImpl implements StockRankingApiDelegate {

    private final StockRankingFacade stockRankingFacade;
    private final PageDataConverter pageDataConverter;

    @PageableOverrider(
        defaultSize = 50,
        defaultSort = "stakeRank:ASC",
        possibleSortNames = {"stakeRank", "marketValueRank"}
    )
    @Override
    public ResponseEntity<StockRankingDataResponse> getStockRankings(Integer size, List<String> sorts) {
        PageRequest pageRequest = pageDataConverter.convert(size, sorts);

        return ResponseEntity.ok(
            stockRankingFacade.getTopNStockRankings(pageRequest)
        );
    }
}