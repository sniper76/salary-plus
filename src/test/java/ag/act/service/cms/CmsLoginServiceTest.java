package ag.act.service.cms;

import ag.act.entity.User;
import ag.act.exception.BadRequestException;
import ag.act.exception.ForbiddenException;
import ag.act.service.user.UserRoleService;
import ag.act.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static ag.act.TestUtil.assertException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class CmsLoginServiceTest {

    @InjectMocks
    private CmsLoginService service;
    @Mock
    private UserService userService;
    @Mock
    private UserRoleService userRoleService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ag.act.model.CmsLoginRequest request;
    private String requestEmail;
    @Mock
    private User user;
    private Long userId;

    @BeforeEach
    void setUp() {
        final String requestPassword = someString(13);
        final String userPassword = someString(15);
        requestEmail = someString(10);
        userId = someLong();

        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
        given(request.getEmail()).willReturn(requestEmail);
        given(request.getPassword()).willReturn(requestPassword);
        given(userService.getUserByEmail(requestEmail)).willReturn(Optional.of(user));
        given(user.getId()).willReturn(userId);
        given(user.getPassword()).willReturn(userPassword);
        given(userRoleService.isAcceptorUser(userId)).willReturn(true);
        given(userRoleService.isAdmin(userId)).willReturn(true);
    }

    @Nested
    class WhenLoginSuccessfully {

        private User actualUser;

        @BeforeEach
        void setUp() {
            actualUser = service.login(request);
        }

        @Test
        void shouldReturnUser() {
            assertThat(actualUser, is(user));
        }

        @Test
        void shouldSetAcceptorTrue() {
            then(actualUser).should().setAcceptor(true);
        }

        @Test
        void shouldSetAdminTrue() {
            then(actualUser).should().setAdmin(true);
        }
    }

    @Nested
    class WhenUserNotFound {
        @BeforeEach
        void setUp() {
            given(userService.getUserByEmail(requestEmail)).willReturn(Optional.empty());
        }

        @Test
        void shouldThrowBadRequestException() {
            assertException(
                BadRequestException.class,
                () -> service.login(request),
                "이메일 혹은 비밀번호를 확인해주세요."
            );
        }
    }

    @Nested
    class WhenPasswordNotMatch {
        @BeforeEach
        void setUp() {
            given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);
        }

        @Test
        void shouldThrowBadRequestException() {
            assertException(
                BadRequestException.class,
                () -> service.login(request),
                "이메일 혹은 비밀번호를 확인해주세요."
            );
        }
    }

    @Nested
    class WhenUserIsNotAdminOrAcceptor {

        @BeforeEach
        void setUp() {
            given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
            given(userRoleService.isAcceptorUser(userId)).willReturn(false);
            given(userRoleService.isAdmin(userId)).willReturn(false);
        }

        @Test
        void shouldThrowForbiddenException() {
            assertException(
                ForbiddenException.class,
                () -> service.login(request),
                "인가되지 않은 접근입니다."
            );
        }
    }
}
