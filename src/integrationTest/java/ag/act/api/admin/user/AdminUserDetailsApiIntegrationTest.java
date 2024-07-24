package ag.act.api.admin.user;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.User;
import ag.act.model.UserDataResponse;
import ag.act.model.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static ag.act.TestUtil.assertTime;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomBooleans.someBoolean;

class AdminUserDetailsApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/users/{userId}";

    private String jwt;
    private User user;
    private Long userId;
    private final Boolean isSolidarityLeaderConfidentialAgreementSigned = someBoolean();

    @BeforeEach
    void setUp() {
        itUtil.init();
        final User adminUser = itUtil.createAdminUser();
        jwt = itUtil.createJwt(adminUser.getId());
        user = itUtil.createUser();
        userId = user.getId();

        user.setIsSolidarityLeaderConfidentialAgreementSigned(isSolidarityLeaderConfidentialAgreementSigned);
        user = itUtil.updateUser(user);
    }

    @Nested
    class WhenGetUserDetails {

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            assertResponse(callApi());
        }

    }

    @Nested
    class WhenGetUserDetailsWithoutUserBadgeVisibility {
        @DisplayName("Should return all visibility to true when user badge visibility data not exist")
        @Test
        void shouldReturnAllTrue() throws Exception {
            UserDataResponse response = callApi();

            assertResponse(response);

            response.getData()
                .getUserBadgeVisibilities()
                .forEach(userBadgeVisibilityResponse ->
                    assertThat(userBadgeVisibilityResponse.getIsVisible(), is(true))
                );
        }
    }

    @Nested
    class WhenGetUserDetailsWithUserBadgeVisibility {

        Map<String, Boolean> userBadgeVisibilityMap;

        @BeforeEach
        void setUp() {
            itUtil.createUserBadgeVisibility(user.getId());
            userBadgeVisibilityMap = itUtil.getUserBadgeVisibilityMapByUserId(user.getId());
        }

        @DisplayName("Should return All visibility data when user badge visibility data exist")
        @Test
        void shouldReturnAllVisibility() throws Exception {
            UserDataResponse response = callApi();

            assertResponse(response);

            response.getData()
                .getUserBadgeVisibilities()
                .forEach(userBadgeVisibilityResponse ->
                    assertThat(userBadgeVisibilityResponse.getIsVisible(),
                        is(userBadgeVisibilityMap.get(userBadgeVisibilityResponse.getLabel())))
                );
        }
    }

    private ag.act.model.UserDataResponse callApi() throws Exception {
        MvcResult response = mockMvc
            .perform(
                get(TARGET_API, userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(status().isOk())
            .andReturn();

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            ag.act.model.UserDataResponse.class
        );
    }

    private void assertResponse(ag.act.model.UserDataResponse result) {
        final UserResponse userResponse = result.getData();

        assertThat(userResponse.getId(), is(user.getId()));
        assertThat(userResponse.getNickname(), is(user.getNickname()));
        assertThat(userResponse.getGender(), is(user.getGender()));
        assertThat(userResponse.getMySpeech(), is(user.getMySpeech()));
        assertThat(userResponse.getIsAgreeToReceiveMail(), is(user.getIsAgreeToReceiveMail()));
        assertThat(userResponse.getIsPinNumberRegistered(), is(user.getHashedPinNumber() != null));
        assertThat(userResponse.getJobTitle(), is(user.getJobTitle()));
        assertThat(userResponse.getAddress(), is(user.getAddress()));
        assertThat(userResponse.getAddressDetail(), is(user.getAddressDetail()));
        assertThat(userResponse.getZipcode(), is(user.getZipcode()));
        assertThat(userResponse.getTotalAssetAmount(), is(user.getTotalAssetAmount()));
        assertThat(userResponse.getProfileImageUrl(), is(user.getProfileImageUrl()));
        assertThat(userResponse.getStatus(), is(user.getStatus()));
        assertThat(userResponse.getIsAdmin(), is(user.isAdmin()));
        assertThat(userResponse.getPhoneNumber(), is(itUtil.decrypt(user.getHashedPhoneNumber())));
        assertThat(userResponse.getIsSolidarityLeaderConfidentialAgreementSigned(), is(isSolidarityLeaderConfidentialAgreementSigned));

        assertTime(userResponse.getBirthDate(), user.getBirthDate());
        assertTime(userResponse.getCreatedAt(), user.getCreatedAt());
        assertTime(userResponse.getUpdatedAt(), user.getUpdatedAt());
        assertTime(userResponse.getDeletedAt(), user.getDeletedAt());
        assertTime(userResponse.getEditedAt(), user.getEditedAt());
    }
}
