package ag.act.api.admin.solidarity;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.model.StockDataArrayResponse;
import ag.act.model.StockResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;
import static shiver.me.timbers.data.random.RandomThings.someThing;

public class GetStocksAutoCompleteApiIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/admin/stocks/auto-complete";

    private String jwt;
    private String searchKeyword;

    @BeforeEach
    void setUp() {
        itUtil.init();
        dbCleaner.clean();

        final User user = itUtil.createAdminUser();
        jwt = itUtil.createJwt(user.getId());
    }

    private ag.act.model.StockDataArrayResponse getResponse(MvcResult response) throws Exception {
        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            ag.act.model.StockDataArrayResponse.class
        );
    }

    private MvcResult callApi() throws Exception {
        return mockMvc
            .perform(
                get(TARGET_API)
                    .param("searchKeyword", searchKeyword)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(status().isOk())
            .andReturn();
    }

    @Nested
    class WhenSearchKeywordProvided {
        @Nested
        class AndOnlyOneStockMatched {
            private Stock expectedStock;

            @BeforeEach
            void setUp() {
                searchKeyword = "TEST_STOCK_NAME";

                expectedStock = itUtil.createStock();
                expectedStock.setName(someString(5) + searchKeyword + someString(5));
                itUtil.updateStock(expectedStock);

                mockRandomNamedStock();
                mockRandomNamedStock();
            }

            private void mockRandomNamedStock() {
                final Stock stock = itUtil.createStock();
                stock.setName(someAlphanumericString(15));
                itUtil.updateStock(stock);
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                StockDataArrayResponse result = getResponse(callApi());
                assertResponse(result);
            }

            private void assertResponse(StockDataArrayResponse result) {
                final List<StockResponse> stockResponses = result.getData();

                assertThat(stockResponses.size(), is(1));
                assertThat(stockResponses.get(0).getCode(), is(expectedStock.getCode()));
                assertThat(stockResponses.get(0).getName(), is(expectedStock.getName()));
            }
        }

        @Nested
        class AndMoreThanTenStocksMatched {
            @BeforeEach
            void setUp() {
                searchKeyword = "TEST_STOCK_NAME";

                for (int i = 0; i < 11; ++i) {
                    mockKeywordMatchedStock();
                }
            }

            private void mockKeywordMatchedStock() {
                final Stock stock = itUtil.createStock();
                stock.setName(someString(5) + searchKeyword + someString(5));
                itUtil.updateStock(stock);
            }

            @DisplayName("Should return 200 response code with 10 matched stocks when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                StockDataArrayResponse result = getResponse(callApi());
                assertResponse(result);
            }

            private void assertResponse(StockDataArrayResponse result) {
                final List<StockResponse> stockResponses = result.getData();

                assertThat(stockResponses.size(), is(10));
            }
        }
    }

    @Nested
    class WhenSearchKeywordIsNullOrEmpty {
        @BeforeEach
        void setUp() {
            searchKeyword = someThing(null, "");

            for (int i = 0; i < 11; i++) {
                itUtil.createStock(someStockCode());
            }
        }

        @DisplayName("Should return 200 response code with top ten stocks when call " + TARGET_API)
        @Test
        void shouldReturnTopTenStocks() throws Exception {
            StockDataArrayResponse result = getResponse(callApi());
            assertThat(result.getData().size(), is(10));
        }
    }
}
