package ag.act.dto.admin;

import ag.act.entity.admin.StockRanking;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockRankingDto {
    private String stockCode;
    private String stockName;
    private LocalDate date;
    private Double stake;
    private Long stakeRank;
    private Long marketValue;
    private Long marketValueRank;

    public Integer getStakeRank() {
        return stakeRank.intValue();
    }

    public Integer getMarketValueRank() {
        return marketValueRank.intValue();
    }

    public StockRanking toStockRanking(Optional<StockRanking> twoDaysAgoStockRanking) {
        final StockRanking stockRanking = new StockRanking();
        stockRanking.setStockCode(stockCode);
        stockRanking.setStockName(stockName);
        stockRanking.setDate(date);
        stockRanking.setStake(stake);
        stockRanking.setStakeRank(getStakeRank());
        stockRanking.setStakeRankDelta(
            twoDaysAgoStockRanking.map(it -> it.getStakeRank() - getStakeRank()).orElse(0)
        );
        stockRanking.setMarketValue(marketValue);
        stockRanking.setMarketValueRank(getMarketValueRank());
        stockRanking.setMarketValueRankDelta(
            twoDaysAgoStockRanking.map(it -> it.getMarketValueRank() - getMarketValueRank()).orElse(0)
        );
        return stockRanking;
    }
}
