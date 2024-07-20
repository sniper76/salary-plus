package ag.act.api.admin.auth;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.converter.DateTimeConverter;
import ag.act.converter.DecryptColumnConverter;
import ag.act.entity.User;
import ag.act.enums.RoleType;
import ag.act.model.AuthUserResponse;
import ag.act.model.CmsLoginRequest;
import ag.act.model.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;

import static ag.act.TestUtil.assertTime;
import static ag.act.TestUtil.someStrongPassword;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomThings.someThing;

class LogInCmsUserApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/auth/login";

    private CmsLoginRequest request;
    @Autowired
    private DecryptColumnConverter decryptColumnConverter;
    private String password;
    private User cmsUser;
    private RoleType cmsUserRoleType;

    @BeforeEach
    void setUp() {
        itUtil.init();
        cmsUserRoleType = someThing(RoleType.ACCEPTOR_USER, RoleType.ADMIN);
        cmsUser = itUtil.createUserWithRole(cmsUserRoleType);
        password = decryptColumnConverter.convert(cmsUser.getHashedPhoneNumber());
    }

    @Nested
    class LogInSuccessfully {

        @BeforeEach
        void setUp() {
            request = genRequest(cmsUser.getEmail(), password);
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final MvcResult response = callApi(status().isOk());

            assertResponse(getResponse(response));
        }
    }

    @Nested
    class FailToLogin {

        @Nested
        class WhenPasswordAndEmailAreWrong {

            @BeforeEach
            void setUp() {
                request = genRequest(someStrongPassword(8, 20), someStrongPassword(8, 20));
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequest() throws Exception {
                final MvcResult response = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(response, 400, "이메일 혹은 비밀번호를 확인해주세요.");
            }
        }

        @Nested
        class WhenPasswordIsNotMatched {

            @BeforeEach
            void setUp() {
                String wrongPassword = someStrongPassword(8, 20);
                request = genRequest(cmsUser.getEmail(), wrongPassword);
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequest() throws Exception {
                final MvcResult response = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(response, 400, "이메일 혹은 비밀번호를 확인해주세요.");
            }
        }
    }

    private CmsLoginRequest genRequest(String email, String password) {
        return new CmsLoginRequest()
            .email(email)
            .password(password);
    }

    private AuthUserResponse getResponse(MvcResult response) throws Exception {
        return itUtil.getResult(response, AuthUserResponse.class);
    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                post(TARGET_API)
                    .content(objectMapperUtil.toJson(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    private void assertResponse(AuthUserResponse result) {
        final User userFromDatabase = itUtil.findUser(cmsUser.getId());
        final UserResponse userResponse = result.getUser();

        assertThat(result.getToken().getAccessToken(), notNullValue());
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
        assertThat(userResponse.getIsAdmin(), is(cmsUserRoleType == RoleType.ADMIN));
        assertThat(userResponse.getRoles(), is(List.of(cmsUserRoleType.name())));
        assertThat(userResponse.getPhoneNumber(), is(itUtil.decrypt(userFromDatabase.getHashedPhoneNumber())));
    }
}
