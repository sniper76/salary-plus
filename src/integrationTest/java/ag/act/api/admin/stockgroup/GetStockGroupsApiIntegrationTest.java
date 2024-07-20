package ag.act.api.admin.stockgroup;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.StockGroup;
import ag.act.entity.User;
import ag.act.itutil.holder.ActEntityTestHolder;
import ag.act.model.Paging;
import ag.act.model.Status;
import ag.act.model.StockGroupResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Map;

import static ag.act.TestUtil.assertTime;
import static ag.act.TestUtil.toMultiValueMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomStrings.someNumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class GetStockGroupsApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/stock-groups";

    private String jwt;
    private Map<String, Object> params;
    private Integer pageNumber;
    private static final ActEntityTestHolder<StockGroup, Long> stockGroupsTestHolder = new ActEntityTestHolder<>();
    private StockGroup stockGroup1;
    private StockGroup stockGroup2;
    private StockGroup stockGroup3;
    private StockGroup stockGroup4;
    private StockGroup deletedStockGroup1;
    private StockGroup deletedStockGroup2;
    private Integer stockCount1;
    private Integer stockCount2;
    private Integer stockCount3;
    private Integer stockCount4;

    @BeforeEach
    void setUp() {
        itUtil.init();
        stockGroupsTestHolder.initialize(itUtil.findAllActiveStockGroups());
        final User adminUser = itUtil.createAdminUser();
        jwt = itUtil.createJwt(adminUser.getId());

        stockCount1 = someIntegerBetween(1, 10);
        stockGroup1 = mockStockGroupGroup(stockCount1);
        stockCount2 = someIntegerBetween(1, 10);
        stockGroup2 = mockStockGroupGroup(stockCount2);
        stockCount3 = someIntegerBetween(1, 10);
        stockGroup3 = mockStockGroupGroup(stockCount3);
        stockCount4 = someIntegerBetween(1, 10);
        stockGroup4 = mockStockGroupGroup(stockCount4);
        deletedStockGroup1 = mockDeleteStockGroupGroup();
        deletedStockGroup2 = mockDeleteStockGroupGroup();
    }

    private StockGroup mockStockGroupGroup(Integer stockCount) {
        StockGroup stockGroup = itUtil.createStockGroup(someString(10));
        stockGroupsTestHolder.addOrSet(stockGroup);

        for (int i = 0; i < stockCount; i++) {
            itUtil.createStockGroupMapping(itUtil.createStock().getCode(), stockGroup.getId());
        }

        return stockGroup;
    }

    private StockGroup mockDeleteStockGroupGroup() {
        return itUtil.createDeletedStockGroup(someString(10));
    }

    @Nested
    class WhenSearchAllStockGroups {

        @Nested
        class AndFirstPage {

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_1;
                params = Map.of(
                    "page", pageNumber.toString(),
                    "size", SIZE.toString()
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnPosts() throws Exception {
                final long totalElements = stockGroupsTestHolder.getItems().size();
                assertResponse(callApiAndGetResult(), totalElements);
            }

            private void assertResponse(ag.act.model.GetStockGroupsResponse result, long totalElements) {
                final Paging paging = result.getPaging();
                final List<StockGroupResponse> stockGroupResponses = result.getData();

                assertPaging(paging, totalElements);
                assertThat(stockGroupResponses.size(), is(SIZE));
                assertNotContainDeletedStockGroup(stockGroupResponses);
                assertPostResponse(stockGroup4, stockCount4, stockGroupResponses.get(0));
                assertPostResponse(stockGroup3, stockCount3, stockGroupResponses.get(1));
            }
        }

        @Nested
        class AndSecondPage {

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_2;
                params = Map.of(
                    "page", pageNumber.toString(),
                    "size", SIZE.toString()
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnPosts() throws Exception {
                final long totalElements = stockGroupsTestHolder.getItems().size();
                assertResponse(callApiAndGetResult(), totalElements);
            }

            private void assertResponse(ag.act.model.GetStockGroupsResponse result, long totalElements) {
                final Paging paging = result.getPaging();
                final List<StockGroupResponse> stockGroupResponses = result.getData();

                assertPaging(paging, totalElements);
                assertThat(stockGroupResponses.size(), is(SIZE));
                assertNotContainDeletedStockGroup(stockGroupResponses);
                assertPostResponse(stockGroup2, stockCount2, stockGroupResponses.get(0));
                assertPostResponse(stockGroup1, stockCount1, stockGroupResponses.get(1));
            }
        }
    }

    private void assertNotContainDeletedStockGroup(List<StockGroupResponse> stockGroupResponses) {
        assertThat(stockGroupResponses.contains(deletedStockGroup1), is(false));
        assertThat(stockGroupResponses.contains(deletedStockGroup2), is(false));
    }

    @Nested
    class WhenSearchSpecificStockGroupCode {

        private StockGroup stockGroup;
        private Integer stockCount;

        @Nested
        class AndFirstStockGroupCode {

            @BeforeEach
            void setUp() {
                stockGroup = stockGroup4;
                stockCount = stockCount4;

                pageNumber = PAGE_1;
                params = Map.of(
                    "stockGroupId", stockGroup.getId(),
                    "page", pageNumber.toString(),
                    "size", SIZE.toString()
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnPosts() throws Exception {
                assertResponse(callApiAndGetResult());
            }

            private void assertResponse(ag.act.model.GetStockGroupsResponse result) {
                final Paging paging = result.getPaging();
                final List<StockGroupResponse> stockGroupResponses = result.getData();

                assertPaging(paging, 1L);
                assertThat(stockGroupResponses.size(), is(1));
                assertPostResponse(stockGroup, stockCount, stockGroupResponses.get(0));
            }
        }

        @Nested
        class AndSecondStockGroupCode {

            @BeforeEach
            void setUp() {
                stockGroup = stockGroup2;
                stockCount = stockCount2;

                pageNumber = PAGE_1;
                params = Map.of(
                    "stockGroupId", stockGroup.getId(),
                    "page", pageNumber.toString(),
                    "size", SIZE.toString()
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnPosts() throws Exception {
                assertResponse(callApiAndGetResult());
            }

            private void assertResponse(ag.act.model.GetStockGroupsResponse result) {
                final Paging paging = result.getPaging();
                final List<StockGroupResponse> stockGroupResponses = result.getData();

                assertPaging(paging, 1L);
                assertThat(stockGroupResponses.size(), is(1));
                assertPostResponse(stockGroup, stockCount, stockGroupResponses.get(0));
            }
        }

        @Nested
        class AndNotFoundStockGroup {

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_1;
                params = Map.of(
                    "stockGroupId", someNumericString(10),
                    "page", pageNumber.toString(),
                    "size", SIZE.toString()
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnPosts() throws Exception {
                assertResponse(callApiAndGetResult());
            }

            private void assertResponse(ag.act.model.GetStockGroupsResponse result) {
                final Paging paging = result.getPaging();
                final List<StockGroupResponse> stockGroupResponses = result.getData();

                assertPaging(paging, 0L);
                assertThat(stockGroupResponses.size(), is(0));
            }
        }
    }

    private ag.act.model.GetStockGroupsResponse callApiAndGetResult() throws Exception {
        MvcResult response = mockMvc
            .perform(
                get(TARGET_API)
                    .params(toMultiValueMap(params))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(status().isOk())
            .andReturn();

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            ag.act.model.GetStockGroupsResponse.class
        );
    }

    private void assertPostResponse(StockGroup stockGroup, Integer stockCount, StockGroupResponse stockGroupResponse) {
        assertThat(stockGroupResponse.getId(), is(stockGroup.getId()));
        assertThat(stockGroupResponse.getName(), is(stockGroup.getName()));
        assertThat(stockGroupResponse.getStatus(), is(Status.ACTIVE));
        assertThat(stockGroupResponse.getDescription(), is(stockGroup.getDescription()));
        assertTime(stockGroupResponse.getCreatedAt(), stockGroup.getCreatedAt());
        assertTime(stockGroupResponse.getUpdatedAt(), stockGroup.getUpdatedAt());
        assertTime(stockGroupResponse.getDeletedAt(), stockGroup.getDeletedAt());
        assertThat(stockGroupResponse.getStockCount(), is(stockCount));
    }

    private void assertPaging(Paging paging, long totalElements) {
        assertThat(paging.getPage(), is(pageNumber));
        assertThat(paging.getTotalElements(), is(totalElements));
        assertThat(paging.getTotalPages(), is((int) Math.ceil(totalElements / (SIZE * 1.0))));
        assertThat(paging.getSize(), is(SIZE));
        assertThat(paging.getSorts().size(), is(1));
        assertThat(paging.getSorts().get(0), is(CREATED_AT_DESC));
    }
}
