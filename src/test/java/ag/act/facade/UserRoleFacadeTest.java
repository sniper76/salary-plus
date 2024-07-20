package ag.act.facade;

import ag.act.converter.DecryptColumnConverter;
import ag.act.converter.user.UserDataResponseConverter;
import ag.act.entity.Role;
import ag.act.entity.User;
import ag.act.entity.UserRole;
import ag.act.enums.RoleType;
import ag.act.exception.BadRequestException;
import ag.act.facade.user.UserRoleFacade;
import ag.act.model.UserDataResponse;
import ag.act.service.RoleService;
import ag.act.service.user.UserPasswordService;
import ag.act.service.user.UserRoleService;
import ag.act.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ag.act.TestUtil.assertException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class UserRoleFacadeTest {
    @InjectMocks
    private UserRoleFacade facade;
    @Mock
    private UserRoleService userRoleService;
    @Mock
    private RoleService roleService;
    @Mock
    private UserService userService;
    @Mock
    private UserDataResponseConverter userDataResponseConverter;
    @Mock
    private UserPasswordService userPasswordService;
    @Mock
    private DecryptColumnConverter decryptColumnConverter;

    @Nested
    class AddRoleToUser {

        @Mock
        private ag.act.model.AddRoleToUserRequest request;
        private Long userId;
        private RoleType roleType;
        @Mock
        private Role role;
        @Mock
        private UserRole userRole;
        @Mock
        private User user;
        @Mock
        private ag.act.model.UserDataResponse expectedResponse;
        private UserDataResponse actualResponse;
        private ArrayList<UserRole> userRoles;

        @BeforeEach
        void setUp() {
            userId = someLong();
            roleType = someEnum(RoleType.class);
            String roleTypeName = roleType.name();
            userRoles = new ArrayList<>();

            given(request.getRoleType()).willReturn(roleTypeName);
            given(roleService.findRoleByType(roleType)).willReturn(Optional.of(role));
            given(userService.findUser(userId)).willReturn(Optional.of(user));
            given(user.getRoles()).willReturn(userRoles);
            given(userDataResponseConverter.convert(user)).willReturn(expectedResponse);
            given(userRole.getRole()).willReturn(role);
            given(userRoleService.saveUserRole(any(UserRole.class))).willReturn(userRole);
        }

        @Nested
        class AddRoleSuccessfully {
            @BeforeEach
            void setUp() {
                actualResponse = facade.addRoleToUser(userId, request);
            }

            @Test
            void shouldReturnResponse() {
                assertThat(actualResponse, is(expectedResponse));
            }

            @Test
            void shouldUserRolesHaveRole() {
                assertThat(userRoles.get(0), is(userRole));
            }
        }

        @Nested
        class FailToAddRole {

            @Nested
            class WhenNotFoundRoleType {

                @Test
                void shouldThrowException() {

                    // Given
                    String roleTypeName = someString(10);
                    given(request.getRoleType()).willReturn(roleTypeName);

                    // When // Then
                    assertException(
                        BadRequestException.class,
                        () -> facade.addRoleToUser(userId, request),
                        "지원하지 않는 RoleType '%s' 타입입니다.".formatted(roleTypeName)
                    );
                }
            }

            @Nested
            class WhenNotFoundRole {

                @Test
                void shouldThrowException() {

                    // Given
                    given(roleService.findRoleByType(roleType)).willReturn(Optional.empty());

                    // When // Then
                    assertException(
                        BadRequestException.class,
                        () -> facade.addRoleToUser(userId, request),
                        "권한을 찾을 수 없습니다."
                    );
                }
            }

            @Nested
            class WhenUserAlreadyHaveSameRole {

                @Test
                void shouldThrowBadRequestException() {
                    // Given
                    given(user.getRoles()).willReturn(new ArrayList<>(List.of(userRole)));

                    // When // Then
                    assertException(
                        BadRequestException.class,
                        () -> facade.addRoleToUser(userId, request),
                        "이미 해당 권한을 가지고 있습니다."
                    );
                }
            }

            @Nested
            class WhenNotFoundUser {

                @Test
                void shouldThrowException() {

                    // Given
                    given(userService.findUser(userId)).willReturn(Optional.empty());

                    // When // Then
                    assertException(
                        BadRequestException.class,
                        () -> facade.addRoleToUser(userId, request),
                        "회원을 찾을 수 없습니다."
                    );
                }
            }
        }
    }

    @Nested
    class AssignAdminToUser {

        private Long userId;
        @Mock
        private Role role;
        @Mock
        private UserRole userRole;
        @Mock
        private User user;
        @Mock
        private ag.act.model.UserDataResponse expectedResponse;
        private UserDataResponse actualResponse;
        private ArrayList<UserRole> userRoles;
        private String password;

        @BeforeEach
        void setUp() {
            userId = someLong();
            userRoles = new ArrayList<>();
            final String hashedPhoneNumber = someString(20);
            password = someString(30);

            given(roleService.findRoleByType(RoleType.ADMIN)).willReturn(Optional.of(role));
            given(userService.findUser(userId)).willReturn(Optional.of(user));
            given(user.getRoles()).willReturn(userRoles);
            given(userDataResponseConverter.convert(user)).willReturn(expectedResponse);
            given(userRole.getRole()).willReturn(role);
            given(userRoleService.saveUserRole(any(UserRole.class))).willReturn(userRole);
            given(user.getHashedPhoneNumber()).willReturn(hashedPhoneNumber);
            given(decryptColumnConverter.convert(hashedPhoneNumber)).willReturn(password);
            given(userService.saveUser(user)).willReturn(user);
        }

        @Nested
        class AddRoleSuccessfully {
            @BeforeEach
            void setUp() {
                actualResponse = facade.assignAdminRoleToUser(userId);
            }

            @Test
            void shouldReturnResponse() {
                assertThat(actualResponse, is(expectedResponse));
            }

            @Test
            void shouldUserRolesHaveRole() {
                assertThat(userRoles.get(0), is(userRole));
            }

            @Test
            void shouldSetPasswordAndChangeRequired() {
                then(userPasswordService).should().setPasswordAndChangeRequired(
                    user,
                    password,
                    true
                );
            }
        }
    }
}
