package ag.act.service.user;

import ag.act.entity.MyDataSummary;
import ag.act.entity.User;
import ag.act.exception.BadRequestException;
import ag.act.facade.auth.AuthFacade;
import ag.act.module.mydata.MyDataSummaryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static ag.act.TestUtil.assertException;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class UserWithdrawalRequestServiceTest {

    @InjectMocks
    private UserWithdrawalRequestService service;
    @Mock
    private UserService userService;
    @Mock
    private AuthFacade authFacade;
    @Mock
    private MyDataSummaryService myDataSummaryService;
    @Mock
    private UserWithdrawalRequestValidator userWithdrawalRequestValidator;
    @Mock
    private User user;
    private Long userId;

    @BeforeEach
    void setUp() {
        userId = someLong();

        given(user.getId()).willReturn(userId);
        given(userService.getUser(userId)).willReturn(user);
    }

    @Nested
    class RequestWithdrawal {

        @BeforeEach
        void setUp() {
            willDoNothing().given(userWithdrawalRequestValidator).validate(user);
            willDoNothing().given(userService).withdrawRequest(user);
            willDoNothing().given(authFacade).withdrawMyDataService(user);
        }

        @Nested
        class WhenNormalUser {

            @Nested
            class AndUserHasMyDataSummary extends DefaultTestCases {
                @Mock
                private MyDataSummary myDataSummary;

                @BeforeEach
                void setUp() {
                    given(myDataSummaryService.findByUserId(userId)).willReturn(Optional.of(myDataSummary));

                    service.requestWithdrawal(userId);
                }

                @Test
                void shouldCallAuthFacadeWithdrawMyDataService() {
                    then(authFacade).should().withdrawMyDataService(user);
                }

            }

            @Nested
            class AndUserDoesNotHaveMyDataSummary extends DefaultTestCases {
                @BeforeEach
                void setUp() {
                    given(myDataSummaryService.findByUserId(userId)).willReturn(Optional.empty());

                    service.requestWithdrawal(userId);
                }

                @Test
                void shouldCallAuthFacadeWithdrawMyDataService() {
                    then(authFacade).should(never()).withdrawMyDataService(user);
                }
            }

            @SuppressWarnings({"unused", "JUnitMalformedDeclaration"})
            class DefaultTestCases {

                @Test
                void shouldCallSolidarityLeaderServiceToCheckIfUserIsLeader() {
                    then(userWithdrawalRequestValidator).should().validate(user);
                }

                @Test
                void shouldCallUserServiceWithdraw() {
                    then(userService).should().withdrawRequest(user);
                }
            }
        }

        @Nested
        class WhenValidationFailed {

            private String errorMessage;

            @BeforeEach
            void setUp() {
                errorMessage = someString(10);

                doThrow(new BadRequestException(errorMessage))
                    .when(userWithdrawalRequestValidator).validate(user);
            }

            @Test
            void shouldThrowException() {
                assertException(
                    BadRequestException.class,
                    () -> service.requestWithdrawal(userId),
                    errorMessage
                );

                then(userWithdrawalRequestValidator).should().validate(user);
                then(userService).should(never()).withdrawRequest(user);
            }
        }
    }
}
