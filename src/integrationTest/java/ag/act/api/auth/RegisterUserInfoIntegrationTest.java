package ag.act.api.auth;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.User;
import ag.act.enums.verification.VerificationOperationType;
import ag.act.enums.verification.VerificationType;
import ag.act.model.Status;
import ag.act.repository.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.Objects;
import java.util.Optional;

import static ag.act.TestUtil.someEmail;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomBooleans.someBoolean;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

class RegisterUserInfoIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/auth/register-user-info";

    @Autowired
    private UserRepository userRepository;

    private ag.act.model.RegisterUserInfoRequest request;
    private String email;
    private String nickname;
    private Boolean isAgreeToReceiveMail;
    private Long userId;
    private String jwt;

    @BeforeEach
    void setUp() {
        itUtil.init();
        final User user = itUtil.createUserAfterVerifyAuthCode();
        userId = user.getId();
        jwt = itUtil.createJwt(userId);
        email = someEmail();
        nickname = someAlphanumericString(20).trim();
    }

    @Nested
    class WhenRegisterUserInfo {

        @BeforeEach
        void setUp() {
            request = genRequest(email, nickname);
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final MvcResult response = callApi(status().isOk());

            final ag.act.model.UserResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                ag.act.model.UserResponse.class
            );

            assertResponse(result);
            assertUserFromDatabase();
            assertUserVerificationHistory();
        }

        private void assertUserVerificationHistory() {
            assertThat(
                itUtil.findAllUserVerificationHistories()
                    .stream()
                    .anyMatch(it -> it.getVerificationType() == VerificationType.USER
                        && it.getOperationType() == VerificationOperationType.REGISTER
                        && Objects.equals(it.getUserId(), userId)),
                is(true)
            );
        }

        private void assertResponse(ag.act.model.UserResponse result) {
            assertThat(result.getId(), is(userId));
            assertThat(result.getEmail(), is(email));
            assertThat(result.getIsAgreeToReceiveMail(), is(isAgreeToReceiveMail));
            assertThat(result.getNickname(), is(nickname));
            assertThat(result.getProfileImageUrl(), startsWith(s3Environment.getBaseUrl()));
            assertThat(result.getStatus(), is(Status.ACTIVE));
        }

        private void assertUserFromDatabase() {
            final Optional<User> userOptional = userRepository.findById(userId);
            assertThat(userOptional.isPresent(), is(true));
            assertThat(userOptional.get().getEmail(), is(email));
            assertThat(userOptional.get().getIsAgreeToReceiveMail(), is(isAgreeToReceiveMail));
            assertThat(userOptional.get().getNickname(), is(nickname));
            assertThat(userOptional.get().getStatus(), is(Status.ACTIVE));
        }

    }

    @Nested
    class WhenAlreadyUsedEmailProvided {

        @BeforeEach
        void setUp() {
            email = someEmail();
            request = genRequest(email, nickname);

            createAnotherUserWhoUseTheSameEmail();
        }

        @DisplayName("Should return 400 response code when email is already used")
        @Test
        void shouldReturnBadRequest() throws Exception {
            final MvcResult response = callApi(status().isBadRequest());

            itUtil.assertErrorResponse(response, 400, "이미 사용중인 이메일입니다.");
        }
    }

    @Nested
    class WhenAlreadyUsedNicknameProvided {

        @BeforeEach
        void setUp() {
            email = someEmail();
            request = genRequest(email, nickname);

            createAnotherUserWhoUseTheSameNickname();
        }

        @DisplayName("Should return 400 response code when nickname is already used")
        @Test
        void shouldReturnBadRequest() throws Exception {
            final MvcResult response = callApi(status().isBadRequest());

            itUtil.assertErrorResponse(response, 400, "이미 사용중인 닉네임입니다.");
        }


    }

    private void createAnotherUserWhoUseTheSameEmail() {
        final User anotherUser = itUtil.createUser();
        anotherUser.setEmail(email); // set the same email.
        itUtil.updateUser(anotherUser);
    }

    private void createAnotherUserWhoUseTheSameNickname() {
        final User anotherUser = itUtil.createUser();
        anotherUser.setNickname(nickname); // set the same nickname.
        itUtil.updateUser(anotherUser);
    }

    @NotNull
    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                post(TARGET_API)
                    .content(objectMapperUtil.toRequestBody(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    private ag.act.model.RegisterUserInfoRequest genRequest(String email, String nickname) {

        isAgreeToReceiveMail = someBoolean();

        ag.act.model.RegisterUserInfoRequest request = new ag.act.model.RegisterUserInfoRequest();
        request.setEmail(email);
        request.setNickname(nickname);
        request.setIsAgreeToReceiveMail(isAgreeToReceiveMail);

        return request;
    }
}
