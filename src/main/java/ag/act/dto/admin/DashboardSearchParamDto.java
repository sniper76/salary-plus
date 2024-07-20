package ag.act.dto.admin;

import ag.act.enums.admin.DashboardStatisticsType;
import lombok.Getter;

import java.util.List;

@Getter
public class DashboardSearchParamDto {
    private final List<DashboardStatisticsType> dashboardStatisticsTypeList;
    private final String startPeriod;
    private final String endPeriod;
    private final String stockCode;
    private final List<String> dateList;

    public DashboardSearchParamDto(
        List<DashboardStatisticsType> typeList, String startPeriod, String endPeriod, String stockCode, List<String> dateList
    ) {
        this.dashboardStatisticsTypeList = typeList;
        this.startPeriod = startPeriod;
        this.endPeriod = endPeriod;
        this.stockCode = stockCode;
        this.dateList = dateList;
    }

    public DashboardSearchParamDto(List<DashboardStatisticsType> typeList, String startPeriod, String endPeriod, String stockCode) {
        this(typeList, startPeriod, endPeriod, stockCode, List.of());
    }

    public DashboardSearchParamDto(List<DashboardStatisticsType> typeList, String startPeriod, String endPeriod) {
        this(typeList, startPeriod, endPeriod, null, List.of());
    }

    public DashboardSearchParamDto(List<DashboardStatisticsType> typeList, List<String> dateList) {
        this(typeList, null, null, null, dateList);
    }
}
