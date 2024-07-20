package ag.act.converter.dashboard;

import ag.act.entity.dashboard.item.DashboardItem;
import ag.act.model.DashboardItemResponse;
import org.springframework.stereotype.Component;

@Component
public class DashboardItemResponseConverter {
    public ag.act.model.DashboardItemResponse convert(DashboardItem item) {
        return new DashboardItemResponse()
            .title(item.getTitle())
            .value(item.getCurrentValueTextWithUnit())
            .variation(
                item.getVariationResponse()
            );
    }
}
