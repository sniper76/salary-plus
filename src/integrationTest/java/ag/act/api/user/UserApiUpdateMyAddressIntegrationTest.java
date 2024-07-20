package ag.act.api.user;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.converter.DateTimeConverter;
import ag.act.entity.User;
import ag.act.model.UserDataResponse;
import ag.act.model.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static ag.act.TestUtil.assertTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

class UserApiUpdateMyAddressIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/users/my-address";

    private String jwt;
    private User user;
    private ag.act.model.UpdateMyAddressRequest request;

    @BeforeEach
    void setUp() {
        itUtil.init();
        user = itUtil.createUserBeforePinRegistered();
        jwt = itUtil.createJwt(user.getId());
        request = genUpdateMyAddressRequest();
    }

    private ag.act.model.UpdateMyAddressRequest genUpdateMyAddressRequest() {
        ag.act.model.UpdateMyAddressRequest request = new ag.act.model.UpdateMyAddressRequest();
        request.setAddress(someAlphanumericString(5));
        request.setAddressDetail(someAlphanumericString(5));
        request.setZipcode(someAlphanumericString(5));
        return request;
    }

    @DisplayName("Should return 200 response code when call " + TARGET_API)
    @Test
    void shouldReturnSuccess() throws Exception {
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

        final UserDataResponse result = objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            UserDataResponse.class
        );

        assertResponse(result);
        assertResponseFromDatabase(result);
    }

    private void assertResponseFromDatabase(UserDataResponse result) {
        final UserResponse userResponse = result.getData();

        assertThat(userResponse.getId(), is(user.getId()));
        assertThat(userResponse.getGender(), is(user.getGender()));
        assertThat(userResponse.getNickname(), is(user.getNickname()));
        assertThat(DateTimeConverter.convert(userResponse.getBirthDate()), is(user.getBirthDate()));
        assertThat(userResponse.getIsPinNumberRegistered(), is(user.getHashedPinNumber() != null));
        assertThat(userResponse.getTotalAssetAmount(), is(user.getTotalAssetAmount()));
        assertThat(userResponse.getProfileImageUrl(), is(user.getProfileImageUrl()));
        assertThat(userResponse.getStatus(), is(user.getStatus()));
        assertThat(userResponse.getAuthType(), is(user.getAuthType()));
        assertTime(userResponse.getCreatedAt(), user.getCreatedAt());
        assertTime(userResponse.getUpdatedAt(), greaterThanOrEqualTo(userResponse.getCreatedAt()));
        assertTime(userResponse.getDeletedAt(), user.getDeletedAt());
    }

    private void assertResponse(UserDataResponse result) {
        final UserResponse userResponse = result.getData();
        assertThat(userResponse.getAddress(), is(request.getAddress()));
        assertThat(userResponse.getAddressDetail(), is(request.getAddressDetail()));
        assertThat(userResponse.getZipcode(), is(request.getZipcode()));
    }

}
