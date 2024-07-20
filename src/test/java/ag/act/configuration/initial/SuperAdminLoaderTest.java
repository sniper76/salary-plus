package ag.act.configuration.initial;

import ag.act.entity.NicknameHistory;
import ag.act.entity.Role;
import ag.act.entity.User;
import ag.act.enums.RoleType;
import ag.act.repository.NicknameHistoryRepository;
import ag.act.repository.RoleRepository;
import ag.act.repository.UserRepository;
import ag.act.service.user.UserPasswordService;
import ag.act.service.user.UserRoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static ag.act.TestUtil.assertException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static shiver.me.timbers.data.random.RandomLongs.somePositiveLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class SuperAdminLoaderTest {

    private SuperAdminLoader loader;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private NicknameHistoryRepository nicknameHistoryRepository;
    @Mock
    private UserPasswordService userPasswordService;
    @Mock
    private UserRoleService userRoleService;
    private String username;
    @Mock
    private User superUser;
    @Mock
    private Role role;

    @BeforeEach
    void setUp() {
        username = someString(5);
        final String password = someString(10);
        final Long userId = somePositiveLong();

        given(userPasswordService.encodePassword(password)).willReturn(password);
        given(userRepository.save(any(User.class))).willReturn(superUser);
        given(superUser.getId()).willReturn(userId);
        given(nicknameHistoryRepository.saveAndFlush(any(NicknameHistory.class)))
            .willAnswer(invocation -> invocation.getArgument(0));

        loader = new SuperAdminLoader(
            username,
            password,
            userRepository,
            roleRepository,
            nicknameHistoryRepository,
            userPasswordService,
            userRoleService
        );
    }

    @Nested
    class WhenSuperAdminIsAlreadyExists {
        @BeforeEach
        void setUp() {
            given(userRepository.findUserByEmail(username)).willReturn(Optional.of(superUser));
            given(roleRepository.findByType(any(RoleType.class))).willReturn(Optional.of(role));
        }

        @Test
        void shouldNotCreateSuperAdmin() {
            // When
            loader.load();

            // Then
            then(userRepository).should().findUserByEmail(username);
        }
    }

    @Nested
    class WhenNotFoundSuperAdminRole {
        @BeforeEach
        void setUp() {
            given(roleRepository.findByType(RoleType.SUPER_ADMIN)).willReturn(Optional.empty());
        }

        @Test
        void shouldThrowRuntimeException() {
            assertException(
                RuntimeException.class,
                () -> loader.load(),
                "SUPER_ADMIN role not found"
            );
        }
    }

    @Nested
    class WhenSuccessfullyCreateSuperAdmin {
        @BeforeEach
        void setUp() {
            given(userRoleService.saveUserRoles(anyList())).willAnswer(invocation -> invocation.getArgument(0));
            given(roleRepository.findByType(RoleType.SUPER_ADMIN)).willReturn(Optional.of(role));

            loader.load();
        }

        @Test
        void shouldCallFindUserByEmail() {
            then(userRepository).should().findUserByEmail(username);
        }

        @Test
        void shouldCallSaveUser() {
            then(userRepository).should().save(any(User.class));
        }

        @Test
        void shouldCallSaveAndFlush() {
            then(nicknameHistoryRepository).should().saveAndFlush(any(NicknameHistory.class));
        }

        @Test
        void shouldCallSaveUserRoles() {
            then(userRoleService).should().saveUserRoles(anyList());
        }
    }
}
