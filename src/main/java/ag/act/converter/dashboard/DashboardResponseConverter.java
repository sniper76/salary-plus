package ag.act.converter.dashboard;

import ag.act.converter.DateTimeConverter;
import ag.act.model.DashboardResponse;
import ag.act.util.DateTimeUtil;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DashboardResponseConverter {
    public ag.act.model.DashboardResponse convert(
        List<ag.act.model.DashboardItemResponse> dashboardItemResponseList,
        LocalDateTime updatedAt
    ) {
        String formattedUpdatedTime = DateTimeUtil.getFormattedKoreanTime("yyyy-MM-dd HH:mm", DateTimeConverter.convert(updatedAt));

        return new DashboardResponse()
            .descriptionLabel(String.format("최종 업데이트(전일대비): %s", formattedUpdatedTime))
            .items(dashboardItemResponseList);
    }

}
