package ag.act.api.user;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.User;
import ag.act.model.AuthType;
import ag.act.model.SimpleStringResponse;
import ag.act.model.UpdateMyAuthTypeRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserApiUpdateMyAuthTypeIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/users/me/auth-type";

    private UpdateMyAuthTypeRequest request;
    private User user;
    private String jwt;

    @BeforeEach
    void setUp() {
        itUtil.init();
        user = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());
    }

    @Nested
    class WhenChangePinToBio {

        @BeforeEach
        void setUp() {
            request = genRequest(AuthType.BIO);
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final SimpleStringResponse result = callApiAndGetResult();

            assertResponse(result);
            assertUserFromDatabase();
        }
    }

    @Nested
    class WhenChangeBioToPin {

        @BeforeEach
        void setUp() {
            request = genRequest(AuthType.PIN);
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final SimpleStringResponse result = callApiAndGetResult();

            assertResponse(result);
            assertUserFromDatabase();
        }
    }

    private SimpleStringResponse callApiAndGetResult() throws Exception {
        MvcResult response = mockMvc
            .perform(
                patch(TARGET_API)
                    .content(objectMapperUtil.toJson(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(status().isOk())
            .andReturn();

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            SimpleStringResponse.class
        );
    }

    private UpdateMyAuthTypeRequest genRequest(AuthType authType) {
        UpdateMyAuthTypeRequest updateMyAuthTypeRequest = new UpdateMyAuthTypeRequest();
        updateMyAuthTypeRequest.setAuthType(authType.toString());
        return updateMyAuthTypeRequest;
    }

    private void assertUserFromDatabase() {
        final User findUser = itUtil.findUser(user.getId());
        assertThat(findUser.getAuthType().getValue(), is(request.getAuthType()));
    }

    private void assertResponse(SimpleStringResponse result) {
        assertThat(result.getStatus(), is("ok"));
    }
}
