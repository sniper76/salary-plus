package ag.act.api.auth.web;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.WebVerification;
import ag.act.model.WebVerificationCodeGenerateRequest;
import ag.act.model.WebVerificationCodeGenerateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static ag.act.TestUtil.assertTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;

class GenerateWebVerificationCodeRequestIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/auth/web/generate-verification-code";

    private WebVerificationCodeGenerateRequest request;
    private UUID authenticationReference;

    @BeforeEach
    void setUp() {
        itUtil.init();
        authenticationReference = UUID.randomUUID();
        request = genRequest(authenticationReference);
    }

    @DisplayName("기존에 생성된 WebVerification 이 없을 때")
    @Nested
    class WhenNoExistingWebVerification {

        @DisplayName("새로운 WebVerification 이 생성된다.")
        @Test
        void shouldReturnSuccess() throws Exception {
            final WebVerificationCodeGenerateResponse result = callApiAndGetResult();

            assertResponse(result);
        }
    }

    @DisplayName("기존에 생성된 WebVerification 이 있을 때")
    @Nested
    class WhenExistingWebVerification {

        private int existingWebVerificationCount;

        @BeforeEach
        void setUp() {
            itUtil.init();
            authenticationReference = UUID.randomUUID();
            request = genRequest(authenticationReference);
            existingWebVerificationCount = createExistingWebVerifications();
        }

        @DisplayName("새로운 WebVerification 을 생성하고, 기존에 생성된 WebVerification 들은 만료된다.")
        @Test
        void shouldReturnSuccess() throws Exception {
            final WebVerificationCodeGenerateResponse result = callApiAndGetResult();

            assertResponse(result);
            assertExistingWebVerifications();
        }

        private int createExistingWebVerifications() {
            final int existingWebVerificationCount = someIntegerBetween(1, 3);
            IntStream.range(0, existingWebVerificationCount)
                .forEach(i -> itUtil.createWebVerification(authenticationReference));

            return existingWebVerificationCount;
        }

        private void assertExistingWebVerifications() {
            final List<WebVerification> allWebVerifications = getAllSortedWebVerifications();

            assertThat(allWebVerifications.size(), is(existingWebVerificationCount + 1));

            for (int i = 0; i < existingWebVerificationCount; i++) {
                final WebVerification webVerification = allWebVerifications.get(i);
                assertThat(LocalDateTime.now().isAfter(webVerification.getVerificationCodeEndDateTime()), is(Boolean.TRUE));
            }
        }

        private List<WebVerification> getAllSortedWebVerifications() {
            return itUtil.findAllWebVerificationsByAuthenticationReference(authenticationReference)
                .stream()
                .sorted(Comparator.comparing(WebVerification::getVerificationCodeEndDateTime))
                .toList();
        }
    }

    @DisplayName("요청 Body 에 필수 값이 없을 때")
    @Nested
    class WhenMissingValuesInRequestBody {

        @BeforeEach
        void setUp() {
            itUtil.init();
            request = new WebVerificationCodeGenerateRequest();
        }

        @DisplayName("400 Bad Request 가 반환된다.")
        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult response = callApi(status().isBadRequest());

            itUtil.assertErrorResponse(response, BAD_REQUEST_STATUS, "인증참조키를 확인하세요.");
        }
    }

    private void assertResponse(WebVerificationCodeGenerateResponse result) {
        final List<WebVerification> allWebVerifications = getValidWebVerifications();

        assertThat(allWebVerifications.size(), is(1));

        final WebVerification firstWebVerification = allWebVerifications.get(0);
        assertThat(result.getVerificationCode(), is(firstWebVerification.getVerificationCode()));
        assertTime(result.getExpirationDateTime(), firstWebVerification.getVerificationCodeEndDateTime());

        assertThat(firstWebVerification.getAuthenticationReference(), is(authenticationReference));
    }

    private List<WebVerification> getValidWebVerifications() {
        return itUtil.findAllWebVerificationsByAuthenticationReference(authenticationReference)
            .stream()
            .filter(webVerification -> LocalDateTime.now().isBefore(webVerification.getVerificationCodeEndDateTime()))
            .toList();
    }

    private WebVerificationCodeGenerateRequest genRequest(final UUID authenticationReference) {
        return new WebVerificationCodeGenerateRequest()
            .authenticationReference(authenticationReference);
    }

    private WebVerificationCodeGenerateResponse callApiAndGetResult() throws Exception {
        MvcResult response = callApi(status().isOk());

        return itUtil.getResult(response, WebVerificationCodeGenerateResponse.class);
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
