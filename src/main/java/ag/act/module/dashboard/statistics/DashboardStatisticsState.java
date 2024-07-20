package ag.act.module.dashboard.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatisticsState {
    private String name;
    private boolean isSuccess;
    private String failureReason;

    public DashboardStatisticsState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "name='" + name + '\'' + ", isSuccess=" + isSuccess + ", failureErrorReason='" + failureReason + '\'';
    }
}
