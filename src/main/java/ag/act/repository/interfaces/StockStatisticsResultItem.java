package ag.act.repository.interfaces;

public interface StockStatisticsResultItem {

    String getDate();

    String getStockCode();

    Long getStockQuantity();

    Double getStake();

    Long getMarketValue();

    Integer getMemberCount();
}
