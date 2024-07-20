package ag.act.facade.admin;

import ag.act.entity.StockReferenceDate;
import ag.act.entity.UserHoldingStock;
import ag.act.entity.UserHoldingStockOnReferenceDate;
import ag.act.facade.admin.stock.AdminDummyStockFacade;
import ag.act.model.Status;
import ag.act.service.stock.StockReferenceDateService;
import ag.act.service.user.UserHoldingStockOnReferenceDateService;
import ag.act.service.user.UserHoldingStockService;
import ag.act.validator.AdminDummyStockValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;

import static ag.act.TestUtil.someLocalDate;
import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static shiver.me.timbers.data.random.RandomLongs.someLong;

@MockitoSettings(strictness = Strictness.LENIENT)
class AdminDummyStockFacadeTest {

    @InjectMocks
    private AdminDummyStockFacade facade;
    @Mock
    private UserHoldingStockService userHoldingStockService;
    @Mock
    private UserHoldingStockOnReferenceDateService userHoldingStockOnReferenceDateService;
    @Mock
    private StockReferenceDateService stockReferenceDateService;
    @Mock
    private AdminDummyStockValidator adminDummyStockValidator;

    @Nested
    class AddDummyStockToUser {

        @Mock
        private ag.act.model.AddDummyStockToUserRequest request;
        @Mock
        private StockReferenceDate stockReferenceDate;
        private Long userId;
        private String stockCode;
        private LocalDate referenceDate;
        @Captor
        private ArgumentCaptor<UserHoldingStock> userHoldingStockCaptor;
        @Captor
        private ArgumentCaptor<UserHoldingStockOnReferenceDate> userHoldingStockOnReferenceDateCaptor;

        @BeforeEach
        void setUp() {
            userId = someLong();
            stockCode = someStockCode();
            final Long stockReferenceDateId = someLong();
            referenceDate = someLocalDate();

            given(request.getStockCode()).willReturn(stockCode);
            given(request.getStockReferenceDateId()).willReturn(stockReferenceDateId);
            given(stockReferenceDateService.getStockReferenceDate(stockReferenceDateId))
                .willReturn(stockReferenceDate);
            willDoNothing().given(adminDummyStockValidator).validateStockReferenceDate(stockCode, stockReferenceDate);
            willDoNothing().given(adminDummyStockValidator).validateAdminActiveUser(userId);
            willDoNothing().given(adminDummyStockValidator).validateStockAlreadyExists(userId, stockCode);
            given(stockReferenceDate.getReferenceDate()).willReturn(referenceDate);

            given(userHoldingStockService.save(any()))
                .willAnswer(invocation -> invocation.getArgument(0));
            given(userHoldingStockOnReferenceDateService.save(any()))
                .willAnswer(invocation -> invocation.getArgument(0));
        }

        @Nested
        class AddDummyStockToUserSuccessfully {

            @BeforeEach
            void setUp() {
                facade.addDummyStockToUser(userId, request);
            }

            @Test
            void shouldSaveUserHoldingStockWithUserId() {
                UserHoldingStock actualUserHoldingStock = getCapturedUserHoldingStock();

                assertThat(actualUserHoldingStock.getUserId(), is(userId));
            }

            @Test
            void shouldSaveUserHoldingStockWithStockCode() {
                UserHoldingStock actualUserHoldingStock = getCapturedUserHoldingStock();

                assertThat(actualUserHoldingStock.getStockCode(), is(stockCode));
            }

            @Test
            void shouldSaveUserHoldingStockWithDefaultQuantity() {
                UserHoldingStock actualUserHoldingStock = getCapturedUserHoldingStock();

                assertThat(actualUserHoldingStock.getQuantity(), is(500L));
            }

            @Test
            void shouldSaveUserHoldingStockOnReferenceDateWithUserId() {
                UserHoldingStockOnReferenceDate actualUserHoldingStockOnReferenceDate = getCapturedUserHoldingStockOnReferenceDate();
                assertThat(actualUserHoldingStockOnReferenceDate.getUserId(), is(userId));
            }

            @Test
            void shouldSaveUserHoldingStockOnReferenceDateWithStockCode() {
                UserHoldingStockOnReferenceDate actualUserHoldingStockOnReferenceDate = getCapturedUserHoldingStockOnReferenceDate();
                assertThat(actualUserHoldingStockOnReferenceDate.getStockCode(), is(stockCode));
            }

            @Test
            void shouldSaveUserHoldingStockOnReferenceDateWithDefaultQuantity() {
                UserHoldingStockOnReferenceDate actualUserHoldingStockOnReferenceDate = getCapturedUserHoldingStockOnReferenceDate();
                assertThat(actualUserHoldingStockOnReferenceDate.getQuantity(), is(500L));
            }

            @Test
            void shouldSaveUserHoldingStockOnReferenceDateWithReferenceDate() {
                UserHoldingStockOnReferenceDate actualUserHoldingStockOnReferenceDate = getCapturedUserHoldingStockOnReferenceDate();
                assertThat(actualUserHoldingStockOnReferenceDate.getReferenceDate(), is(referenceDate));
            }

            @Test
            void shouldSaveUserHoldingStockOnReferenceDateWithStatus() {
                UserHoldingStockOnReferenceDate actualUserHoldingStockOnReferenceDate = getCapturedUserHoldingStockOnReferenceDate();
                assertThat(actualUserHoldingStockOnReferenceDate.getStatus(), is(Status.ACTIVE));
            }

            private UserHoldingStock getCapturedUserHoldingStock() {
                then(userHoldingStockService).should().save(userHoldingStockCaptor.capture());
                return userHoldingStockCaptor.getValue();
            }

            private UserHoldingStockOnReferenceDate getCapturedUserHoldingStockOnReferenceDate() {
                then(userHoldingStockOnReferenceDateService).should().save(userHoldingStockOnReferenceDateCaptor.capture());
                return userHoldingStockOnReferenceDateCaptor.getValue();
            }
        }
    }

    @Nested
    class DeleteDummyStockFromUser {

        @Mock
        private ag.act.model.DeleteDummyStockFromUserRequest request;
        private Long userId;
        private String stockCode;

        @BeforeEach
        void setUp() {
            userId = someLong();
            stockCode = someStockCode();

            given(request.getStockCode()).willReturn(stockCode);
            willDoNothing().given(adminDummyStockValidator).validateAdminActiveUser(userId);
            willDoNothing().given(adminDummyStockValidator).validateDummyStock(userId, stockCode);
        }

        @Nested
        class DeleteDummyStockFromUserSuccessfully {

            @BeforeEach
            void setUp() {
                facade.deleteDummyStockToUser(userId, request);
            }

            @Test
            void shouldCallDeleteUserHoldingStock() {
                then(userHoldingStockService).should().deleteDummyUserHoldingStock(userId, stockCode);
            }

            @Test
            void shouldCallDeleteUserHoldingStockOnReferenceDate() {
                then(userHoldingStockOnReferenceDateService).should().deleteUserHoldingStockOnReferenceDate(userId, stockCode);
            }
        }
    }
}
