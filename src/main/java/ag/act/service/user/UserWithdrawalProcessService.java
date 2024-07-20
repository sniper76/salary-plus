package ag.act.service.user;

import ag.act.entity.User;
import ag.act.enums.admin.DashboardStatisticsType;
import ag.act.model.Status;
import ag.act.module.mydata.MyDataSummaryService;
import ag.act.service.NicknameHistoryService;
import ag.act.service.admin.dashboard.DashboardStockStatisticsService;
import ag.act.service.digitaldocument.DigitalDocumentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserWithdrawalProcessService {
    private final UserService userService;
    private final UserRoleService userRoleService;
    private final UserHoldingStockService userHoldingStockService;
    private final UserHoldingStockOnReferenceDateService userHoldingStockOnReferenceDateService;
    private final MyDataSummaryService myDataSummaryService;
    private final DigitalDocumentUserService digitalDocumentUserService;
    private final NicknameHistoryService nicknameHistoryService;
    private final DashboardStockStatisticsService dashboardStockStatisticsService;

    public void withdrawUser(User user, Status status) {
        final Long userId = user.getId();

        userService.withdrawUser(user, status);
        userHoldingStockService.findAllByUserIdAndStatusActive(userId).forEach(userHoldingStock -> {
            final String stockCode = userHoldingStock.getStockCode();
            dashboardStockStatisticsService.upsertStockUserWithdrawalCount(stockCode, DashboardStatisticsType.DAILY_STOCK_USER_WITHDRAWAL_COUNT);
        });
        userHoldingStockService.deleteAllByUserId(userId);
        userHoldingStockOnReferenceDateService.deleteAllByUserId(userId);
        userRoleService.deleteAll(userId);
        myDataSummaryService.deleteAllByUserId(userId);
        nicknameHistoryService.deleteAllByUserId(userId);

        digitalDocumentUserService.findAllByUserId(userId).forEach(digitalDocumentUserService::deleteUserDigitalDocument);
    }
}
