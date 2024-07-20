package ag.act.api.admin.stockgroup;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Stock;
import ag.act.entity.StockGroup;
import ag.act.entity.User;
import ag.act.model.GetStockGroupDetailsDataResponse;
import ag.act.model.SimpleStockResponse;
import ag.act.model.StockGroupDetailsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.Comparator;
import java.util.List;

import static ag.act.TestUtil.assertTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomLongs.someLongBetween;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class GetStockGroupDetailsApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/stock-groups/{stockGroupId}";
    private String jwt;
    private StockGroup stockGroup;
    private Long stockGroupId;
    private List<Stock> stocks;

    @BeforeEach
    void setUp() {
        itUtil.init();
        final User adminUser = itUtil.createAdminUser();
        jwt = itUtil.createJwt(adminUser.getId());

        stockGroup = itUtil.createStockGroup(someString(10));
        stockGroupId = stockGroup.getId();
        stocks = List.of(
            itUtil.createStock(),
            itUtil.createStock(),
            itUtil.createStock()
        );
        stocks.forEach(stock -> itUtil.createStockGroupMapping(stock.getCode(), stockGroupId));

        mockStockGroupGroup(someIntegerBetween(1, 10));
        mockStockGroupGroup(someIntegerBetween(1, 10));
        mockStockGroupGroup(someIntegerBetween(1, 10));
    }

    private StockGroup mockStockGroupGroup(Integer stockCount) {
        StockGroup stockGroup = itUtil.createStockGroup(someString(10));

        for (int i = 0; i < stockCount; i++) {
            itUtil.createStockGroupMapping(itUtil.createStock().getCode(), stockGroup.getId());
        }

        return stockGroup;
    }

    @Nested
    class WhenGetStockGroupDetails {

        @Nested
        class AndFoundStockGroup {

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnPosts() throws Exception {
                final MvcResult response = callApiAndGetResult(status().isOk());

                final GetStockGroupDetailsDataResponse result = objectMapperUtil.toResponse(
                    response.getResponse().getContentAsString(),
                    GetStockGroupDetailsDataResponse.class
                );

                assertStockGroupResponse(stockGroup, stocks, result.getData());
            }
        }

        @Nested
        class AndNotFoundStockGroup {

            @BeforeEach
            void setUp() {
                stockGroupId = someLongBetween(1000L, 100000L);
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnPosts() throws Exception {
                final MvcResult response = callApiAndGetResult(status().isNotFound());

                itUtil.assertErrorResponse(response, 404, "종목그룹을 찾을 수 없습니다.");
            }
        }
    }

    private MvcResult callApiAndGetResult(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                get(TARGET_API, stockGroupId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    private void assertStockGroupResponse(
        StockGroup stockGroup,
        List<Stock> stocks,
        StockGroupDetailsResponse stockGroupResponse
    ) {
        assertThat(stockGroupResponse.getId(), is(stockGroup.getId()));
        assertThat(stockGroupResponse.getName(), is(stockGroup.getName()));
        assertThat(stockGroupResponse.getStatus(), is(stockGroup.getStatus()));
        assertThat(stockGroupResponse.getDescription(), is(stockGroup.getDescription()));
        assertTime(stockGroupResponse.getCreatedAt(), stockGroup.getCreatedAt());
        assertTime(stockGroupResponse.getUpdatedAt(), stockGroup.getUpdatedAt());
        assertTime(stockGroupResponse.getDeletedAt(), stockGroup.getDeletedAt());

        final List<Stock> expectedSortedStocks = stocks.stream().sorted(Comparator.comparing(Stock::getName)).toList();
        final List<SimpleStockResponse> actualStocks = stockGroupResponse.getStocks();

        for (int i = 0; i < actualStocks.size(); i++) {
            assertThat(actualStocks.get(i).getCode(), is(expectedSortedStocks.get(i).getCode()));
            assertThat(actualStocks.get(i).getName(), is(expectedSortedStocks.get(i).getName()));
            assertThat(actualStocks.get(i).getStandardCode(), is(expectedSortedStocks.get(i).getStandardCode()));
        }
    }
}
