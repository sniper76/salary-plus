package ag.act.api.admin.user;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.net.URI;
import java.net.http.HttpResponse;
import java.util.Map;

import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomThings.someThing;

class AdminUserMyDataWithdrawApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/users/mydata/withdraw/{finpongAccessToken}";
    private static final int RETRY_TIMES = 3;

    @Mock
    private HttpResponse<String> deleteResponse;

    private String jwt;
    private String finpongAccessToken;

    @BeforeEach
    void setUp() throws Exception {
        itUtil.init();
        final User user = itUtil.createAdminUser();
        jwt = itUtil.createJwt(user.getId());

        setUpMyDataHttpClientUtilMocks();
    }

    private void setUpMyDataHttpClientUtilMocks() throws Exception {
        given(defaultHttpClientUtil.delete(any(URI.class), anyMap())).willReturn(deleteResponse);
    }

    @Nested
    class WhenFinpongAccessTokenIsValid {
        @BeforeEach
        void setUp() {
            finpongAccessToken = someAlphanumericString(20);
            setUpMyDataHectorResponseMocks();
        }

        private void setUpMyDataHectorResponseMocks() {
            given(deleteResponse.body())
                .willReturn("Error to test MyData's withdrawal with spring-retry")
                .willReturn("Error to test MyData's withdrawal with spring-retry")
                .willReturn(genSuccessResponse());
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult response = callApi(status().isOk());

            itUtil.assertSimpleOkay(response);
            then(defaultHttpClientUtil).should(times(RETRY_TIMES)).delete(any(URI.class), anyMap());
        }
    }

    @Nested
    class WhenFinpongAccessTokenIsInvalid {
        @BeforeEach
        void setUp() {
            finpongAccessToken = someThing(" ", "  ", "        ");
        }

        @DisplayName("Should return 400 response code when call " + TARGET_API)
        @Test
        void shouldReturnBadRequest() throws Exception {
            MvcResult response = callApi(status().isBadRequest());

            itUtil.assertErrorResponse(response, 400, "마이데이터 토큰을 확인해주세요.");
            then(defaultHttpClientUtil).should(never()).delete(any(URI.class), anyMap());
        }
    }

    private String genSuccessResponse() {
        Map<String, String> result = Map.of("code", "FP-00000");
        Map<String, Map<String, String>> response = Map.of("result", result);
        return objectMapperUtil.toJson(response);
    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                delete(TARGET_API, finpongAccessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(resultMatcher)
            .andReturn();
    }
}
