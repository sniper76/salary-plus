package ag.act.enums.admin;

import lombok.Getter;

import java.time.ZonedDateTime;

import static ag.act.enums.admin.CompareTextType.COMPARE_DAY;
import static ag.act.enums.admin.CompareTextType.COMPARE_MONTH;

@Getter
public enum DashboardStatisticsPeriodType {
    DAILY("일별", COMPARE_DAY) {
        @Override
        public String getPeriodFormat() {
            return "yyyy-MM-dd";
        }

        @Override
        public ZonedDateTime plus(ZonedDateTime zonedDateTime) {
            return zonedDateTime.plusDays(1);
        }
    },
    MONTHLY("월별", COMPARE_MONTH) {
        @Override
        public String getPeriodFormat() {
            return "yyyy-MM";
        }

        @Override
        public ZonedDateTime plus(ZonedDateTime zonedDateTime) {
            return zonedDateTime.plusMonths(1);
        }
    };

    private final String displayName;
    private final CompareTextType compareTextType;

    DashboardStatisticsPeriodType(String displayName, CompareTextType compareTextType) {
        this.displayName = displayName;
        this.compareTextType = compareTextType;
    }

    public static DashboardStatisticsPeriodType fromValue(String type) {
        try {
            return DashboardStatisticsPeriodType.valueOf(type.toUpperCase());
        } catch (Exception e) {
            return DAILY;
        }
    }

    public abstract String getPeriodFormat();

    public abstract ZonedDateTime plus(ZonedDateTime zonedDateTime);
}
