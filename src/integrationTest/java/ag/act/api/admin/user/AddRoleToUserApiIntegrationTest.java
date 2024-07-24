package ag.act.api.admin.user;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.converter.DateTimeConverter;
import ag.act.entity.User;
import ag.act.enums.RoleType;
import ag.act.model.AddRoleToUserRequest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class AddRoleToUserApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/users/{userId}/add-role";

    private String jwt;
    private Long userId;
    private AddRoleToUserRequest request;

    @BeforeEach
    void setUp() {
        itUtil.init();
        final User adminUser = itUtil.createUserWithRole(RoleType.SUPER_ADMIN);
        jwt = itUtil.createJwt(adminUser.getId());

        final User user = itUtil.createUser();
        userId = user.getId();
        itUtil.createUserRole(user, RoleType.USER);
    }

    @Nested
    class AddToRoleSuccessfully {

        @BeforeEach
        void setUp() {
            request = genRequest(RoleType.ADMIN.name());
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final MvcResult response = callApi(status().isOk());

            assertResponse(getResponse(response));
        }
    }

    @Nested
    class FailToAddToRole {

        @Nested
        class WhenAddSameRoleAgain {

            @BeforeEach
            void setUp() {
                request = genRequest(RoleType.USER.name());
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequest() throws Exception {
                final MvcResult response = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(response, 400, "이미 해당 권한을 가지고 있습니다.");
            }
        }

        @Nested
        class WhenNotFoundRoleType {

            private String roleTypeName;

            @BeforeEach
            void setUp() {
                roleTypeName = someString(10);
                request = genRequest(roleTypeName);
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequest() throws Exception {
                final MvcResult response = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(response, 400, "지원하지 않는 RoleType '%s' 타입입니다.".formatted(roleTypeName));
            }
        }

        @Nested
        class WhenNotFoundUser {

            @BeforeEach
            void setUp() {
                userId = someLong();
                request = genRequest(RoleType.ADMIN.name());
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequest() throws Exception {
                final MvcResult response = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(response, 400, "회원을 찾을 수 없습니다.");
            }
        }

        @Nested
        class WhenNotSuperAdmin {

            @BeforeEach
            void setUp() {
                final User adminUser = itUtil.createAdminUser();
                jwt = itUtil.createJwt(adminUser.getId());
                request = genRequest(RoleType.ADMIN.name());
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequest() throws Exception {
                final MvcResult response = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(response, 400, "슈퍼 관리자만 가능한 기능입니다.");
            }
        }
    }

    private AddRoleToUserRequest genRequest(String roleTypeName) {
        return new AddRoleToUserRequest().roleType(roleTypeName);
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
                patch(TARGET_API, userId)
                    .content(objectMapperUtil.toJson(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    private void assertResponse(ag.act.model.UserDataResponse result) {
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
        assertThat(userResponse.getIsAdmin(), is(true));
        assertThat(userResponse.getRoles(), is(List.of(RoleType.USER.name(), RoleType.ADMIN.name())));
        assertThat(userResponse.getPhoneNumber(), is(itUtil.decrypt(userFromDatabase.getHashedPhoneNumber())));
    }
}
