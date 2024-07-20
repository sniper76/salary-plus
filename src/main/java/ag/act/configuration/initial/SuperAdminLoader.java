package ag.act.configuration.initial;

import ag.act.configuration.security.ActUserProvider;
import ag.act.entity.NicknameHistory;
import ag.act.entity.Role;
import ag.act.entity.User;
import ag.act.entity.UserRole;
import ag.act.enums.RoleType;
import ag.act.model.Gender;
import ag.act.model.Status;
import ag.act.parser.DateTimeParser;
import ag.act.repository.NicknameHistoryRepository;
import ag.act.repository.RoleRepository;
import ag.act.repository.UserRepository;
import ag.act.service.user.UserPasswordService;
import ag.act.service.user.UserRoleService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Order(1)
@Component
@Transactional
public class SuperAdminLoader implements InitialLoader {

    private final String username;
    private final String password;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final NicknameHistoryRepository nicknameHistoryRepository;
    private final UserPasswordService userPasswordService;
    private final UserRoleService userRoleService;

    public SuperAdminLoader(
        @Value("${security.super-user.username:admin@act.ag}") String username,
        @Value("${security.super-user.password:admin}") String password,
        UserRepository userRepository,
        RoleRepository roleRepository,
        NicknameHistoryRepository nicknameHistoryRepository,
        UserPasswordService userPasswordService,
        UserRoleService userRoleService
    ) {
        this.username = username;
        this.password = password;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.nicknameHistoryRepository = nicknameHistoryRepository;
        this.userPasswordService = userPasswordService;
        this.userRoleService = userRoleService;
    }

    @Override
    public void load() {
        loadRoles();

        User superAdmin = loadSuperAdmin();
        superAdmin.setAdmin(true);
        ActUserProvider.initSystemUser(superAdmin);
    }

    private User loadSuperAdmin() {
        final Optional<User> superUserOptional = userRepository.findUserByEmail(username);

        if (superUserOptional.isPresent()) {
            final User superAdmin = superUserOptional.get();
            updateSuperAdminIfNeeded(superAdmin);
            return superAdmin;
        }

        final User superAdmin = createSuperAdmin();

        final Role superAdminRole = roleRepository.findByType(RoleType.SUPER_ADMIN)
            .orElseThrow(() -> new RuntimeException("SUPER_ADMIN role not found"));

        final UserRole userRole = new UserRole();
        userRole.setUserId(superAdmin.getId());
        userRole.setRoleId(superAdminRole.getId());

        final List<UserRole> userRoles = List.of(userRole);
        userRoleService.saveUserRoles(userRoles);

        return superAdmin;
    }

    private void updateSuperAdminIfNeeded(User user) {
        if (!userPasswordService.isCorrectPassword(password, user.getPassword())) {
            user.setPassword(userPasswordService.encodePassword(password));
            userRepository.save(user);
        }
    }

    private User createSuperAdmin() {

        User user = new User();
        user.setName("슈퍼관리자");
        user.setEmail(username);
        user.setPassword(userPasswordService.encodePassword(password));
        user.setHashedPhoneNumber("SuperAdmin");
        user.setBirthDate(DateTimeParser.parseDate("19700101"));
        user.setGender(Gender.M);
        user.setHashedCI("SuperAdmin");
        user.setHashedDI("SuperAdmin");
        user.setIsAgreeToReceiveMail(true);
        user.setHashedPinNumber("SuperAdmin");
        user.setLastPinNumberVerifiedAt(LocalDateTime.now().plusYears(100));
        user.setNickname("SuperAdmin");
        user.setJobTitle("SuperAdmin");
        user.setAddress("SuperAdmin");
        user.setAddressDetail("SuperAdmin");
        user.setZipcode("12345");
        user.setTotalAssetAmount(0L);
        user.setProfileImageUrl(null);
        user.setPushToken(null);
        user.setStatus(Status.ACTIVE);

        final User savedUser = userRepository.save(user);

        createNicknameHistory(savedUser);

        return savedUser;
    }

    private NicknameHistory createNicknameHistory(User user) {
        final NicknameHistory nicknameHistory = new NicknameHistory();
        nicknameHistory.setUserId(user.getId());
        nicknameHistory.setIsFirst(true);
        nicknameHistory.setNickname("SuperAdmin");
        nicknameHistory.setStatus(Status.ACTIVE);
        nicknameHistoryRepository.saveAndFlush(nicknameHistory);
        user.setNicknameHistory(nicknameHistory);
        return nicknameHistory;
    }

    private void loadRoles() {
        for (RoleType roleType : RoleType.values()) {
            roleRepository.findByType(roleType)
                .orElseGet(() -> {
                    final Role role = new Role();
                    role.setType(roleType);
                    role.setStatus(Status.ACTIVE);
                    return roleRepository.save(role);
                });
        }
    }
}
