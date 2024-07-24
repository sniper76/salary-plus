package ag.act.api.stocksearch;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Stock;
import ag.act.entity.StockSearchTrend;
import ag.act.entity.User;
import ag.act.model.CreateStockSearchTrendRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CreateStockSearchTrendIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/stock-search-trends";

    private CreateStockSearchTrendRequest request;
    private String jwt;
    private User currentUser;

    @BeforeEach
    void setUp() {
        itUtil.init();
        currentUser = itUtil.createUser();
        jwt = itUtil.createJwt(currentUser.getId());

        final Stock stock = itUtil.createStock();
        request = genRequest(stock);
    }

    @DisplayName("Should return 200 response code when call " + TARGET_API)
    @Test
    void shouldReturnSuccess() throws Exception {
        final MvcResult response = callApi();

        itUtil.assertSimpleOkay(response);
        assertDatabase();
    }

    private void assertDatabase() {
        final List<StockSearchTrend> stockSearchTrends = itUtil.findAllStockSearchTrends(request.getStockCode(), currentUser.getId());
        assertThat(stockSearchTrends.size(), is(1));
        
        final StockSearchTrend stockSearchTrend = stockSearchTrends.get(0);
        assertThat(stockSearchTrend.getStockCode(), is(request.getStockCode()));
        assertThat(stockSearchTrend.getUserId(), is(currentUser.getId()));
    }

    @NotNull
    private MvcResult callApi() throws Exception {
        return mockMvc
            .perform(
                post(TARGET_API)
                    .content(objectMapperUtil.toJson(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(status().isOk())
            .andReturn();
    }

    private CreateStockSearchTrendRequest genRequest(Stock stock) {
        return new CreateStockSearchTrendRequest()
            .stockCode(stock.getCode());
    }
}
