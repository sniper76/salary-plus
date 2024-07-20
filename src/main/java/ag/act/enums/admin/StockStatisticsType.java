package ag.act.enums.admin;

import ag.act.repository.interfaces.StockStatisticsResultItem;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public enum StockStatisticsType {
    STOCK_QUANTITY("주식수") {
        @Override
        public BigDecimal toValue(StockStatisticsResultItem item) {
            return BigDecimal.valueOf(item.getStockQuantity());
        }
    },
    MEMBER_COUNT("주주수") {
        @Override
        public BigDecimal toValue(StockStatisticsResultItem item) {
            return BigDecimal.valueOf(item.getMemberCount());
        }
    },
    MARKET_VALUE("시가액") {
        @Override
        public BigDecimal toValue(StockStatisticsResultItem item) {
            return BigDecimal.valueOf(item.getMarketValue());
        }
    },
    STAKE("지분율") {
        @Override
        public BigDecimal toValue(StockStatisticsResultItem item) {
            return BigDecimal.valueOf(item.getStake());
        }
    };

    private final String displayName;

    StockStatisticsType(String displayName) {
        this.displayName = displayName;
    }

    public static StockStatisticsType fromValue(String searchTypeName) {
        try {
            return StockStatisticsType.valueOf(searchTypeName.toUpperCase());
        } catch (NullPointerException | IllegalArgumentException e) {
            return STOCK_QUANTITY;
        }
    }

    public abstract BigDecimal toValue(StockStatisticsResultItem item);
}
