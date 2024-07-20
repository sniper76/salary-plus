package ag.act.api.admin.auth;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.converter.DateTimeConverter;
import ag.act.converter.DecryptColumnConverter;
import ag.act.entity.User;
import ag.act.enums.RoleType;
import ag.act.model.ChangePasswordRequest;
import ag.act.model.UserDataResponse;
import ag.act.model.UserResponse;
import ag.act.service.user.UserPasswordService;
import ag.act.util.PasswordUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static ag.act.TestUtil.assertTime;
import static ag.act.TestUtil.someStrongPassword;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

class ChangePasswordForCmsUserApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/auth/change-password";

    private String jwt;
    private Long userId;
    private ChangePasswordRequest request;
    @Autowired
    private UserPasswordService userPasswordService;
    @Autowired
    private DecryptColumnConverter decryptColumnConverter;
    @Autowired
    private PasswordUtil passwordUtil;
    private String currentPassword;
    private String newPassword;

    @BeforeEach
    void setUp() {
        itUtil.init();
        final User adminUser = itUtil.createAdminUser();
        adminUser.setIsChangePasswordRequired(true);
        itUtil.updateUser(adminUser);
        userId = adminUser.getId();
        jwt = itUtil.createJwt(userId);

        currentPassword = decryptColumnConverter.convert(adminUser.getHashedPhoneNumber());
    }

    @Nested
    class ChangePasswordSuccessfully {

        @BeforeEach
        void setUp() {
            newPassword = someStrongPassword(8, 20);
            request = genRequest(newPassword, newPassword);
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final MvcResult response = callApi(status().isOk());

            assertResponse(getResponse(response), List.of(RoleType.ADMIN.name()), true);
        }

        @Nested
        class WhenUserIsAcceptorUser {

            @BeforeEach
            void setUp() {

                final User acceptorUser = itUtil.createAcceptorUser();
                acceptorUser.setIsChangePasswordRequired(true);
                itUtil.updateUser(acceptorUser);
                userId = acceptorUser.getId();
                jwt = itUtil.createJwt(userId);

                currentPassword = decryptColumnConverter.convert(acceptorUser.getHashedPhoneNumber());

                newPassword = someStrongPassword(8, 20);
                request = genRequest(newPassword, newPassword);
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                final MvcResult response = callApi(status().isOk());

                assertResponse(getResponse(response), List.of(RoleType.ACCEPTOR_USER.name()), false);
            }
        }
    }

    @Nested
    class FailToChangePassword {

        @Nested
        class WhenPasswordAndConfirmPasswordAreDifferent {

            @BeforeEach
            void setUp() {
                request = genRequest(someStrongPassword(8, 20), someStrongPassword(8, 20));
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequest() throws Exception {
                final MvcResult response = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(response, 400, "비밀번호와 컨펌 비밀번호가 일치하지 않습니다.");
            }
        }

        @Nested
        class WhenCurrentPasswordIsNotMatched {

            @BeforeEach
            void setUp() {
                currentPassword = someStrongPassword(8, 20);
                newPassword = someStrongPassword(8, 20);
                request = genRequest(newPassword, newPassword);
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequest() throws Exception {
                final MvcResult response = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(response, 400, "현재 비밀번호가 일치하지 않습니다.");
            }
        }

        @Nested
        class WhenPasswordIsNotStrongEnough {

            @BeforeEach
            void setUp() {
                newPassword = someAlphanumericString(15);
                request = genRequest(newPassword, newPassword);
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequest() throws Exception {
                final MvcResult response = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(response, 400,
                    "비밀번호는 영문 대소문자, 숫자, 특수문자를 포함하여 %s자 이상이어야 합니다.".formatted(passwordUtil.getMinLength()));
            }
        }

        @Nested
        class WhenNotAdmin {

            @BeforeEach
            void setUp() {
                final User user = itUtil.createUser();
                jwt = itUtil.createJwt(user.getId());
                newPassword = someStrongPassword(8, 20);
                request = genRequest(newPassword, newPassword);
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                final MvcResult response = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(response, 400, "권한이 부족합니다.");
            }
        }
    }

    private ChangePasswordRequest genRequest(String password, String confirmPassword) {
        return new ChangePasswordRequest()
            .currentPassword(currentPassword)
            .password(password)
            .confirmPassword(confirmPassword);
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
                post(TARGET_API)
                    .content(objectMapperUtil.toJson(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    private void assertResponse(UserDataResponse result, List<String> roleNames, boolean isAdmin) {
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
        assertTime(userResponse.getCreatedAt(), userFromDatabase.getCreatedAt());
        assertTime(userResponse.getUpdatedAt(), userFromDatabase.getUpdatedAt());
        assertTime(userResponse.getDeletedAt(), userFromDatabase.getDeletedAt());
        assertThat(userResponse.getIsAdmin(), is(isAdmin));
        assertThat(userResponse.getRoles(), is(roleNames));
        assertThat(userResponse.getPhoneNumber(), is(itUtil.decrypt(userFromDatabase.getHashedPhoneNumber())));
        assertThat(userPasswordService.isCorrectPassword(newPassword, userFromDatabase.getPassword()), is(true));
        assertThat(userFromDatabase.getIsChangePasswordRequired(), is(false));
    }
}
