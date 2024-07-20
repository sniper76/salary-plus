package ag.act.converter.stock;

import ag.act.enums.admin.StockStatisticsType;
import ag.act.model.StockStatisticsResponse;
import ag.act.repository.interfaces.StockStatisticsResultItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SolidarityStatisticsDailyResponseConverter {

    public List<StockStatisticsResponse> convert(
        List<StockStatisticsResultItem> stockStatisticsResultItems,
        StockStatisticsType type
    ) {

        return stockStatisticsResultItems
            .stream()
            .map(it -> new StockStatisticsResponse().key(it.getDate()).value(type.toValue(it)))
            .toList();
    }
}
