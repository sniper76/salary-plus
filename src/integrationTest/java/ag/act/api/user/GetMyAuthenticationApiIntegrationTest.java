package ag.act.api.user;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.model.MyStockAuthenticationResponse;
import ag.act.model.SimpleStockResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetMyAuthenticationApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/users/me/stocks/{stockCode}/authentication";

    private String jwt;
    private User user;
    private String stockCode;

    @BeforeEach
    void setUp() {
        itUtil.init();
        user = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());

        final Stock stock = itUtil.createStock();
        stockCode = stock.getCode();
    }

    @Nested
    class WhenSuccess {
        final long quantity = 5000;

        @BeforeEach
        void setUp() {
            itUtil.createUserHoldingStock(stockCode, user, quantity);
        }

        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult response = callApi(status().isOk());

            final MyStockAuthenticationResponse result = itUtil.getResult(response, MyStockAuthenticationResponse.class);

            assertResponse(result);
        }

        private void assertResponse(MyStockAuthenticationResponse response) {
            final SimpleStockResponse holdingStock = response.getStock();
            assertThat(holdingStock.getCode(), is(stockCode));
            assertThat(response.getIndividualStockCountLabel(), is("%s주+".formatted(quantity)));
        }
    }

    @Nested
    class WhenError {

        @Nested
        class WhenHaveNotHoldingStock {

            @Test
            void shouldReturnError() throws Exception {
                MvcResult response = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(response, 400, "보유 주식 정보가 없습니다.");
            }
        }
    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                get(TARGET_API, stockCode)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(resultMatcher)
            .andReturn();
    }
}
