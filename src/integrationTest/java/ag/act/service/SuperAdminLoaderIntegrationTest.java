package ag.act.service;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.NicknameHistory;
import ag.act.entity.Role;
import ag.act.entity.User;
import ag.act.enums.RoleType;
import ag.act.repository.NicknameHistoryRepository;
import ag.act.repository.RoleRepository;
import ag.act.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

class SuperAdminLoaderIntegrationTest extends AbstractCommonIntegrationTest {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private NicknameHistoryRepository nicknameHistoryRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        itUtil.init();
    }

    @Nested
    class LoadSuperAdmin {
        @Test
        void loadSuperAdmin() {
            final Optional<User> superUserOptional = userRepository.findUserByEmail("test_super_user_username");
            assertThat(superUserOptional.isPresent(), is(true));
            assertThat(superUserOptional.get().getNicknameHistory(), notNullValue());

            assertNicknameHistory(superUserOptional.get());
            assertRoles();
        }

        private void assertNicknameHistory(User user) {
            final List<NicknameHistory> nicknameHistoryList = nicknameHistoryRepository.findAll();
            assertThat(nicknameHistoryList.size(), greaterThan(0));
            assertThat(nicknameHistoryList.get(0).getUserId(), is(user.getId()));
        }

        private void assertRoles() {
            for (RoleType roleType : RoleType.values()) {
                final Optional<Role> roleOptional = roleRepository.findByType(roleType);
                assertThat(roleOptional.isPresent(), is(true));
                assertThat(roleOptional.get(), notNullValue());
            }
        }
    }
}
