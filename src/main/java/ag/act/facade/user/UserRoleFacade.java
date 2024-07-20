package ag.act.facade.user;

import ag.act.converter.DecryptColumnConverter;
import ag.act.converter.user.UserDataResponseConverter;
import ag.act.entity.Role;
import ag.act.entity.User;
import ag.act.entity.UserRole;
import ag.act.enums.RoleType;
import ag.act.exception.BadRequestException;
import ag.act.model.AddRoleToUserRequest;
import ag.act.model.UserDataResponse;
import ag.act.service.RoleService;
import ag.act.service.user.UserPasswordService;
import ag.act.service.user.UserRoleService;
import ag.act.service.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserRoleFacade {
    private final UserRoleService userRoleService;
    private final RoleService roleService;
    private final UserService userService;
    private final UserDataResponseConverter userDataResponseConverter;
    private final UserPasswordService userPasswordService;
    private final DecryptColumnConverter decryptColumnConverter;

    public ag.act.model.UserDataResponse assignAdminRoleToUser(Long userId) {

        final User user = addRoleToUser(
            getUser(userId),
            new AddRoleToUserRequest().roleType(RoleType.ADMIN.name())
        );

        initializeCmsPassword(user);

        return userDataResponseConverter.convert(userService.saveUser(user));
    }

    public ag.act.model.UserDataResponse assignAcceptorRoleToUser(Long userId) {

        final User currentUser = getUser(userId);
        final RoleType acceptorUserRoleType = RoleType.ACCEPTOR_USER;

        if (hasRole(currentUser, getRole(acceptorUserRoleType))) {
            return userDataResponseConverter.convert(currentUser);
        }

        final User roleAddedUser = addRoleToUser(
            currentUser,
            new AddRoleToUserRequest().roleType(acceptorUserRoleType.name())
        );

        initializeCmsPassword(roleAddedUser);

        return userDataResponseConverter.convert(userService.saveUser(roleAddedUser));
    }

    private void initializeCmsPassword(User user) {
        userPasswordService.setPasswordAndChangeRequired(
            user,
            decryptColumnConverter.convert(user.getHashedPhoneNumber()),
            Boolean.TRUE
        );
    }

    public UserDataResponse addRoleToUser(Long userId, AddRoleToUserRequest addRoleToUserRequest) {
        final User user = addRoleToUser(getUser(userId), addRoleToUserRequest);

        return userDataResponseConverter.convert(user);
    }

    private User addRoleToUser(User user, AddRoleToUserRequest addRoleToUserRequest) {
        final Role role = getRole(getRoleType(addRoleToUserRequest.getRoleType()));

        validateUserAlreadyHasRole(user, role);
        user.getRoles().add(saveUserRole(user, role));

        return user;
    }

    private UserRole saveUserRole(User user, Role role) {
        return userRoleService.saveUserRole(createUserRole(user, role));
    }

    private UserRole createUserRole(User user, Role role) {
        final UserRole userRole = new UserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(role.getId());
        userRole.setRole(role);
        return userRole;
    }

    private void validateUserAlreadyHasRole(User user, Role role) {
        if (hasRole(user, role)) {
            throw new BadRequestException("이미 해당 권한을 가지고 있습니다.");
        }
    }

    private boolean hasRole(User user, Role role) {
        return user.getRoles()
            .stream()
            .anyMatch(userRole -> userRole.getRole().getType() == role.getType());
    }

    private Role getRole(RoleType roleType) {
        return roleService.findRoleByType(roleType)
            .orElseThrow(() -> new BadRequestException("권한을 찾을 수 없습니다."));
    }

    private User getUser(Long userId) {
        return userService.findUser(userId)
            .orElseThrow(() -> new BadRequestException("회원을 찾을 수 없습니다."));
    }

    private RoleType getRoleType(String roleTypeName) {
        try {
            return RoleType.fromValue(roleTypeName);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(e.getMessage(), e);
        }
    }

    public UserDataResponse revokeAdminToUser(Long userId) {
        return revokeToUser(userId, RoleType.ADMIN);
    }

    public UserDataResponse revokeAcceptorToUser(Long userId) {
        return revokeToUser(userId, RoleType.ACCEPTOR_USER);
    }

    private UserDataResponse revokeToUser(Long userId, RoleType roleType) {
        final User user = getUser(userId);
        final UserRole userRole = getUserRole(user, roleType);

        userRoleService.deleteById(userRole.getId());

        if (!hasAnyRolesExceptAdmin(user)) {
            saveUserRole(user, getRole(RoleType.USER));
        }

        return userDataResponseConverter.convert(userService.getUser(userId));
    }

    private boolean hasAnyRolesExceptAdmin(User user) {
        return user.getRoles()
            .stream()
            .anyMatch(it -> it.getRole().getType() != RoleType.ADMIN);
    }

    private UserRole getUserRole(User user, RoleType roleType) {
        return user.getRoles()
            .stream()
            .filter(it -> it.getRole().getType() == roleType)
            .findFirst()
            .orElseThrow(() -> new BadRequestException("해당 사용자는 어드민 권한이 없습니다."));
    }
}
