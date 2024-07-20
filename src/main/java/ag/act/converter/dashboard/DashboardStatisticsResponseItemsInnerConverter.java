package ag.act.converter.dashboard;

import ag.act.dto.admin.DashboardStatisticsCountDto;
import ag.act.model.DashboardStatisticsResponseItemsInner;
import org.springframework.stereotype.Component;

@Component
public class DashboardStatisticsResponseItemsInnerConverter {
    public DashboardStatisticsResponseItemsInner convert(
        DashboardStatisticsCountDto dto
    ) {
        return new DashboardStatisticsResponseItemsInner()
            .key(dto.getDate())
            .value(dto.getBigDecimalValue());
    }
}
