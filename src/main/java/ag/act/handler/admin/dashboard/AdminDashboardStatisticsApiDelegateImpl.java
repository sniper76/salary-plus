package ag.act.handler.admin.dashboard;

import ag.act.api.AdminDashboardStatisticsApiDelegate;
import ag.act.core.guard.IsAdminGuard;
import ag.act.core.guard.UseGuards;
import ag.act.dto.admin.DashboardStatisticsParamDto;
import ag.act.facade.admin.dashboard.AdminDashboardStatisticsFacade;
import ag.act.model.DashboardStatisticsAgeDataResponse;
import ag.act.model.DashboardStatisticsDataResponse;
import ag.act.model.DashboardStatisticsGenderDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@UseGuards(IsAdminGuard.class)
@SuppressWarnings("checkstyle:MemberName")
public class AdminDashboardStatisticsApiDelegateImpl implements AdminDashboardStatisticsApiDelegate {
    private static final int PERIOD_SEVEN = 7;
    private static final int PERIOD_TWO = 2;
    private final AdminDashboardStatisticsFacade adminDashboardStatisticsFacade;

    @Override
    public ResponseEntity<DashboardStatisticsDataResponse> getStatistics(
        String type, String stockCode, String periodType, String searchFrom, String searchTo
    ) {
        return ResponseEntity.ok(adminDashboardStatisticsFacade.getStatistics(
            new DashboardStatisticsParamDto(type, stockCode, periodType, PERIOD_SEVEN, searchFrom, searchTo)
        ));
    }

    @Override
    public ResponseEntity<DashboardStatisticsAgeDataResponse> getStatisticsAge(
        String periodType, String searchFrom, String searchTo
    ) {
        return ResponseEntity.ok(adminDashboardStatisticsFacade.getStatisticsAge(
            new DashboardStatisticsParamDto(periodType, PERIOD_TWO, searchFrom, searchTo)
        ));
    }

    @Override
    public ResponseEntity<DashboardStatisticsGenderDataResponse> getStatisticsGender(
        String periodType, String searchFrom, String searchTo
    ) {
        return ResponseEntity.ok(adminDashboardStatisticsFacade.getStatisticsGender(
            new DashboardStatisticsParamDto(periodType, PERIOD_TWO, searchFrom, searchTo)
        ));
    }
}
