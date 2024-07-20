package ag.act.converter.dashboard;

import ag.act.enums.admin.DashboardStatisticsPeriodType;
import ag.act.enums.admin.UpDownType;
import ag.act.model.DashboardStatisticsResponseSummary;
import org.springframework.stereotype.Component;

@Component
public class DashboardStatisticsResponseSummaryConverter {
    public DashboardStatisticsResponseSummary convert(
        DashboardStatisticsPeriodType periodType,
        Double currentValue,
        Double pastValue
    ) {
        double changeCalculateValue = currentValue - pastValue;
        return new DashboardStatisticsResponseSummary()
            .upDownText(periodType.getCompareTextType().getDisplayName())
            .upDownPercent(getPercent(changeCalculateValue, pastValue))
            .upDown(getUpDown(changeCalculateValue));
    }

    private String getPercent(Double changeValue, Double total) {
        return "%d%s".formatted(Math.round(changeValue * 100 / Math.max(1, total)), "%");
    }

    private String getUpDown(double calculate) {
        return switch (Double.compare(calculate, 0)) {
            case 0 -> "-";
            case 1 -> UpDownType.UP.getDisplayName();
            default -> UpDownType.DOWN.getDisplayName();
        };
    }
}
