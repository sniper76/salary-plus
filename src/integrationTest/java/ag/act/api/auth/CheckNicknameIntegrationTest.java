package ag.act.api.auth;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

class CheckNicknameIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/auth/check-nickname";

    private String jwt;
    private ag.act.model.CheckNicknameRequest request;
    private User user;

    @BeforeEach
    void setUp() {
        itUtil.init();
        user = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());
    }

    @Nested
    class WhenNewNicknameProvided {

        @BeforeEach
        void setUp() {
            request = new ag.act.model.CheckNicknameRequest().nickname(someAlphanumericString(10));
        }

        @DisplayName("Should return 200 response code with `true` when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final ag.act.model.CheckNicknameResponse result = getResponse(callApi());
            assertResponse(result, true);
        }

    }

    @Nested
    class WhenMyNicknameProvided {

        @BeforeEach
        void setUp() {
            request = new ag.act.model.CheckNicknameRequest().nickname(user.getNickname());
        }

        @DisplayName("Should return 200 response code with `true` when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final ag.act.model.CheckNicknameResponse result = getResponse(callApi());
            assertResponse(result, true);
        }

    }

    @Nested
    class WhenExistingNicknameProvided {

        @BeforeEach
        void setUp() {
            final User anotherUser = itUtil.createUser();
            request = new ag.act.model.CheckNicknameRequest().nickname(anotherUser.getNickname());
        }

        @DisplayName("Should return 200 response code with `false` when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final ag.act.model.CheckNicknameResponse result = getResponse(callApi());
            assertResponse(result, false);
        }

    }

    private ag.act.model.CheckNicknameResponse getResponse(MvcResult response)
        throws JsonProcessingException, UnsupportedEncodingException {

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            ag.act.model.CheckNicknameResponse.class
        );
    }

    private MvcResult callApi() throws Exception {
        return mockMvc
            .perform(
                post(TARGET_API)
                    .content(objectMapperUtil.toRequestBody(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(status().isOk())
            .andReturn();
    }

    private void assertResponse(ag.act.model.CheckNicknameResponse result, boolean expectedCanUse) {
        assertThat(result.getData().getCanUse(), is(expectedCanUse));
    }
}
