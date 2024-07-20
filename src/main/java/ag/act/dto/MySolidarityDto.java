package ag.act.dto;

import ag.act.dto.mysolidarity.InProgressActionUserStatus;
import ag.act.entity.Solidarity;
import ag.act.entity.SolidarityDailySummary;
import ag.act.entity.Stock;
import ag.act.entity.UserHoldingStock;
import ag.act.entity.admin.StockRanking;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MySolidarityDto {
    private UserHoldingStock userHoldingStock;
    private Stock stock;
    private Solidarity solidarity;
    private SolidarityDailySummary mostRecentSolidarityDailySummary;
    private InProgressActionUserStatus inProgressActionUserStatus;
    private String stakeRank;
    private Integer stakeRankDelta;
    private String marketValueRank;
    private Integer marketValueRankDelta;

    public MySolidarityDto(
        UserHoldingStock userHoldingStock,
        Stock stock,
        Solidarity solidarity,
        SolidarityDailySummary mostRecentSolidarityDailySummary,
        StockRanking stockRanking
    ) {
        this.userHoldingStock = userHoldingStock;
        this.userHoldingStock.setStock(stock);
        this.stock = stock;
        this.stock.setSolidarity(solidarity);
        this.solidarity = solidarity;
        this.solidarity.setStock(stock);
        this.solidarity.setMostRecentDailySummary(mostRecentSolidarityDailySummary);
        this.mostRecentSolidarityDailySummary = mostRecentSolidarityDailySummary;
        this.stakeRank = getStakeRank(getStakeRankValue(stockRanking));
        this.stakeRankDelta = getStakeRankDeltaValue(stockRanking);
        this.marketValueRank = getMarketValueRank(getMarketValueRankValue(stockRanking));
        this.marketValueRankDelta = getMarketValueRankDeltaValue(stockRanking);
    }

    private Integer getStakeRankValue(StockRanking stockRanking) {
        return stockRanking == null ? null : stockRanking.getStakeRank();
    }

    private Integer getStakeRankDeltaValue(StockRanking stockRanking) {
        return stockRanking == null ? null : stockRanking.getStakeRankDelta();
    }

    private Integer getMarketValueRankValue(StockRanking stockRanking) {
        return stockRanking == null ? null : stockRanking.getMarketValueRank();
    }

    private Integer getMarketValueRankDeltaValue(StockRanking stockRanking) {
        return stockRanking == null ? null : stockRanking.getMarketValueRankDelta();
    }

    private String getStakeRank(Integer stakeRank) {
        return stakeRank == null ? "-" : stakeRank.toString();
    }

    private String getMarketValueRank(Integer marketValueRank) {
        return marketValueRank == null ? "-" : marketValueRank.toString();
    }
}
