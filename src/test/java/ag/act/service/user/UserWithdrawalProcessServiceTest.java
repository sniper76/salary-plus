package ag.act.service.user;

import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.enums.admin.DashboardStatisticsType;
import ag.act.module.mydata.MyDataSummaryService;
import ag.act.service.NicknameHistoryService;
import ag.act.service.admin.dashboard.DashboardStockStatisticsService;
import ag.act.service.digitaldocument.DigitalDocumentUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class UserWithdrawalProcessServiceTest {

    @InjectMocks
    private UserWithdrawalProcessService service;
    @Mock
    private UserService userService;
    @Mock
    private UserRoleService userRoleService;
    @Mock
    private UserHoldingStockService userHoldingStockService;
    @Mock
    private UserHoldingStockOnReferenceDateService userHoldingStockOnReferenceDateService;
    @Mock
    private MyDataSummaryService myDataSummaryService;
    @Mock
    private DigitalDocumentUserService digitalDocumentUserService;
    @Mock
    private NicknameHistoryService nicknameHistoryService;
    @Mock
    private DashboardStockStatisticsService dashboardStockStatisticsService;
    @Mock
    private User user;

    @Nested
    class WithdrawUserWhenUserHoldingStockIsNull {

        private Long userId;
        @Mock
        private DigitalDocumentUser digitalDocumentUser;

        @SuppressWarnings("unchecked")
        @BeforeEach
        void setUp() {
            userId = someLong();

            given(user.getId()).willReturn(userId);
            given(userService.withdrawUser(user, ag.act.model.Status.DELETED_BY_USER)).willReturn(user);
            given(userHoldingStockService.findAllByUserIdAndStatusActive(userId)).willReturn(Collections.EMPTY_LIST);
            given(digitalDocumentUserService.findAllByUserId(userId)).willReturn(List.of(digitalDocumentUser));

            service.withdrawUser(user, ag.act.model.Status.DELETED_BY_USER);
        }

        @Test
        void shouldCallUserServiceWithdrawUser() {
            then(userService).should().withdrawUser(user, ag.act.model.Status.DELETED_BY_USER);
        }

        @Test
        void shouldCallUserHoldingStockService() {
            then(userHoldingStockService).should().deleteAllByUserId(userId);
        }

        @Test
        void shouldCallUserHoldingStockOnReferenceDateService() {
            then(userHoldingStockOnReferenceDateService).should().deleteAllByUserId(userId);
        }

        @Test
        void shouldCallUserRoleService() {
            then(userRoleService).should().deleteAll(userId);
        }

        @Test
        void shouldCallMyDataSummaryService() {
            then(myDataSummaryService).should().deleteAllByUserId(userId);
        }

        @Test
        void shouldCallDigitalDocumentUserService() {
            then(digitalDocumentUserService).should().findAllByUserId(userId);
        }

        @Test
        void shouldCallNicknameHistoryService() {
            then(nicknameHistoryService).should().deleteAllByUserId(userId);
        }

        @Test
        void shouldCallDigitalDocumentUserServiceForDeleteAll() {
            then(digitalDocumentUserService).should().deleteUserDigitalDocument(digitalDocumentUser);
        }
    }

    @Nested
    class WithdrawUserWhenUserHoldingStockIsNotNull {

        private Long userId;
        private String stockCode;
        @Mock
        private DigitalDocumentUser digitalDocumentUser;

        @Mock
        private UserHoldingStock userHoldingStock;

        @BeforeEach
        void setUp() {
            userId = someLong();
            stockCode = someString(5);

            given(user.getId()).willReturn(userId);
            given(userService.withdrawUser(user, ag.act.model.Status.DELETED_BY_USER)).willReturn(user);
            given(userHoldingStockService.findAllByUserIdAndStatusActive(userId)).willReturn(List.of(userHoldingStock));
            given(userHoldingStock.getStockCode()).willReturn(stockCode);
            given(digitalDocumentUserService.findAllByUserId(userId)).willReturn(List.of(digitalDocumentUser));

            service.withdrawUser(user, ag.act.model.Status.DELETED_BY_USER);
        }

        @Test
        void shouldCallUserServiceWithdrawUser() {
            then(userService).should().withdrawUser(user, ag.act.model.Status.DELETED_BY_USER);
        }

        @Test
        void shouldCallUserHoldingStockService() {
            then(userHoldingStockService).should().deleteAllByUserId(userId);
        }

        @Test
        void shouldCallUserHoldingStockOnReferenceDateService() {
            then(userHoldingStockOnReferenceDateService).should().deleteAllByUserId(userId);
        }

        @Test
        void shouldCallDashboardStockStatisticsService() {
            verify(dashboardStockStatisticsService, times(1))
                .upsertStockUserWithdrawalCount(stockCode, DashboardStatisticsType.DAILY_STOCK_USER_WITHDRAWAL_COUNT);
        }

        @Test
        void shouldCallUserRoleService() {
            then(userRoleService).should().deleteAll(userId);
        }

        @Test
        void shouldCallMyDataSummaryService() {
            then(myDataSummaryService).should().deleteAllByUserId(userId);
        }

        @Test
        void shouldCallDigitalDocumentUserService() {
            then(digitalDocumentUserService).should().findAllByUserId(userId);
        }

        @Test
        void shouldCallNicknameHistoryService() {
            then(nicknameHistoryService).should().deleteAllByUserId(userId);
        }

        @Test
        void shouldCallDigitalDocumentUserServiceForDeleteAll() {
            then(digitalDocumentUserService).should().deleteUserDigitalDocument(digitalDocumentUser);
        }
    }
}
