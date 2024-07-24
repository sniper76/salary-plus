package ag.act.api.admin.stockgroup;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.User;
import ag.act.model.StockDataArrayResponse;
import ag.act.model.StockResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomThings.someThing;

public class GetStockGroupsAutoCompleteApiIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/admin/stock-groups/auto-complete";
    private static final String OTHER_STOCK_GROUP_NAME_PREFIX = "ZZZ_";

    private String jwt;
    private String searchKeyword;
    private List<String> stockGroupNames;

    @BeforeEach
    void setUp() {
        itUtil.init();
        final User user = itUtil.createAdminUser();
        jwt = itUtil.createJwt(user.getId());

        stockGroupNames = new ArrayList<>();
        cleanAllExistingStockGroups();

        createTop10StockGroups();
    }

    @SuppressWarnings("SimplifyStreamApiCallChains")
    private void cleanAllExistingStockGroups() {
        itUtil.findAllActiveStockGroups()
            .stream()
            .map(stockGroup -> {
                if (!stockGroup.getName().startsWith(OTHER_STOCK_GROUP_NAME_PREFIX)) {
                    stockGroup.setName(OTHER_STOCK_GROUP_NAME_PREFIX + stockGroup.getName());
                }
                return stockGroup;
            })
            .forEach(itUtil::updateStockGroup);
    }

    private void createTop10StockGroups() {
        IntStream.range(0, 10)
            .forEach(index -> {
                final String stockGroupName = "!!!" + "_" + someAlphanumericString(20);
                stockGroupNames.add(stockGroupName);
                itUtil.createStockGroup(stockGroupName);
            });
    }

    private StockDataArrayResponse getResponse(MvcResult response) throws Exception {
        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            StockDataArrayResponse.class
        );
    }

    private MvcResult callApi() throws Exception {
        return mockMvc
            .perform(
                get(TARGET_API)
                    .param("searchKeyword", searchKeyword)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(status().isOk())
            .andReturn();
    }

    @Nested
    class WhenSearchKeywordProvided {
        @Nested
        class SomeStockGroupsMatched {
            private String stockGroupName;

            @BeforeEach
            void setUp() {
                searchKeyword = someAlphanumericString(10);
                stockGroupName = someAlphanumericString(10) + searchKeyword + someAlphanumericString(10);
                itUtil.createStockGroup(stockGroupName);
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
                assertThat(stockResponses.get(0).getName(), is(stockGroupName));
            }
        }

        @Nested
        class NotMatched {
            @BeforeEach
            void setUp() {
                searchKeyword = someAlphanumericString(10);
            }

            @DisplayName("Should return 200 response code with 10 matched stock groups when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                StockDataArrayResponse result = getResponse(callApi());
                assertResponse(result);
            }

            private void assertResponse(StockDataArrayResponse result) {
                final List<StockResponse> stockResponses = result.getData();

                assertThat(stockResponses.size(), is(0));
            }
        }
    }

    @Nested
    class WhenSearchKeywordIsNullOrEmpty {
        @BeforeEach
        void setUp() {
            searchKeyword = someThing(null, "", " ", "    ");
        }

        @DisplayName("Should return 200 response code with top ten stock groups when call " + TARGET_API)
        @Test
        void shouldReturnTopTenStockGroups() throws Exception {
            StockDataArrayResponse result = getResponse(callApi());
            assertThat(result.getData().size(), is(10));

            assertThat(
                result.getData().stream().map(StockResponse::getName).toList(),
                containsInAnyOrder(stockGroupNames.stream().sorted().toArray())
            );
        }
    }
}
