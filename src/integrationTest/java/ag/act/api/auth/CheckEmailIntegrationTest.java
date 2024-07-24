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
import java.util.List;

import static ag.act.TestUtil.someEmail;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CheckEmailIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/auth/check-email";

    private String jwt;
    private ag.act.model.CheckEmailRequest request;
    private User user;

    @BeforeEach
    void setUp() {
        itUtil.init();
        user = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());
    }

    private ag.act.model.CheckEmailResponse getResponse(MvcResult response)
        throws JsonProcessingException, UnsupportedEncodingException {

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            ag.act.model.CheckEmailResponse.class
        );
    }

    private MvcResult callApi() throws Exception {
        return mockMvc
            .perform(
                post(TARGET_API)
                    .content(objectMapperUtil.toRequestBody(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(status().isOk())
            .andReturn();
    }

    private void assertResponse(ag.act.model.CheckEmailResponse result, boolean expectedCanUse) {
        assertThat(result.getData().getCanUse(), is(expectedCanUse));
    }

    private MvcResult callApiError(ag.act.model.CheckEmailRequest request) throws Exception {
        return mockMvc
            .perform(
                post(TARGET_API)
                    .content(objectMapperUtil.toRequestBody(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(status().isBadRequest())
            .andReturn();
    }

    @Nested
    class WhenNewEmailProvided {

        @BeforeEach
        void setUp() {
            request = new ag.act.model.CheckEmailRequest().email(someEmail());
        }

        @DisplayName("Should return 200 response code with `true` when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final ag.act.model.CheckEmailResponse result = getResponse(callApi());
            assertResponse(result, true);
        }

    }

    @Nested
    class WhenMyEmailProvided {

        @BeforeEach
        void setUp() {
            request = new ag.act.model.CheckEmailRequest().email(user.getEmail());
        }

        @DisplayName("Should return 200 response code with `true` when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final ag.act.model.CheckEmailResponse result = getResponse(callApi());
            assertResponse(result, true);
        }

    }

    @Nested
    class WhenExistingEmailProvided {

        @BeforeEach
        void setUp() {
            final User anotherUser = itUtil.createUser();
            request = new ag.act.model.CheckEmailRequest().email(anotherUser.getEmail());
        }

        @DisplayName("Should return 200 response code with `false` when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final ag.act.model.CheckEmailResponse result = getResponse(callApi());
            assertResponse(result, false);
        }

    }

    @Nested
    class WhenInvalidateEmailProvided {

        @BeforeEach
        void setUp() {
            request = new ag.act.model.CheckEmailRequest();
        }

        @DisplayName("Should return 400 response code success email when call " + TARGET_API)
        @Test
        void shouldReturnSuccessResponse() throws Exception {
            successResponse(List.of(
                    "test@test.co.kr",
                    "test@act.ag",
                    "99test@act.com",
                    "1212121@test.com"
                )
            );
        }

        @DisplayName("Should return 400 response code error email when call " + TARGET_API)
        @Test
        void shouldReturnErrorResponse() throws Exception {
            errorResponse(List.of(
                    "test@testcom",
                    "+test@test.com",
                    "-test@test.com",
                    ".test@test.com",
                    "_test@test.com",
                    "test@test.c",
                    "t@test.com"
                )
            );
        }

        private void errorResponse(List<String> emails) throws Exception {
            for (String s : emails) {
                itUtil.assertErrorResponse(
                    callApiError(request.email(s)),
                    400,
                    "형식에 맞지 않는 이메일입니다. ex) act123@naver.com"
                );
            }
        }

        private void successResponse(List<String> emails) throws Exception {
            for (String s : emails) {
                request.email(s);
                final ag.act.model.CheckEmailResponse result = getResponse(callApi());
                assertResponse(result, true);
            }
        }
    }
}
