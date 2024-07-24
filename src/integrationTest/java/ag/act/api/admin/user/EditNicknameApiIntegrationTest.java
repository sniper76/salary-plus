package ag.act.api.admin.user;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.User;
import ag.act.model.UserDataResponse;
import ag.act.model.UserResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.io.UnsupportedEncodingException;

import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class EditNicknameApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/users/{userId}/nickname";

    private String jwt;
    private User adminUser;
    private Long userId;
    private ag.act.model.EditUserNicknameRequest request;


    @BeforeEach
    void setUp() {
        itUtil.init();
        adminUser = itUtil.createSuperAdminUser();
        jwt = itUtil.createJwt(adminUser.getId());

        final User user = itUtil.createUser();
        userId = user.getId();
    }

    @Nested
    class EditNicknameSuccessfully {

        @BeforeEach
        void setUp() {
            request = genRequest(someString(6));
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final MvcResult response = callApi(status().isOk());

            assertResponse(getResponse(response));
        }
    }

    @Nested
    class FailToEditNickname {

        @Nested
        class WhenEmptyNicknameRequest {

            @BeforeEach
            void setUp() {
                request = genEmptyRequest();
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequest() throws Exception {
                final MvcResult response = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(response, 400, "닉네임을 확인해주세요.");
            }
        }

        @Nested
        class WhenNicknameExistAlready {

            @BeforeEach
            void setUp() {
                request = genRequest(adminUser.getNickname());
            }

            @DisplayName("Should return 400 response code when nickname is already used")
            @Test
            void shouldReturnBadRequest() throws Exception {

                final MvcResult response = callApi(status().isBadRequest());
                itUtil.assertErrorResponse(response, 400, "이미 사용중인 닉네임입니다.");
            }
        }
    }

    private ag.act.model.EditUserNicknameRequest genRequest(String nickname) {
        return new ag.act.model.EditUserNicknameRequest().nickname(nickname);
    }

    private ag.act.model.EditUserNicknameRequest genEmptyRequest() {
        return new ag.act.model.EditUserNicknameRequest().nickname(Strings.EMPTY);
    }

    private UserDataResponse getResponse(MvcResult response) throws JsonProcessingException, UnsupportedEncodingException {
        return objectMapperUtil.toResponse(response.getResponse().getContentAsString(), UserDataResponse.class);
    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc.perform(
                patch(TARGET_API, userId)
                    .content(objectMapperUtil.toJson(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt))))
            .andExpect(resultMatcher)
            .andReturn();
    }

    private void assertResponse(UserDataResponse result) {
        final User userFromDatabase = itUtil.findUser(userId);
        final UserResponse userResponse = result.getData();

        assertThat(userResponse.getId(), is(userFromDatabase.getId()));
        assertThat(userResponse.getNickname(), is(userFromDatabase.getNickname()));
    }
}
