package ag.act.api.app.commons;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Stock;
import ag.act.entity.TestStock;
import ag.act.entity.User;
import ag.act.model.GetSimpleStockDataResponse;
import ag.act.model.SimpleStockResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Comparator;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetCommonStocksApiIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/commons/stocks";
    private String jwt;

    @BeforeEach
    void setUp() {
        itUtil.init();
        final User user = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());
    }

    private GetSimpleStockDataResponse getResponse(MvcResult response) throws Exception {
        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            GetSimpleStockDataResponse.class
        );
    }

    private MvcResult callApi() throws Exception {
        return mockMvc
            .perform(
                get(TARGET_API)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(status().isOk())
            .andReturn();
    }

    @Nested
    @DisplayName("앱에서 공통 주식 목록을 조회한다.")
    class GetCommonStocks {

        private List<SimpleStockResponse> expectedStocks;

        @BeforeEach
        void setUp() {

            TestStock testStock1 = itUtil.createTestStock();
            TestStock testStock2 = itUtil.createTestStock();
            itUtil.createStock(testStock1.getCode());
            itUtil.createStock(testStock2.getCode());
            itUtil.createStock();
            itUtil.createStock();

            expectedStocks = itUtil.findAllStocksWithoutTestStocks()
                .stream()
                .map(this::toSimpleStockResponse)
                .sorted(Comparator.comparing(SimpleStockResponse::getName))
                .toList();
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            assertResponse(getResponse(callApi()));
        }

        private void assertResponse(GetSimpleStockDataResponse result) {
            final List<SimpleStockResponse> data = result.getData();

            assertThat(data, is(expectedStocks));
        }

        private SimpleStockResponse toSimpleStockResponse(Stock stock) {
            return new SimpleStockResponse()
                .code(stock.getCode())
                .name(stock.getName())
                .standardCode(stock.getStandardCode());
        }
    }
}
