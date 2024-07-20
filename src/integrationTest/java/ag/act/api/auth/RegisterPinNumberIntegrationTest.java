package ag.act.api.auth;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.User;
import ag.act.entity.UserVerificationHistory;
import ag.act.enums.verification.VerificationType;
import ag.act.model.Status;
import ag.act.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static ag.act.TestUtil.assertTime;
import static ag.act.enums.verification.VerificationOperationType.REGISTER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someNumericString;

class RegisterPinNumberIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/auth/register-pin-number";

    @Autowired
    private UserRepository userRepository;

    private ag.act.model.PinNumberRequest request;
    private Long userId;
    private String jwt;

    @BeforeEach
    void setUp() {
        itUtil.init();
        final User user = itUtil.createUserBeforePinRegistered();
        userId = user.getId();
        jwt = itUtil.createJwt(userId);
        request = genRequest();
    }

    @DisplayName("Should return 200 response code when call " + TARGET_API)
    @Test
    void shouldReturnSuccess() throws Exception {
        MvcResult response = mockMvc
            .perform(
                post(TARGET_API)
                    .content(objectMapperUtil.toRequestBody(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(status().isOk())
            .andReturn();

        final ag.act.model.UserResponse result = objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            ag.act.model.UserResponse.class
        );

        assertResponse(result);
    }

    private void assertResponse(ag.act.model.UserResponse result) {
        final User dbUser = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("[TEST] User를 찾을 수 없습니다."));
        assertThat(result.getId(), is(dbUser.getId()));
        assertThat(result.getEmail(), is(dbUser.getEmail()));
        assertThat(result.getName(), is(dbUser.getName()));
        assertThat(result.getGender(), is(dbUser.getGender()));
        assertThat(result.getIsAgreeToReceiveMail(), is(dbUser.getIsAgreeToReceiveMail()));
        assertThat(result.getNickname(), is(dbUser.getNickname()));
        assertThat(result.getMySpeech(), is(dbUser.getMySpeech()));
        assertThat(result.getJobTitle(), is(dbUser.getJobTitle()));
        assertThat(result.getAddress(), is(dbUser.getAddress()));
        assertThat(result.getAddressDetail(), is(dbUser.getAddressDetail()));
        assertThat(result.getZipcode(), is(dbUser.getZipcode()));
        assertThat(result.getTotalAssetAmount(), is(dbUser.getTotalAssetAmount()));
        assertThat(result.getProfileImageUrl(), startsWith(itUtil.getDefaultProfileImage()));
        assertThat(result.getProfileImageUrl(), startsWith(dbUser.getProfileImageUrl()));
        assertThat(result.getStatus(), is(Status.ACTIVE));
        assertThat(result.getAuthType(), is(dbUser.getAuthType()));
        assertTime(result.getBirthDate(), dbUser.getBirthDate());
        assertTime(result.getLastPinNumberVerifiedAt(), dbUser.getLastPinNumberVerifiedAt());
        assertTime(result.getCreatedAt(), dbUser.getCreatedAt());
        assertTime(result.getUpdatedAt(), dbUser.getUpdatedAt());
        assertTime(result.getDeletedAt(), dbUser.getDeletedAt());
        assertThat(result.getIsPinNumberRegistered(), is(true));

        final UserVerificationHistory userVerificationHistory = itUtil.findFirstVerificationHistoryRepository(userId);
        assertThat(userVerificationHistory.getVerificationType(), is(VerificationType.PIN));
        assertThat(userVerificationHistory.getOperationType(), is(REGISTER));
        assertThat(userVerificationHistory.getUserIp(), is("127.0.0.1"));
    }

    private ag.act.model.PinNumberRequest genRequest() {

        String pinNumber = someNumericString(6);

        ag.act.model.PinNumberRequest request = new ag.act.model.PinNumberRequest();
        request.setPinNumber(pinNumber);

        return request;
    }
}
