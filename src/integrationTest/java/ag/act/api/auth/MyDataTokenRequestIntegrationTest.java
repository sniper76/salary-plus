package ag.act.api.auth;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.net.http.HttpResponse;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someNumericString;
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
        final User user = itUtil.createUser(someNumericString(6));
        jwt = itUtil.createJwt(user.getId());

        // 외부서비스는 Mocking 한다.
        final String responseBody = genResponse();
        given(defaultHttpClientUtil.post(any(), anyLong(), anyString())).willReturn(response);
        given(response.body())
            .willReturn("Error to test MyData's fnpongAccessToken with spring-retry")
            .willReturn("Error to test MyData's fnpongAccessToken with spring-retry")
            .willReturn(responseBody);
    }

    @Nested
    class WhenGetTokenSuccessfully {

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    post(TARGET_API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isOk())
                .andReturn();

            assertResponse(itUtil.getResult(response, ag.act.model.MyDataTokenResponse.class));
        }
    }


    @Nested
    class WhenFailToGetToken {

        @BeforeEach
        void setUp() {
            given(response.body())
                .willReturn("Error to test MyData's fnpongAccessToken with spring-retry")
                .willReturn("Error to test MyData's fnpongAccessToken with spring-retry")
                .willReturn("Error to test MyData's fnpongAccessToken with spring-retry");
        }

        @DisplayName("Should return 500 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    post(TARGET_API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isInternalServerError())
                .andReturn();

            itUtil.assertErrorResponse(response, 500, "마이데이터 엑세스토큰 요청 중에 알 수 없는 오류가 발생하였습니다.");
        }
    }

    private void assertResponse(ag.act.model.MyDataTokenResponse result) {
        assertThat(result.getToken(), is(finpongAccessToken));
    }

    private String genResponse() {
        Map<String, String> result = Map.of("code", "FP-00000");
        Map<String, String> data = Map.of("accessToken", finpongAccessToken);
        Map<String, Map<String, String>> response = Map.of("result", result, "data", data);
        return objectMapperUtil.toJson(response);
    }
}
