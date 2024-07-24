package ag.act.api.admin.email;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.model.SenderEmailRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import static ag.act.TestUtil.someEmail;
import static ag.act.TestUtil.someHtmlContent;
import static ag.act.itutil.authentication.AuthenticationTestUtil.xApiKey;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

class PublicEmailIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/public-api/email";

    private SenderEmailRequest request;

    @Value("${act.public.api-key}")
    private String publicApiKey;

    @BeforeEach
    void setUp() {
        itUtil.init();
    }

    @Nested
    class WhenError {

        @Nested
        class WhenSenderEmailError {

            @BeforeEach
            void setUp() {
                request = genRequest(someAlphanumericString(10), someEmail());
            }

            @Test
            void shouldReturnSuccess() throws Exception {
                final MvcResult mvcResult = callApi(status().isBadRequest(), request);

                itUtil.assertErrorResponse(mvcResult, 400, "형식에 맞지 않는 이메일입니다. ex) act123@naver.com");
            }
        }

        @Nested
        class WhenReceiverEmailError {

            @BeforeEach
            void setUp() {
                request = genRequest(someEmail(), someAlphanumericString(10));
            }

            @Test
            void shouldReturnSuccess() throws Exception {
                final MvcResult mvcResult = callApi(status().isBadRequest(), request);

                itUtil.assertErrorResponse(mvcResult, 400, "형식에 맞지 않는 이메일입니다. ex) act123@naver.com");
            }
        }
    }

    @Nested
    class WhenSuccess {

        @BeforeEach
        void setUp() {
            request = genRequest(someEmail(), someEmail());
        }

        @Test
        void shouldReturnSuccess() throws Exception {
            itUtil.assertSimpleOkay(callApi(status().isOk(), request));
        }
    }

    private MvcResult callApi(ResultMatcher resultMatcher, SenderEmailRequest request1) throws Exception {
        return mockMvc
            .perform(
                post(TARGET_API)
                    .content(objectMapperUtil.toJson(request1))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(xApiKey(publicApiKey)))
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    private SenderEmailRequest genRequest(String sender, String receiver) {
        return new SenderEmailRequest()
            .recipientEmail(receiver)
            .senderEmail(sender)
            .subject(someAlphanumericString(10))
            .content(someHtmlContent().html());
    }
}
