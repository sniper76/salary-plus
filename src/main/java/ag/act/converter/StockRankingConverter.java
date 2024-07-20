package ag.act.converter;

import ag.act.entity.admin.StockRanking;
import ag.act.model.StockRankingResponse;
import ag.act.util.DecimalFormatUtil;
import ag.act.util.StockMarketValueUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StockRankingConverter {

    private static final String STAKE_STRING_FORMAT = "%s%%";
    private static final String MARKET_VALUE_STRING_FORMAT = "%s억원";

    public List<ag.act.model.StockRankingResponse> convert(List<StockRanking> stockRankings) {
        return stockRankings.stream()
            .map(stockRanking -> {
                final StockRankingResponse stockRankingResponse = new StockRankingResponse();
                stockRankingResponse.setStockCode(stockRanking.getStockCode());
                stockRankingResponse.setStockName(stockRanking.getStockName());
                stockRankingResponse.setStake(getFormattedStake(stockRanking.getStake()));
                stockRankingResponse.setStakeRank(stockRanking.getStakeRank());
                stockRankingResponse.setStakeRankDelta(stockRanking.getStakeRankDelta());
                stockRankingResponse.setMarketValue(getFormattedMarketValue(stockRanking.getMarketValue()));
                stockRankingResponse.setMarketValueRank(stockRanking.getMarketValueRank());
                stockRankingResponse.setMarketValueRankDelta(stockRanking.getMarketValueRankDelta());
                return stockRankingResponse;
            }).toList();
    }

    private String getFormattedStake(double stake) {
        return String.format(
            STAKE_STRING_FORMAT,
            DecimalFormatUtil.formatWithTwoDecimalPlaces(stake)
        );
    }

    private String getFormattedMarketValue(Long marketValue) {
        return String.format(
            MARKET_VALUE_STRING_FORMAT,
            StockMarketValueUtil.convertValueToBillionFormatWithExactOneDecimal(marketValue)
        );
    }
}
