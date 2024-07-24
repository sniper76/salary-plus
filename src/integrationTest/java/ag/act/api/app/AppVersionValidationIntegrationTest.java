package ag.act.api.app;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.dto.AppVersion;
import ag.act.entity.User;
import ag.act.enums.AppPreferenceType;
import ag.act.model.CheckEmailResponse;
import ag.act.model.SimpleStringResponse;
import ag.act.model.WebVerificationCodeGenerateRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import static ag.act.TestUtil.someEmail;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AppVersionValidationIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/auth/check-email";

    private String jwt;
    private ag.act.model.CheckEmailRequest request;
    private final String correctAppVersion = AppPreferenceType.MIN_APP_VERSION.getDefaultValue();

    @BeforeEach
    void setUp() {
        itUtil.init();
        User user = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());
    }

    @AfterEach
    void tearDown() {
        setUpAppPreferenceCacheMocks();
    }

    private CheckEmailResponse getResponse(MvcResult response)
        throws JsonProcessingException, UnsupportedEncodingException {

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            CheckEmailResponse.class
        );
    }

    private MvcResult callApi(String appVersion, ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                post(TARGET_API)
                    .content(objectMapperUtil.toRequestBody(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
                    .header(X_APP_VERSION, appVersion)
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    private void assertResponse(CheckEmailResponse result) {
        assertThat(result.getData().getCanUse(), is(true));
    }

    @Nested
    class WhenCorrectAppVersionProvided {

        @BeforeEach
        void setUp() {
            request = new ag.act.model.CheckEmailRequest().email(someEmail());
        }

        @DisplayName("Should return 200 response code with `true` when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final CheckEmailResponse result = getResponse(callApi(correctAppVersion, status().isOk()));
            assertResponse(result);
        }
    }

    @Nested
    class WhenWrongAppVersionProvided {

        @BeforeEach
        void setUp() {
            request = new ag.act.model.CheckEmailRequest().email(someEmail());
        }

        @DisplayName("Should return 426 response when call " + TARGET_API)
        @ParameterizedTest(name = "{index} => wrongAppVersion=''{0}''")
        @CsvSource({
            "2.2.2",
            "2.2.1",
            "2.1.2",
            "2.1.1",
            "1.1.1",
            "1.20.30"
        })
        void shouldReturnSuccess(String wrongAppVersion) throws Exception {
            final MvcResult mvcResult = callApi(wrongAppVersion, status().isUpgradeRequired());

            itUtil.assertErrorResponse(
                mvcResult,
                426,
                4261,
                "최신앱으로 업데이트해야 서비스를 이용할 수 있습니다"
            );
        }
    }

    @Nested
    class WhenAllPermissionApiCalled {

        @DisplayName("Should return 200 response when call all permission apis")
        @ParameterizedTest(name = "{index} => wrongAppVersion=''{0}''")
        @CsvSource({
            "2.2.2",
            "2.2.1",
            "2.1.2",
            "2.1.1",
            "1.1.1",
            "1.20.30"
        })
        void shouldReturnSuccess(String wrongAppVersion) throws Exception {
            final MvcResult mvcResult = callApi(wrongAppVersion, status().isOk());
            final SimpleStringResponse result = itUtil.getResult(mvcResult, SimpleStringResponse.class);

            assertThat(result.getStatus(), is("ok"));
        }

        private MvcResult callApi(String appVersion, ResultMatcher resultMatcher) throws Exception {
            return mockMvc
                .perform(
                    get("/api/health")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .headers(headers(jwt(jwt)))
                        .header(X_APP_VERSION, appVersion)
                )
                .andExpect(resultMatcher)
                .andReturn();
        }
    }

    @DisplayName("Web 용 API 를 호출 했을때")
    @Nested
    class WhenWebApiCalled {
        private static final String TARGET_API = "/api/auth/web/generate-verification-code";
        private WebVerificationCodeGenerateRequest request;

        @BeforeEach
        void setUp() {
            request = new WebVerificationCodeGenerateRequest()
                .authenticationReference(UUID.randomUUID());

            minAppVersionIsTooHigh();
        }

        private void minAppVersionIsTooHigh() {
            given(appPreferenceCache.getValue(AppPreferenceType.MIN_APP_VERSION))
                .willReturn(AppVersion.of("100.100.100"));
        }

        @Test
        @DisplayName("Should return 200 response when call web apis")
        void shouldReturnSuccess() throws Exception {
            callApi();
        }

        private void callApi() throws Exception {
            mockMvc
                .perform(
                    post(TARGET_API)
                        .content(objectMapperUtil.toRequestBody(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_APP_VERSION, X_APP_VERSION_WEB)
                )
                .andExpect(status().isOk())
                .andReturn();
        }
    }
}
