package ag.act.api.auth;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.User;
import ag.act.model.MyDataTokenResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.net.http.HttpResponse;
import java.util.Map;

import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class MyDataTokenRequestIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/auth/mydata-token-request";

    @Mock
    private HttpResponse<String> response;
    private String jwt;
    private String finpongAccessToken;

    @BeforeEach
    void setUp() throws Exception {
        finpongAccessToken = someString(50);

        itUtil.init();
        final User user = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());

        setUpMyDataHttpClientUtilMocks();
    }

    private void setUpMyDataHttpClientUtilMocks() throws Exception {
        given(defaultHttpClientUtil.post(any(), anyLong(), anyString())).willReturn(response);
    }

    @Nested
    class WhenGetTokenSuccessfully {

        @BeforeEach
        void setUp() {
            setUpMyDataHectorResponseMocks();
        }

        private void setUpMyDataHectorResponseMocks() {
            given(response.body())
                .willReturn("Error to test MyData's finpongAccessToken with spring-retry")
                .willReturn("Error to test MyData's finpongAccessToken with spring-retry")
                .willReturn(genSuccessResponse());
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult response = callApi(status().isOk());

            assertResponse(itUtil.getResult(response, MyDataTokenResponse.class));
            then(defaultHttpClientUtil).should(times(3)).post(any(), anyLong(), anyString());
        }
    }

    @Nested
    class WhenFailToGetToken {

        @BeforeEach
        void setUp() {
            setUpMyDataHectorResponseMocks();
        }

        private void setUpMyDataHectorResponseMocks() {
            given(response.body())
                .willReturn("Error to test MyData's finpongAccessToken with spring-retry")
                .willReturn("Error to test MyData's finpongAccessToken with spring-retry")
                .willReturn("Error to test MyData's finpongAccessToken with spring-retry");
        }

        @DisplayName("Should return 500 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult response = callApi(status().isInternalServerError());

            itUtil.assertErrorResponse(response, 500, "마이데이터 엑세스토큰 요청 중에 알 수 없는 오류가 발생하였습니다.");
        }
    }

    private void assertResponse(MyDataTokenResponse result) {
        assertThat(result.getToken(), is(finpongAccessToken));
    }

    private String genSuccessResponse() {
        Map<String, Map<String, String>> response = Map.of(
            "result", Map.of("code", "FP-00000"),
            "data", Map.of("accessToken", finpongAccessToken)
        );
        return objectMapperUtil.toJson(response);
    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                post(TARGET_API)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(resultMatcher)
            .andReturn();
    }
}
