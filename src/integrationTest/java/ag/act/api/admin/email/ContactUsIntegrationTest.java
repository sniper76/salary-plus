package ag.act.api.admin.email;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.model.ContactUsRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;
import software.amazon.awssdk.services.ses.model.SendEmailResponse;

import static ag.act.TestUtil.someEmail;
import static ag.act.TestUtil.someHtmlContent;
import static ag.act.TestUtil.somePhoneNumber;
import static ag.act.itutil.authentication.AuthenticationTestUtil.xAppVersion;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

class ContactUsIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/contact-us";

    private ContactUsRequest request;

    @BeforeEach
    void setUp() {
        itUtil.init();
    }

    @Nested
    class WhenError {

        @Nested
        class WhenApiVersionIsNotWEB {

            @BeforeEach
            void setUp() {
                request = genRequest(someEmail());
            }

            @Test
            void shouldReturnError() throws Exception {
                final String wrongXAppVersion = someAlphanumericString(10);
                final MvcResult mvcResult = callApi(status().isUnauthorized(), request, wrongXAppVersion);

                itUtil.assertErrorResponse(mvcResult, UNAUTHORIZED_STATUS, "인가되지 않은 접근입니다.");
            }
        }

        @Nested
        class WhenSenderEmailError {

            @BeforeEach
            void setUp() {
                request = genRequest(someAlphanumericString(10));
            }

            @Test
            void shouldReturnError() throws Exception {
                final MvcResult mvcResult = callApi(status().isBadRequest(), request, X_APP_VERSION_WEB);

                itUtil.assertErrorResponse(mvcResult, BAD_REQUEST_STATUS, "형식에 맞지 않는 이메일입니다. ex) act123@naver.com");
            }
        }

        @Nested
        class WhenPhoneNumberError {

            @BeforeEach
            void setUp() {
                request = genRequest(someEmail());
                request.setPhoneNumber(someAlphanumericString(11));
            }

            @Test
            void shouldReturnError() throws Exception {
                final MvcResult mvcResult = callApi(status().isBadRequest(), request, X_APP_VERSION_WEB);

                itUtil.assertErrorResponse(mvcResult, BAD_REQUEST_STATUS, "휴대폰번호를 확인해주세요.");
            }
        }

        @Nested
        class WhenSecretKeyError {

            @BeforeEach
            void setUp() {
                request = genRequest(someEmail());

                given(recaptchaVerifier.verifyCaptcha(request.getRecaptchaResponse())).willReturn(false);
            }

            @Test
            void shouldReturnError() throws Exception {
                final MvcResult mvcResult = callApi(status().isBadRequest(), request, X_APP_VERSION_WEB);

                itUtil.assertErrorResponse(mvcResult, BAD_REQUEST_STATUS, "잘못된 Captcha 인증 요청입니다.");
            }
        }
    }

    @Nested
    class WhenSuccess {

        @Mock
        private SendEmailResponse sendEmailResponse;

        @BeforeEach
        void setUp() {
            request = genRequest(someEmail());

            given(sesService.sendEmail(any(SendEmailRequest.class))).willReturn(sendEmailResponse);
            given(recaptchaVerifier.verifyCaptcha(request.getRecaptchaResponse())).willReturn(true);
        }

        @Test
        void shouldReturnSuccess() throws Exception {
            itUtil.assertSimpleOkay(callApi(status().isOk(), request, X_APP_VERSION_WEB));
            then(sesService).should().sendEmail(any(SendEmailRequest.class));
        }
    }

    @SuppressWarnings("checkstyle:ParameterName")
    private MvcResult callApi(ResultMatcher resultMatcher, ContactUsRequest request, String xAppVersion) throws Exception {
        return mockMvc
            .perform(
                post(TARGET_API)
                    .content(objectMapperUtil.toJson(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(xAppVersion(xAppVersion)))
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    private ContactUsRequest genRequest(String sender) {
        return new ContactUsRequest()
            .recaptchaResponse(someAlphanumericString(10))
            .senderEmail(sender)
            .senderName(someAlphanumericString(10))
            .phoneNumber(somePhoneNumber())
            .content(someHtmlContent().html());
    }
}
