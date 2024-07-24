package ag.act.api.user;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.BlockedUser;
import ag.act.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.Optional;

import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UnBlockUserApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/users/me/blocked-users/{blockedUserId}";

    private User user;
    private User blockTargetUser;
    private String jwt;
    private BlockedUser blockedUser;

    @BeforeEach
    void setUp() {
        itUtil.init();
        user = itUtil.createUser();
        blockTargetUser = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());
        blockedUser = itUtil.createBlockedUser(user.getId(), blockTargetUser.getId());
    }

    @Nested
    class WhenNormal {

        @Test
        void shouldReturnSuccess() throws Exception {
            ag.act.model.SimpleStringResponse simpleStringResponse = callApiAndGetResult(status().isOk());

            assertResponse(simpleStringResponse);
            assertFromDatabase();
        }
    }

    @Nested
    class WhenWithdrawUserBlocked {

        @BeforeEach
        void setUp() {
            blockTargetUser = itUtil.createDeletedUser();
            blockedUser = itUtil.createBlockedUser(user.getId(), blockTargetUser.getId());
        }

        @Test
        void shouldReturnSuccess() throws Exception {
            callApiAndGetResult(status().isOk());
            assertFromDatabase();
        }
    }

    private ag.act.model.SimpleStringResponse callApiAndGetResult(ResultMatcher resultMatcher) throws Exception {
        MvcResult response = mockMvc
            .perform(
                delete(TARGET_API, blockTargetUser.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(resultMatcher)
            .andReturn();

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            ag.act.model.SimpleStringResponse.class
        );
    }

    private void assertResponse(ag.act.model.SimpleStringResponse response) {
        assertThat(response.getStatus(), is("ok"));
    }

    private void assertFromDatabase() {
        Optional<BlockedUser> result = itUtil.findBlockedUserById(blockedUser.getId());

        assertThat(result.isPresent(), is(false));
    }
}
