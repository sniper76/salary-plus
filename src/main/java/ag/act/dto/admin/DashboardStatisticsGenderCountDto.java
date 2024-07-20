package ag.act.dto.admin;

import ag.act.enums.admin.DashboardStatisticsType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatisticsGenderCountDto {
    private DashboardStatisticsType type;
    private String date;
    private Long maleValue;
    private Long femaleValue;

    public static DashboardStatisticsGenderCountDto empty() {
        return new DashboardStatisticsGenderCountDto(
            DashboardStatisticsType.DAILY_USER_GENDER_COUNT,
            null,
            0L,
            0L
        );
    }

    public Long getTotal() {
        return maleValue + femaleValue;
    }
}
