package ag.act.facade.authfacade;

import ag.act.configuration.security.ActUserProvider;
import ag.act.entity.User;
import ag.act.facade.auth.AuthFacade;
import ag.act.model.CheckNicknameResponse;
import ag.act.service.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mockStatic;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class AuthFacadeCheckNicknameTest {

    @InjectMocks
    private AuthFacade facade;
    private List<MockedStatic<?>> statics;
    @Mock
    private UserService userService;
    @Mock
    private User user;
    @Mock
    private User foundUser;
    private String nickname;
    private CheckNicknameResponse result;
    private Long userId;

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(ActUserProvider.class));

        // Given
        nickname = someString(10);
        userId = someLong();

        given(user.getId()).willReturn(userId);
        given(ActUserProvider.getNoneNull()).willReturn(user);
    }

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @Nested
    class WhenNotFoundTheNickname extends DefaultTrueTestCases {
        @BeforeEach
        void setUp() {
            // Given
            given(userService.getUserByNickname(nickname)).willReturn(Optional.empty());

            // When
            result = facade.checkNickname(nickname);
        }
    }

    @Nested
    class WhenFoundTheNickname {
        @BeforeEach
        void setUp() {
            // Given
            given(userService.getUserByNickname(nickname)).willReturn(Optional.of(foundUser));
        }

        @Nested
        class AndTheUserIsTheSameUser extends DefaultTrueTestCases {
            @BeforeEach
            void setUp() {
                // Given
                given(foundUser.getId()).willReturn(userId);

                // When
                result = facade.checkNickname(nickname);
            }
        }

        @Nested
        class AndAnotherUserAlreadyUsedTheSameNickname extends DefaultFalseTestCases {
            @BeforeEach
            void setUp() {
                // Given
                given(foundUser.getId()).willReturn(someLong());

                // When
                result = facade.checkNickname(nickname);
            }
        }
    }

    @SuppressWarnings({"unused", "JUnitMalformedDeclaration"})
    class DefaultTrueTestCases {

        @Test
        void shouldInvalidatePinNumberVerificationTime() {
            then(userService).should().getUserByNickname(nickname);
        }

        @Test
        void shouldReturnCanUseIsTrue() {
            assertThat(result.getData().getCanUse(), is(true));
        }
    }

    @SuppressWarnings({"unused", "JUnitMalformedDeclaration"})
    class DefaultFalseTestCases {

        @Test
        void shouldTryFindUserByNickname() {
            then(userService).should().getUserByNickname(nickname);
        }

        @Test
        void shouldReturnCanUseIsFalse() {
            assertThat(result.getData().getCanUse(), is(false));
        }
    }

}
