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

import java.net.URI;
import java.net.http.HttpResponse;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomThings.someThing;

class AdminUserMyDataWithdrawApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/users/mydata/withdraw/{finpongAccessToken}";

    @Mock
    private HttpResponse<String> deleteResponse;

    private String jwt;
    private String finpongAccessToken;

    @BeforeEach
    void setUp() throws Exception {
        itUtil.init();
        final User user = itUtil.createAdminUser();
        jwt = itUtil.createJwt(user.getId());
        finpongAccessToken = someAlphanumericString(20);

        // 외부서비스는 Mocking 한다.
        mockWithdrawMyDataService();
    }

    @Nested
    class WhenFinpongAccessTokenIsValid {

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    delete(TARGET_API, finpongAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isOk())
                .andReturn();

            final ag.act.model.SimpleStringResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                ag.act.model.SimpleStringResponse.class
            );

            assertThat(result.getStatus(), is("ok"));
            then(defaultHttpClientUtil).should().delete(any(URI.class), anyMap());
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
            MvcResult response = mockMvc
                .perform(
                    delete(TARGET_API, finpongAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isBadRequest())
                .andReturn();

            itUtil.assertErrorResponse(response, 400, "마이데이터 토큰을 확인해주세요.");
        }
    }

    private void mockWithdrawMyDataService() throws Exception {
        final String responseBody = genResponse();
        given(defaultHttpClientUtil.delete(any(URI.class), anyMap())).willReturn(deleteResponse);
        given(deleteResponse.body()).willReturn(responseBody);
    }

    private String genResponse() {
        Map<String, String> result = Map.of("code", "FP-00000");
        Map<String, Map<String, String>> response = Map.of("result", result);
        return objectMapperUtil.toJson(response);
    }
}
