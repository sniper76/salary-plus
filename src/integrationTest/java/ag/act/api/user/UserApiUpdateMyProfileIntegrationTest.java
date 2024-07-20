package ag.act.api.user;


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

import java.util.List;
import java.util.Map;

import static ag.act.TestUtil.assertTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomBooleans.someBoolean;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

class UserApiUpdateMyProfileIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/users/me";

    private String jwt;
    private User user;
    private ag.act.model.UpdateMyProfileRequest request;
    private Map<String, Boolean> badgeVisibilityMap;

    @BeforeEach
    void setUp() {
        itUtil.init();
        user = itUtil.createUserBeforePinRegistered();
        jwt = itUtil.createJwt(user.getId());
        request = genUpdateMyProfileRequest();
    }

    private ag.act.model.UpdateMyProfileRequest genUpdateMyProfileRequest() {
        ag.act.model.UpdateMyProfileRequest request = new ag.act.model.UpdateMyProfileRequest();
        request.setJobTitle(someAlphanumericString(5));
        request.setMySpeech(someAlphanumericString(5));
        request.setAddress(someAlphanumericString(5));
        request.setAddressDetail(someAlphanumericString(5));
        request.setZipcode(someAlphanumericString(5));
        request.setIsAgreeToReceiveMail(someBoolean());
        return request;
    }

    @Nested
    class Success {
        @Nested
        class Normal {
            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                final UserDataResponse result = callApi();

                assertResponse(result);
                assertResponseFromDatabase(result);
            }
        }

        @Nested
        class AndUserBadgeVisibility {
            @BeforeEach
            void setUp() {
                request.isVisibilityStockQuantity(someBoolean());
                request.isVisibilityTotalAsset(someBoolean());
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                final UserDataResponse result = callApi();

                assertResponse(result);
                assertResponseFromDatabase(result);
            }
        }
    }

    private UserDataResponse callApi() throws Exception {
        MvcResult response = mockMvc
            .perform(
                patch(TARGET_API)
                    .content(objectMapperUtil.toJson(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(status().isOk())
            .andReturn();

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            UserDataResponse.class
        );
    }

    private void assertResponseFromDatabase(ag.act.model.UserDataResponse result) {
        final UserResponse userResponse = result.getData();
        assertThat(userResponse.getId(), is(user.getId()));
        assertThat(userResponse.getGender(), is(user.getGender()));
        assertThat(userResponse.getNickname(), is(user.getNickname()));
        assertTime(userResponse.getBirthDate(), user.getBirthDate());
        assertThat(userResponse.getIsPinNumberRegistered(), is(user.getHashedPinNumber() != null));
        assertThat(userResponse.getTotalAssetAmount(), is(user.getTotalAssetAmount()));
        assertThat(userResponse.getProfileImageUrl(), is(user.getProfileImageUrl()));
        assertThat(userResponse.getStatus(), is(user.getStatus()));
        assertThat(userResponse.getAuthType(), is(user.getAuthType()));
        assertTime(userResponse.getCreatedAt(), user.getCreatedAt());
        assertTime(userResponse.getUpdatedAt(), greaterThanOrEqualTo(userResponse.getCreatedAt()));
        assertTime(userResponse.getDeletedAt(), user.getDeletedAt());
        assertUserBadgeVisibility(userResponse.getUserBadgeVisibilities());
    }

    private void assertUserBadgeVisibility(
        List<ag.act.model.UserBadgeVisibilityResponse> userBadgeVisibilityResponses
    ) {
        badgeVisibilityMap = Map.of(
            "isVisibilityStockQuantity", request.getIsVisibilityStockQuantity(),
            "isVisibilityTotalAsset", request.getIsVisibilityTotalAsset()
        );

        userBadgeVisibilityResponses
            .forEach(response -> assertThat(response.getIsVisible(), is(badgeVisibilityMap.get(response.getName()))));
    }

    private void assertResponse(UserDataResponse result) {
        final UserResponse userResponse = result.getData();
        assertThat(userResponse.getJobTitle(), is(request.getJobTitle()));
        assertThat(userResponse.getMySpeech(), is(request.getMySpeech()));
        assertThat(userResponse.getAddress(), is(request.getAddress()));
        assertThat(userResponse.getAddressDetail(), is(request.getAddressDetail()));
        assertThat(userResponse.getZipcode(), is(request.getZipcode()));
        assertThat(userResponse.getIsAgreeToReceiveMail(), is(request.getIsAgreeToReceiveMail()));
    }
}
