package ag.act.api.auth;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.User;
import ag.act.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ResetPinNumberIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/auth/reset-pin-number";

    @Autowired
    private UserRepository userRepository;
    private Long userId;
    private String jwt;

    @BeforeEach
    void setUp() {
        itUtil.init();
        final User user = itUtil.createUserBeforePinRegistered();
        userId = user.getId();
        jwt = itUtil.createJwt(userId);
    }

    @DisplayName("Should return 200 response code when call " + TARGET_API)
    @Test
    void shouldReturnSuccess() throws Exception {
        MvcResult response = mockMvc
            .perform(
                post(TARGET_API)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(status().isOk())
            .andReturn();

        final ag.act.model.SimpleStringResponse result = objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            ag.act.model.SimpleStringResponse.class
        );

        assertResponse(result);
    }

    private void assertResponse(ag.act.model.SimpleStringResponse result) {
        final User dbUser = userRepository.findById(userId).orElseThrow();
        assertThat(dbUser.getHashedPinNumber(), is(nullValue()));
        assertThat(dbUser.getLastPinNumberVerifiedAt(), is(nullValue()));
        assertThat(result.getStatus(), is("ok"));
    }
}
