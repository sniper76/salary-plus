package ag.act.service.user;

import ag.act.entity.Role;
import ag.act.entity.UserRole;
import ag.act.enums.RoleType;
import ag.act.repository.UserRoleRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserRoleService {
    private final UserRoleRepository userRoleRepository;

    public UserRoleService(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    public boolean isAdmin(Long userId) {
        return hasRole(userId, this::isAdminRoleType);
    }

    public boolean isSuperAdmin(Long userId) {
        return hasRole(userId, this::isSuperAdminRoleType);
    }

    public boolean isAcceptorUser(Long userId) {
        return hasRole(userId, this::isAcceptorUserRoleType);
    }

    private boolean hasRole(Long userId, Predicate<UserRole> isRoleType) {
        return getActiveUserRoles(userId).anyMatch(isRoleType);
    }

    public boolean hasAnyRole(Long userId, RoleType... roleTypes) {
        Objects.requireNonNull(roleTypes);

        final Set<RoleType> roleTypeSet = Arrays.stream(roleTypes).collect(Collectors.toSet());

        return hasRole(
            userId,
            userRole -> roleTypeSet.contains(userRole.getRole().getType())
        );
    }

    private boolean isAdminRoleType(UserRole userRole) {
        return RoleType.isAdminRoleType(userRole.getRole().getType());
    }

    private boolean isSuperAdminRoleType(UserRole userRole) {
        return RoleType.isSuperAdminRoleType(userRole.getRole().getType());
    }

    private boolean isAcceptorUserRoleType(UserRole userRole) {
        return RoleType.isAcceptorUserRoleType(userRole.getRole().getType());
    }

    private boolean isActiveRole(UserRole userRole) {
        return userRole.getRole().getStatus() == ag.act.model.Status.ACTIVE;
    }

    public List<UserRole> saveUserRoles(List<UserRole> userRoles) {
        return userRoleRepository.saveAll(userRoles);
    }

    public UserRole saveUserRole(UserRole userRole) {
        return userRoleRepository.save(userRole);
    }

    public void deleteAll(Long userId) {
        userRoleRepository.deleteAllByUserId(userId);
    }

    public void deleteById(Long id) {
        userRoleRepository.deleteById(id);
    }

    public List<UserRole> getUserRoles(Long userId) {
        return userRoleRepository.findAllByUserId(userId);
    }

    public List<Role> getActiveRoles(Long userId) {
        return getActiveUserRoles(userId)
            .map(UserRole::getRole)
            .toList();
    }

    @NotNull
    private Stream<UserRole> getActiveUserRoles(Long userId) {
        return getUserRoles(userId)
            .stream()
            .filter(this::isActiveRole);
    }
}
