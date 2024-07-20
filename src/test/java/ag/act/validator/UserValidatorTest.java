package ag.act.validator;

import ag.act.core.configuration.GlobalBoardManager;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.exception.DeletedUserException;
import ag.act.exception.NotHaveStockException;
import ag.act.model.Status;
import ag.act.validator.user.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.stream.Stream;

import static ag.act.TestUtil.assertException;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static shiver.me.timbers.data.random.RandomStrings.someString;
import static shiver.me.timbers.data.random.RandomThings.someThing;

@MockitoSettings(strictness = Strictness.LENIENT)
class UserValidatorTest {

    @InjectMocks
    private UserValidator validator;
    @Mock
    private GlobalBoardManager globalBoardManager;

    private String stockCode;
    @Mock
    private User user;

    @BeforeEach
    void setUp() {
        given(globalBoardManager.isGlobalStockCode(stockCode)).willReturn(false);
    }

    @Nested
    class ValidateStatus {
        @Nested
        class WhenUserIsNotDeleted {
            @Test
            void shouldNotThrowAnyException() {

                // Given
                given(user.getStatus()).willReturn(someThing(Status.ACTIVE, Status.INACTIVE_BY_USER, Status.INACTIVE_BY_ADMIN));

                // When // Then
                validator.validateStatus(user);
            }
        }

        @Nested
        class WhenUserIsDeleted {
            @ParameterizedTest(name = "{index} => status=''{0}''")
            @MethodSource("statusProvider")
            void shouldThrowDeletedUserException(Status status) {

                // Given
                given(user.getStatus()).willReturn(status);

                // When // Then
                assertException(
                    DeletedUserException.class,
                    () -> validator.validateStatus(user),
                    "탈퇴한 회원입니다."
                );
            }

            private static Stream<Arguments> statusProvider() {
                return Stream.of(
                    Arguments.of(Status.DELETED_BY_USER),
                    Arguments.of(Status.DELETED_BY_ADMIN),
                    Arguments.of(Status.WITHDRAWAL_REQUESTED)
                );
            }
        }
    }

    @Nested
    class ValidateHavingStock {
        @Mock
        private UserHoldingStock userHoldingStock;

        @BeforeEach
        void setUp() {
            stockCode = someString(5);
        }

        @Nested
        class WhenUserIsNotDeleted {
            @Test
            void shouldNotThrowAnyException() {

                // Given
                given(user.getUserHoldingStocks()).willReturn(List.of(userHoldingStock));
                given(userHoldingStock.getStockCode()).willReturn(stockCode);

                // When // Then
                validator.validateHavingStock(user, stockCode);
            }
        }

        @Nested
        class WhenUserDoesNotHaveStock {
            @Test
            void shouldThrowNotHaveStockException() {

                // Given
                given(user.getUserHoldingStocks()).willReturn(List.of(userHoldingStock));
                given(userHoldingStock.getStockCode()).willReturn(stockCode);

                // When // Then
                assertException(
                    NotHaveStockException.class,
                    () -> validator.validateHavingStock(user, someString(5)),
                    "현재 주식을 보유하지 않은 회원입니다."
                );
            }
        }

        @Nested
        class WhenStockIsForGlobalBoard {
            @BeforeEach
            void setUp() {
                given(globalBoardManager.isGlobalStockCode(stockCode)).willReturn(true);

                validator.validateHavingStock(user, stockCode);
            }

            @Test
            void shouldNotThrowAnyException() {
                then(globalBoardManager).should().isGlobalStockCode(stockCode);
            }

            @Test
            void shouldNotCallUserHoldingStock() {
                then(user).shouldHaveNoInteractions();
            }
        }
    }
}
