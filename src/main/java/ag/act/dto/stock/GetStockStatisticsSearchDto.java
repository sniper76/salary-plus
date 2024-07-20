package ag.act.dto.stock;

import ag.act.enums.admin.StockStatisticsPeriodType;
import ag.act.enums.admin.StockStatisticsType;
import lombok.Getter;

@Getter
public class GetStockStatisticsSearchDto {
    private final String code;
    private final StockStatisticsType type;
    private final StockStatisticsPeriodType periodType;
    private final GetStockStatisticsSearchPeriodDto searchPeriod;

    public GetStockStatisticsSearchDto(String code, String type, String periodType, String period) {
        this.code = code;
        this.type = StockStatisticsType.fromValue(type);
        this.periodType = StockStatisticsPeriodType.fromValue(periodType);

        searchPeriod = this.periodType.getSearchPeriod(period);
    }
}
