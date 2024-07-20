package ag.act.converter.dashboard;

import ag.act.enums.admin.DashboardStatisticsPeriodType;
import ag.act.enums.admin.UpDownType;
import ag.act.model.DashboardStatisticsItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DashboardStatisticsItemResponseConverter {

    public DashboardStatisticsItemResponse convert(
        Long currentValue, Long pastValue, Long total, String title,
        DashboardStatisticsPeriodType periodType
    ) {
        long changeCalculateValue = currentValue - pastValue;
        return new DashboardStatisticsItemResponse()
            .value(currentValue)
            .percent(getPercent(currentValue, total))
            .title(title)
            .upDown(getUpDown(changeCalculateValue))
            .upDownText(periodType.getCompareTextType().getDisplayName())
            .upDownPercent(getPercent(changeCalculateValue, pastValue));
    }

    private String getPercent(Long changeValue, Long total) {
        if (changeValue <= 0 && total <= 0) {
            return "0%";
        } else if (total <= 0) {
            return "100%";
        }
        return "%d%s".formatted(changeValue * 100 / total, "%");
    }

    private String getUpDown(long calculate) {
        switch (Long.compare(calculate, 0)) {
            case 0:
                return "-";
            case 1:
                return UpDownType.UP.getDisplayName();
            default:
                return UpDownType.DOWN.getDisplayName();
        }
    }
}
