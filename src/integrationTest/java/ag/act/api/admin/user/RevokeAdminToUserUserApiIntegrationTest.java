package ag.act.api.admin.user;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.converter.DateTimeConverter;
import ag.act.entity.User;
import ag.act.enums.RoleType;
import ag.act.model.UserDataResponse;
import ag.act.model.UserResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static ag.act.TestUtil.assertTime;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RevokeAdminToUserUserApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/users/{userId}/revoke-admin";

    private String jwt;
    private Long userId;

    @BeforeEach
    void setUp() {
        itUtil.init();
        final User adminUser = itUtil.createSuperAdminUser();
        jwt = itUtil.createJwt(adminUser.getId());

        final User user = itUtil.createUser();
        userId = user.getId();
        itUtil.createUserRole(user, RoleType.ADMIN);
    }

    private UserDataResponse getResponse(MvcResult response) throws JsonProcessingException, UnsupportedEncodingException {
        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            UserDataResponse.class
        );
    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                post(TARGET_API, userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    private void assertResponse(UserDataResponse result) {
        final User userFromDatabase = itUtil.findUser(userId);
        final UserResponse userResponse = result.getData();

        assertThat(userResponse.getId(), is(userFromDatabase.getId()));
        assertThat(userResponse.getNickname(), is(userFromDatabase.getNickname()));
        assertThat(userResponse.getGender(), is(userFromDatabase.getGender()));
        assertThat(userResponse.getMySpeech(), is(userFromDatabase.getMySpeech()));
        assertThat(DateTimeConverter.convert(userResponse.getBirthDate()), is(userFromDatabase.getBirthDate()));
        assertThat(userResponse.getIsAgreeToReceiveMail(), is(userFromDatabase.getIsAgreeToReceiveMail()));
        assertThat(userResponse.getIsPinNumberRegistered(), is(userFromDatabase.getHashedPinNumber() != null));
        assertThat(userResponse.getJobTitle(), is(userFromDatabase.getJobTitle()));
        assertThat(userResponse.getAddress(), is(userFromDatabase.getAddress()));
        assertThat(userResponse.getAddressDetail(), is(userFromDatabase.getAddressDetail()));
        assertThat(userResponse.getZipcode(), is(userFromDatabase.getZipcode()));
        assertThat(userResponse.getTotalAssetAmount(), is(userFromDatabase.getTotalAssetAmount()));
        assertThat(userResponse.getProfileImageUrl(), is(userFromDatabase.getProfileImageUrl()));
        assertThat(userResponse.getStatus(), is(userFromDatabase.getStatus()));
        assertThat(userResponse.getAuthType(), is(userFromDatabase.getAuthType()));
        assertTime(userResponse.getCreatedAt(), userFromDatabase.getCreatedAt());
        assertTime(userResponse.getUpdatedAt(), userFromDatabase.getUpdatedAt());
        assertTime(userResponse.getDeletedAt(), userFromDatabase.getDeletedAt());
        assertThat(userResponse.getIsAdmin(), is(false));
        assertThat(userResponse.getIsChangePasswordRequired(), is(userFromDatabase.getIsChangePasswordRequired()));
        assertThat(userResponse.getRoles(), is(List.of(RoleType.USER.name())));
        assertThat(userResponse.getPhoneNumber(), is(itUtil.decrypt(userFromDatabase.getHashedPhoneNumber())));
    }

    @Nested
    class RevokeToRoleSuccessfully {

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final MvcResult response = callApi(status().isOk());

            assertResponse(getResponse(response));
        }
    }
}
