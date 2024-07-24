package ag.act.api.auth;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LoginIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/auth/verify-pin-number";

    private String jwt;

    @Nested
    class WhenUserIsAlreadyDeleted {
        @BeforeEach
        void setUp() {
            itUtil.init();
            final User user = itUtil.createUser();
            jwt = itUtil.createJwt(user.getId());
            itUtil.withdrawRequest(user);
        }

        @DisplayName("Should return 401 response code when call " + TARGET_API)
        @Test
        void shouldReturn401() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    post(TARGET_API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .headers(headers(jwt(jwt)))
                )
                .andExpect(status().isUnauthorized())
                .andReturn();

            itUtil.assertErrorResponse(response, 401, "탈퇴한 회원입니다.");
        }
    }
}
