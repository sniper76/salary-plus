package ag.act.api.auth.web;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.User;
import ag.act.entity.UserVerificationHistory;
import ag.act.entity.WebVerification;
import ag.act.enums.verification.VerificationOperationType;
import ag.act.enums.verification.VerificationType;
import ag.act.model.SimpleStringResponse;
import ag.act.model.VerificationCodeRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDateTime;
import java.util.UUID;

import static ag.act.TestUtil.someWebVerificationCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UpdateWebVerificationCodeIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/auth/web/redeem-verification-code";

    private VerificationCodeRequest request;
    private UUID authenticationReference;
    private String userJwt;
    private User user;

    @BeforeEach
    void setUp() {
        itUtil.init();
        user = itUtil.createUser();
        userJwt = itUtil.createJwt(user.getId());
        authenticationReference = UUID.randomUUID();
    }

    @Nested
    class WhenError {
        @DisplayName("개인안심번호 불일치")
        @Nested
        class WhenNotFoundVerificationCode {
            @BeforeEach
            void setUp() {
                request = genRequest(someWebVerificationCode());
            }

            @DisplayName("`개인안심번호를 다시 입력해주세요.` 4002 에러를 리턴한다.")
            @Test
            void shouldReturnError() throws Exception {
                MvcResult response = callApi(status().isBadRequest(), request);

                itUtil.assertErrorResponse(response, 400, 4002, "개인안심번호를 다시 입력해주세요.");
            }
        }

        @DisplayName("안심 수단 기간 만료")
        @Nested
        class WhenVerificationCodeOverThreeMinute {
            @BeforeEach
            void setUp() {
                final LocalDateTime now = LocalDateTime.now();
                final WebVerification webVerification = itUtil.createWebVerification(authenticationReference);
                webVerification.setVerificationCodeEndDateTime(now.minusSeconds(10));
                itUtil.updateWebVerification(webVerification);

                request = genRequest(webVerification.getVerificationCode());
            }

            @DisplayName("`인증 수단의 기간이 만료되어 재생성이 필요합니다.` 4003 에러를 리턴한다.")
            @Test
            void shouldReturnError() throws Exception {
                MvcResult response = callApi(status().isBadRequest(), request);

                itUtil.assertErrorResponse(response, 400, 4003, "인증 수단의 기간이 만료되어 재생성이 필요합니다.");
            }
        }
    }

    @Nested
    class WhenSuccess {
        @BeforeEach
        void setUp() {
            final LocalDateTime now = LocalDateTime.now();
            final WebVerification webVerification = itUtil.createWebVerification(authenticationReference);
            webVerification.setVerificationCodeEndDateTime(now.plusSeconds(10));
            itUtil.updateWebVerification(webVerification);

            request = genRequest(webVerification.getVerificationCode());
        }

        @Test
        void shouldReturnSuccess() throws Exception {
            SimpleStringResponse response = callApiAndGetResult(request);

            assertThat(response.getStatus(), is("ok"));

            itUtil.findWebVerificationByAuthenticationReference(authenticationReference)
                .ifPresent(webVerification -> {
                    assertThat(webVerification.getUserId(), is(user.getId()));
                    assertThat(webVerification.getVerificationCodeRedeemedAt(), notNullValue());
                });

            final UserVerificationHistory userVerificationHistory = itUtil.findFirstVerificationHistoryRepository(user.getId());
            assertThat(userVerificationHistory.getVerificationType(), is(VerificationType.WEB));
            assertThat(userVerificationHistory.getOperationType(), is(VerificationOperationType.VERIFICATION));
        }
    }

    private VerificationCodeRequest genRequest(String verificationCode) {
        return new VerificationCodeRequest()
            .verificationCode(verificationCode);
    }

    private SimpleStringResponse callApiAndGetResult(VerificationCodeRequest verificationCodeRequest) throws Exception {
        MvcResult response = callApi(status().isOk(), verificationCodeRequest);

        return itUtil.getResult(response, SimpleStringResponse.class);
    }

    private MvcResult callApi(ResultMatcher resultMatcher, VerificationCodeRequest verificationCodeRequest) throws Exception {
        return mockMvc
            .perform(
                post(TARGET_API)
                    .content(objectMapperUtil.toRequestBody(verificationCodeRequest))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + userJwt)
                    .header("X-APP-VERSION", "APP")
            )
            .andExpect(resultMatcher)
            .andReturn();
    }
}
