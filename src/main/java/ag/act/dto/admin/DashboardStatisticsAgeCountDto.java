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
public class DashboardStatisticsAgeCountDto {
    private DashboardStatisticsType type;
    private String date;
    private Long age10Value;
    private Long age20Value;
    private Long age30Value;
    private Long age40Value;
    private Long age50Value;
    private Long age60Value;
    private Long age70Value;
    private Long age80Value;
    private Long age90Value;

    public Long getAge70AndOver() {
        return age70Value + age80Value + age90Value;
    }

    public static DashboardStatisticsAgeCountDto empty() {
        return new DashboardStatisticsAgeCountDto(
            DashboardStatisticsType.DAILY_USER_AGE_COUNT,
            null,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L
        );
    }

    public Long getTotal() {
        return age10Value
            + age20Value
            + age30Value
            + age40Value
            + age50Value
            + age60Value
            + age70Value
            + age80Value
            + age90Value;
    }
}
