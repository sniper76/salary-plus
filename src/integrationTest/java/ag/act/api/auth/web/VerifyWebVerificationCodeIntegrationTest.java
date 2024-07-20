package ag.act.api.auth.web;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.User;
import ag.act.entity.WebVerification;
import ag.act.model.Status;
import ag.act.model.UserResponse;
import ag.act.model.WebVerificationCodeRequest;
import ag.act.model.WebVerificationCodeResponse;
import ag.act.model.WebVerificationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static ag.act.TestUtil.assertTime;
import static ag.act.TestUtil.someWebVerificationCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;

class VerifyWebVerificationCodeIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/auth/web/verify-verification-code";

    private WebVerificationCodeRequest request;
    private UUID authenticationReference;
    private String verificationCode;

    @BeforeEach
    void setUp() {
        itUtil.init();
        authenticationReference = UUID.randomUUID();
    }

    @DisplayName("기존에 생성된 WebVerification 이 없을 때")
    @Nested
    class WhenNoExistingWebVerification {
        @BeforeEach
        void setUp() {
            request = genRequest(authenticationReference);
        }

        @DisplayName("`안심번호를 찾을 수 없습니다.` 404 에러를 리턴한다.")
        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult response = callApi(status().isNotFound());

            itUtil.assertErrorResponse(response, NOT_FOUND_STATUS, "안심번호를 찾을 수 없습니다.");
        }
    }

    @DisplayName("기존에 생성된 WebVerification 이 있을 때")
    @Nested
    class WhenExistingWebVerification {

        @DisplayName("WebVerification 상태가 WAITING 일 때")
        @Nested
        class WhenWaitingStatus {

            @BeforeEach
            void setUp() {
                final WebVerification webVerification = createWaitingWebVerification();
                verificationCode = webVerification.getVerificationCode();

                request = genRequest(authenticationReference, verificationCode);
            }

            @DisplayName("Waiting 상태를 리턴한다.")
            @Test
            void shouldReturnSuccess() throws Exception {
                final WebVerificationCodeResponse result = callApiAndGetResult();

                assertResponse(result);
            }

            private void assertResponse(WebVerificationCodeResponse result) {

                final List<WebVerification> allWebVerifications = getAllWebVerificationsByAuthenticationReference();

                assertThat(allWebVerifications.size(), is(1));

                final WebVerification firstWebVerification = allWebVerifications.get(0);
                assertThat(result.getStatus(), is(WebVerificationStatus.WAITING));
                assertThat(result.getToken(), nullValue());
                assertThat(firstWebVerification.getAuthenticationReference(), is(authenticationReference));
                assertThat(firstWebVerification.getVerificationCode(), is(verificationCode));
            }
        }

        @DisplayName("WebVerification 상태가 EXPIRED 일 때")
        @Nested
        class WhenExpiredStatus {

            @BeforeEach
            void setUp() {
                final WebVerification webVerification = createExpiredWebVerification();

                verificationCode = webVerification.getVerificationCode();

                request = genRequest(authenticationReference, verificationCode);
            }

            @DisplayName("Expired 상태를 리턴한다.")
            @Test
            void shouldReturnSuccess() throws Exception {
                final WebVerificationCodeResponse result = callApiAndGetResult();

                assertResponse(result);
            }

            private void assertResponse(WebVerificationCodeResponse result) {

                final List<WebVerification> allWebVerifications = getAllWebVerificationsByAuthenticationReference();

                assertThat(allWebVerifications.size(), is(1));

                final WebVerification firstWebVerification = allWebVerifications.get(0);
                assertThat(result.getStatus(), is(WebVerificationStatus.EXPIRED));
                assertThat(result.getToken(), nullValue());
                assertThat(firstWebVerification.getAuthenticationReference(), is(authenticationReference));
                assertThat(firstWebVerification.getVerificationCode(), is(verificationCode));
            }
        }

        @DisplayName("WebVerification 상태가 VERIFIED 일 때")
        @Nested
        class WhenVerifiedStatus {

            private Long currentUserId;

            @BeforeEach
            void setUp() {
                final User currentUser = itUtil.createUser();
                currentUserId = currentUser.getId();
                final WebVerification webVerification = createVerifiedWebVerification(currentUser.getId());

                verificationCode = webVerification.getVerificationCode();

                request = genRequest(authenticationReference, verificationCode);
            }

            @DisplayName("Verified 상태를 리턴한다.")
            @Test
            void shouldReturnSuccess() throws Exception {
                final WebVerificationCodeResponse result = callApiAndGetResult();

                assertResponse(result);
            }

            private void assertResponse(WebVerificationCodeResponse result) {

                final List<WebVerification> allWebVerifications = getAllWebVerificationsByAuthenticationReference();

                assertThat(allWebVerifications.size(), is(1));

                final WebVerification firstWebVerification = allWebVerifications.get(0);
                assertThat(result.getStatus(), is(WebVerificationStatus.VERIFIED));
                assertThat(result.getToken(), notNullValue());
                assertThat(firstWebVerification.getAuthenticationReference(), is(authenticationReference));

                assertUserResponse(result.getUser());
            }

            private void assertUserResponse(final UserResponse actualUser) {
                final User dbUser = itUtil.findUser(currentUserId);

                assertThat(actualUser.getId(), is(dbUser.getId()));
                assertThat(actualUser.getEmail(), is(dbUser.getEmail()));
                assertThat(actualUser.getName(), is(dbUser.getName()));
                assertThat(actualUser.getGender(), is(dbUser.getGender()));
                assertThat(actualUser.getIsAgreeToReceiveMail(), is(dbUser.getIsAgreeToReceiveMail()));
                assertThat(actualUser.getNickname(), is(dbUser.getNickname()));
                assertThat(actualUser.getMySpeech(), is(dbUser.getMySpeech()));
                assertThat(actualUser.getJobTitle(), is(dbUser.getJobTitle()));
                assertThat(actualUser.getAddress(), is(dbUser.getAddress()));
                assertThat(actualUser.getAddressDetail(), is(dbUser.getAddressDetail()));
                assertThat(actualUser.getZipcode(), is(dbUser.getZipcode()));
                assertThat(actualUser.getTotalAssetAmount(), is(dbUser.getTotalAssetAmount()));
                assertThat(actualUser.getProfileImageUrl(), is(dbUser.getProfileImageUrl()));
                assertThat(actualUser.getStatus(), is(Status.ACTIVE));
                assertThat(actualUser.getAuthType(), is(dbUser.getAuthType()));
                assertTime(actualUser.getBirthDate(), dbUser.getBirthDate());
                assertTime(actualUser.getLastPinNumberVerifiedAt(), dbUser.getLastPinNumberVerifiedAt());
                assertTime(actualUser.getCreatedAt(), dbUser.getCreatedAt());
                assertTime(actualUser.getUpdatedAt(), dbUser.getUpdatedAt());
                assertTime(actualUser.getDeletedAt(), dbUser.getDeletedAt());
                assertThat(actualUser.getIsPinNumberRegistered(), is(true));
            }
        }
    }

    private List<WebVerification> getAllWebVerificationsByAuthenticationReference() {
        return itUtil.findAllWebVerificationsByAuthenticationReference(authenticationReference);
    }

    private WebVerification createWaitingWebVerification() {
        return itUtil.createWebVerification(authenticationReference);
    }

    private WebVerification createExpiredWebVerification() {
        WebVerification webVerification = createWaitingWebVerification();
        webVerification.setVerificationCodeEndDateTime(LocalDateTime.now().minusMinutes(someIntegerBetween(10, 100)));
        webVerification = itUtil.updateWebVerification(webVerification);
        return webVerification;
    }

    private WebVerification createVerifiedWebVerification(Long userId) {
        WebVerification webVerification = createWaitingWebVerification();
        webVerification.setUserId(userId);
        webVerification.setVerificationCodeRedeemedAt(LocalDateTime.now().minusSeconds(someIntegerBetween(1, 10)));
        webVerification = itUtil.updateWebVerification(webVerification);
        return webVerification;
    }

    private WebVerificationCodeRequest genRequest(UUID authenticationReference, String verificationCode) {
        return new WebVerificationCodeRequest()
            .authenticationReference(authenticationReference)
            .verificationCode(verificationCode);
    }

    private WebVerificationCodeRequest genRequest(UUID authenticationReference) {
        return genRequest(authenticationReference, someWebVerificationCode());
    }

    private WebVerificationCodeResponse callApiAndGetResult() throws Exception {
        MvcResult response = callApi(status().isOk());

        return itUtil.getResult(response, WebVerificationCodeResponse.class);
    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                post(TARGET_API)
                    .content(objectMapperUtil.toRequestBody(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header(X_APP_VERSION, X_APP_VERSION_WEB)
            )
            .andExpect(resultMatcher)
            .andReturn();
    }
}
