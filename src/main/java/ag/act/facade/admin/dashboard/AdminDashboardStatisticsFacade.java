package ag.act.facade.admin.dashboard;

import ag.act.dto.admin.DashboardStatisticsParamDto;
import ag.act.model.DashboardStatisticsAgeDataResponse;
import ag.act.model.DashboardStatisticsDataResponse;
import ag.act.model.DashboardStatisticsDataResponseSearch;
import ag.act.model.DashboardStatisticsGenderDataResponse;
import ag.act.service.admin.dashboard.DashboardAgeStatisticsService;
import ag.act.service.admin.dashboard.DashboardGenderStatisticsService;
import ag.act.service.admin.dashboard.DashboardStatisticsService;
import ag.act.service.admin.dashboard.DashboardStockStatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
@Slf4j
public class AdminDashboardStatisticsFacade {
    private final DashboardStatisticsService dashboardStatisticsService;
    private final DashboardStockStatisticsService dashboardStockStatisticsService;
    private final DashboardAgeStatisticsService dashboardAgeStatisticsService;
    private final DashboardGenderStatisticsService dashboardGenderStatisticsService;

    public DashboardStatisticsDataResponse getStatistics(DashboardStatisticsParamDto paramDto) {
        return new DashboardStatisticsDataResponse().data(
            Stream.concat(
                dashboardStatisticsService.getStatistics(paramDto).stream(),
                dashboardStockStatisticsService.getStatistics(paramDto).stream()
            ).toList()
        ).search(getSearch(paramDto));
    }

    private ag.act.model.DashboardStatisticsDataResponseSearch getSearch(DashboardStatisticsParamDto paramDto) {
        return new DashboardStatisticsDataResponseSearch()
            .from(paramDto.getSearchFrom())
            .to(paramDto.getSearchTo())
            .period(paramDto.getPeriod());
    }

    public DashboardStatisticsAgeDataResponse getStatisticsAge(DashboardStatisticsParamDto paramDto) {
        return new DashboardStatisticsAgeDataResponse().data(
            dashboardAgeStatisticsService.getStatistics(paramDto)
        ).search(getSearch(paramDto));
    }

    public DashboardStatisticsGenderDataResponse getStatisticsGender(DashboardStatisticsParamDto paramDto) {
        return new DashboardStatisticsGenderDataResponse().data(
            dashboardGenderStatisticsService.getStatistics(paramDto)
        ).search(getSearch(paramDto));
    }
}
