package ag.act.api.admin.user;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.User;
import ag.act.enums.ActErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CheckIsChangePasswordRequiredApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/users/{userId}";

    private String jwt;
    private Long userId;

    @BeforeEach
    void setUp() {
        itUtil.init();
        final User adminUser = itUtil.createAdminUser();
        jwt = itUtil.createJwt(adminUser.getId());

        adminUser.setIsChangePasswordRequired(true);
        itUtil.updateUser(adminUser);

        final User user = itUtil.createUser();
        userId = user.getId();
    }

    @Nested
    class WhenGetUserDetails {

        @DisplayName("Should return 403 response code when call " + TARGET_API)
        @Test
        void shouldReturnForbiddenException() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    get(TARGET_API, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .headers(headers(jwt(jwt)))
                )
                .andExpect(status().isForbidden())
                .andReturn();

            final ag.act.model.ErrorResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                ag.act.model.ErrorResponse.class
            );

            assertThat(result.getMessage(), is("비밀번호 변경이 필요합니다."));
            assertThat(result.getErrorCode(), is(ActErrorCode.IS_CHANGE_PASSWORD_REQUIRED.getCode()));
            assertThat(result.getStatusCode(), is(403));
        }
    }
}
