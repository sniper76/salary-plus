package ag.act.module.dashboard.statistics;

import ag.act.enums.admin.DashboardStatisticsType;
import ag.act.service.admin.dashboard.DashboardAgeStatisticsService;
import ag.act.service.admin.dashboard.DashboardGenderStatisticsService;
import ag.act.service.admin.dashboard.DashboardStatisticsService;
import ag.act.service.admin.dashboard.DashboardStockStatisticsService;
import ag.act.util.KoreanDateTimeUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class DashboardStatisticsProcessor {
    private final DashboardStatisticsService dashboardStatisticsService;
    private final DashboardStockStatisticsService dashboardStockStatisticsService;
    private final DashboardAgeStatisticsService dashboardAgeStatisticsService;
    private final DashboardGenderStatisticsService dashboardGenderStatisticsService;

    public void processBatch(DashboardStatisticsStateCollector dashboardStatisticsStateCollector) {
        log.info("DashboardStatisticsProcessor startTime : {}", KoreanDateTimeUtil.getYesterdayStartDateTime());
        log.info("DashboardStatisticsProcessor endTime : {}", KoreanDateTimeUtil.getYesterdayEndDateTime());
        Stream.of(
                this.saveUserActiveMyDataAccessCount(),
                this.saveUserActiveMyDataAccessAccumulate(),
                this.saveUserStatusProcessingCount(),
                this.saveUserStatusActiveCount(),
                this.saveUserStatusWithdrawalCount(),
                this.saveUserRegistrationCount(),
                this.saveUserWithdrawalCount(),
                this.saveTotalAsset(),
                this.saveTotalPostViewCount(),
                this.saveUserLoginCount(),
                this.saveDailyActiveUser(),
                this.saveMonthlyActiveUser(),
                this.saveUserAverageLogin(),
                this.saveReuseAverageLogin(),
                this.saveDailyUserRegistrationAverageRate(),
                this.saveDailyAssetAverageRate(),
                this.saveDailyUserGenderCount(),
                this.saveDailyUserAgeCount(),
                this.saveDailyPostCount(),
                this.saveDailyCommentCount(),
                this.saveDailyLikedCount()
            ).parallel()
            .map(Supplier::get)
            .forEach(dashboardStatisticsStateCollector::addState);
    }

    private Supplier<DashboardStatisticsState> saveTotalAsset() {
        return () -> withState(
            DashboardStatisticsType.DAILY_TOTAL_ASSET_PRICE,
            dashboardStatisticsService::saveTotalAsset
        );
    }

    private Supplier<DashboardStatisticsState> saveDailyUserAgeCount() {
        return () -> withState(
            DashboardStatisticsType.DAILY_USER_AGE_COUNT,
            dashboardAgeStatisticsService::saveDailyUserAgeCount
        );
    }

    private Supplier<DashboardStatisticsState> saveDailyUserGenderCount() {
        return () -> withState(
            DashboardStatisticsType.DAILY_USER_GENDER_COUNT,
            dashboardGenderStatisticsService::saveDailyUserGenderCount
        );
    }

    private Supplier<DashboardStatisticsState> saveUserActiveMyDataAccessCount() {
        return () -> withState(
            DashboardStatisticsType.DAILY_USER_ACTIVE_MY_DATA_ACCESS_COUNT,
            dashboardStatisticsService::saveUserActiveMyDataAccessCount
        );
    }

    private Supplier<DashboardStatisticsState> saveUserActiveMyDataAccessAccumulate() {
        return () -> withState(
            DashboardStatisticsType.DAILY_USER_ACTIVE_MY_DATA_ACCESS_ACCUMULATE,
            dashboardStatisticsService::saveUserActiveMyDataAccessAccumulate
        );
    }

    private Supplier<DashboardStatisticsState> saveUserStatusProcessingCount() {
        return () -> withState(
            DashboardStatisticsType.DAILY_USER_STATUS_PROCESSING_COUNT,
            dashboardStatisticsService::saveUserStatusProcessingCount
        );
    }

    private Supplier<DashboardStatisticsState> saveUserStatusActiveCount() {
        return () -> withState(
            DashboardStatisticsType.DAILY_USER_STATUS_ACTIVE_COUNT,
            dashboardStatisticsService::saveUserStatusActiveCount
        );
    }

    private Supplier<DashboardStatisticsState> saveUserStatusWithdrawalCount() {
        return () -> withState(
            DashboardStatisticsType.DAILY_USER_STATUS_WITHDRAWAL_COUNT,
            dashboardStatisticsService::saveUserStatusWithdrawalCount
        );
    }

    private Supplier<DashboardStatisticsState> saveUserRegistrationCount() {
        return () -> withState(
            DashboardStatisticsType.DAILY_USER_REGISTRATION_COUNT,
            dashboardStatisticsService::saveUserRegistrationCount
        );
    }

    private Supplier<DashboardStatisticsState> saveUserWithdrawalCount() {
        return () -> withState(
            DashboardStatisticsType.DAILY_USER_WITHDRAWAL_COUNT,
            dashboardStatisticsService::saveUserWithdrawalCount
        );
    }

    private Supplier<DashboardStatisticsState> saveTotalPostViewCount() {
        return () -> withState(
            DashboardStatisticsType.DAILY_POST_VIEW_COUNT,
            dashboardStatisticsService::saveTotalPostViewCount
        );
    }

    private Supplier<DashboardStatisticsState> saveUserLoginCount() {
        return () -> withState(
            DashboardStatisticsType.DAILY_USER_LOGIN_COUNT,
            dashboardStatisticsService::saveUserLoginCount
        );
    }

    private Supplier<DashboardStatisticsState> saveDailyActiveUser() {
        return () -> withState(
            DashboardStatisticsType.DAILY_ACTIVE_USER,
            dashboardStatisticsService::saveDailyActiveUser
        );
    }

    private Supplier<DashboardStatisticsState> saveMonthlyActiveUser() {
        return () -> withState(
            DashboardStatisticsType.MONTHLY_ACTIVE_USER,
            dashboardStatisticsService::saveMonthlyActiveUser
        );
    }

    private Supplier<DashboardStatisticsState> saveUserAverageLogin() {
        return () -> withState(
            DashboardStatisticsType.DAILY_USER_ACCESS_PIN_NUMBER_COUNT,
            dashboardStatisticsService::saveUserAverageLogin
        );
    }

    private Supplier<DashboardStatisticsState> saveReuseAverageLogin() {
        return () -> withState(
            DashboardStatisticsType.DAILY_USER_REUSE_RATE,
            dashboardStatisticsService::saveReuseAverageLogin
        );
    }

    private Supplier<DashboardStatisticsState> saveDailyUserRegistrationAverageRate() {
        return () -> withState(
            DashboardStatisticsType.DAILY_STOCK_MEMBER_COUNT,
            dashboardStockStatisticsService::saveDailyUserRegistrationAverageRate
        );
    }

    private Supplier<DashboardStatisticsState> saveDailyAssetAverageRate() {
        return () -> withState(
            DashboardStatisticsType.DAILY_STOCK_USER_HOLDING_COUNT,
            dashboardStockStatisticsService::saveDailyAssetAverageRate
        );
    }

    private Supplier<DashboardStatisticsState> saveDailyPostCount() {
        return () -> withState(
            DashboardStatisticsType.DAILY_STOCK_POST_COUNT,
            dashboardStockStatisticsService::saveDailyPostCount
        );
    }

    private Supplier<DashboardStatisticsState> saveDailyCommentCount() {
        return () -> withState(
            DashboardStatisticsType.DAILY_STOCK_COMMENT_COUNT,
            dashboardStockStatisticsService::saveDailyCommentCount
        );
    }

    private Supplier<DashboardStatisticsState> saveDailyLikedCount() {
        return () -> withState(
            DashboardStatisticsType.DAILY_STOCK_LIKED_COUNT,
            dashboardStockStatisticsService::saveDailyLikedCount
        );
    }

    private DashboardStatisticsState withState(DashboardStatisticsType type, Consumer<DashboardStatisticsType> consumer) {
        DashboardStatisticsState state = new DashboardStatisticsState(type.name());
        try {
            consumer.accept(type);
            state.setSuccess(true);
        } catch (Exception e) {
            state.setSuccess(false);
            state.setFailureReason(e.getMessage());
            log.error("DashboardStatisticsState Error: {}", type, e);
        }
        return state;
    }
}
