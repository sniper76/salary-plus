package ag.act.dto.admin;

import ag.act.enums.admin.DashboardStatisticsType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatisticsCountDto {
    private DashboardStatisticsType type;
    private String date;
    private Double value;

    public BigDecimal getBigDecimalValue() {
        return new BigDecimal(value);
    }
}
