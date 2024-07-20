package ag.act.api.auth;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LogoutIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/auth/logout";

    private String jwt;

    @BeforeEach
    void setUp() {
        itUtil.init();
        final User user = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());
    }

    @DisplayName("Should return 200 response code when call " + TARGET_API)
    @Test
    void shouldReturnSuccess() throws Exception {
        MvcResult response = mockMvc
            .perform(
                post(TARGET_API)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(status().isNoContent())
            .andReturn();

        assertThat(response.getResponse().getContentAsString(), is(""));
    }
}
