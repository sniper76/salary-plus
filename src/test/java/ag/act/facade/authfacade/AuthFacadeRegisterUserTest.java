package ag.act.facade.authfacade;

import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.user.UserConverter;
import ag.act.entity.User;
import ag.act.facade.auth.AuthFacade;
import ag.act.model.UserResponse;
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
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mockStatic;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class AuthFacadeRegisterUserTest {

    @InjectMocks
    private AuthFacade facade;
    private List<MockedStatic<?>> statics;
    @Mock
    private UserService userService;
    @Mock
    private UserConverter userConverter;
    @Mock
    private ag.act.model.UserResponse userResponse;
    @Mock
    private User savedUser;
    @Mock
    private User user;
    @Mock
    private ag.act.model.RegisterUserInfoRequest registerUserInfoRequest;

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(ActUserProvider.class));

        final String email = someString(5);
        final Long userId = someLong();

        given(user.getId()).willReturn(userId);
        given(registerUserInfoRequest.getEmail()).willReturn(email);
        given(ActUserProvider.getNoneNull()).willReturn(user);
        given(userService.getUserByEmail(email)).willReturn(Optional.empty());
        given(userService.registerUserInfo(user, registerUserInfoRequest)).willReturn(savedUser);
        given(userConverter.convert(savedUser)).willReturn(userResponse);
    }

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @Nested
    class Success {

        private UserResponse result;

        @BeforeEach
        void setUp() {
            result = facade.registerUserInfo(registerUserInfoRequest);
        }

        @Test
        void shouldReturnTheUserResponse() {
            assertThat(result, is(userResponse));
        }

        @Test
        void shouldSaveUser() {
            then(userService).should().registerUserInfo(user, registerUserInfoRequest);
        }

        @Test
        void shouldConvertSavedUser() {
            then(userConverter).should().convert(savedUser);
        }
    }

}
