package ag.act.api.admin.stock;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Solidarity;
import ag.act.entity.SolidarityDailySummary;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.itutil.holder.StocksTestHolder;
import ag.act.model.GetStocksResponse;
import ag.act.model.Paging;
import ag.act.model.StockResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Map;

import static ag.act.TestUtil.someStockCode;
import static ag.act.TestUtil.toMultiValueMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomDoubles.someDoubleBetween;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;

class GetStocksApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/stocks";
    private static final StocksTestHolder stocksTestHolder = new StocksTestHolder();
    private String jwt;
    private Map<String, Object> params;
    private Integer pageNumber;
    private Stock stock1;
    private Stock stock2;
    private Stock stock3;
    private Stock stock4;
    private SolidarityDailySummary solidarityDailySummary1;
    private SolidarityDailySummary solidarityDailySummary2;
    private SolidarityDailySummary solidarityDailySummary3;
    private SolidarityDailySummary solidarityDailySummary4;

    @BeforeEach
    void setUp() {
        itUtil.init();
        stocksTestHolder.initialize(itUtil.findAllStocksThatHaveSolidarity());

        final User adminUser = itUtil.createAdminUser();
        jwt = itUtil.createJwt(adminUser.getId());

        cleanUpExistingSolidarityDailySummaries();
        cleanUpExistingStock();

        stock1 = mockStock(1000L);
        solidarityDailySummary1 = mockSolidarityDailySummary(stock1, 91.0, 400);
        stock2 = mockStock(2000L);
        solidarityDailySummary2 = mockSolidarityDailySummary(stock2, 95.0, 300);
        stock3 = mockStock(3000L);
        solidarityDailySummary3 = mockSolidarityDailySummary(stock3, 98.0, 200);
        stock4 = mockStock(4000L);
        solidarityDailySummary4 = mockSolidarityDailySummary(stock4, 99.0, 100);
    }

    private long getMarketValue(Stock stock) {
        return stock.getTotalIssuedQuantity() * stock.getClosingPrice();
    }

    private void cleanUpExistingSolidarityDailySummaries() {
        itUtil.findAllSolidarityDailySummary().stream()
            .peek(solidarityDailySummary -> {
                solidarityDailySummary.setStake(someDoubleBetween(0.0, 80.0));
                solidarityDailySummary.setMemberCount(someIntegerBetween(0, 99));
                solidarityDailySummary.setMarketValue(0L);
            })
            .forEach(itUtil::updateSolidarityDailySummary);
    }

    private void cleanUpExistingStock() {
        itUtil.findAllStocks().stream()
            .peek(stock -> stock.setTotalIssuedQuantity(0L))
            .forEach(itUtil::updateStock);
    }

    private Stock mockStock(Long totalIssuedQuantity) {
        Stock stock = itUtil.createStock();
        stock.setTotalIssuedQuantity(totalIssuedQuantity);
        stock.setClosingPrice(100);
        stocksTestHolder.addOrSet(stock);
        return itUtil.updateStock(stock);
    }

    private SolidarityDailySummary mockSolidarityDailySummary(Stock stock, Double stake, Integer memberCount) {
        final Solidarity solidarity = itUtil.createSolidarity(stock.getCode());
        SolidarityDailySummary summary = itUtil.createSolidarityDailySummary();
        summary.setStake(stake);
        summary.setMemberCount(memberCount);
        summary.setMarketValue(getMarketValue(stock));
        solidarity.setMostRecentDailySummary(summary);
        stock.setSolidarity(solidarity);
        stock.setSolidarityId(solidarity.getId());
        itUtil.updateSolidarity(solidarity);

        return summary;
    }

    private GetStocksResponse callApiAndGetResult() throws Exception {
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
            ag.act.model.GetStocksResponse.class
        );
    }

    @SuppressWarnings({"unchecked"})
    private List<String> getSortsOrDefault(Map<String, Object> params) {
        return (List<String>) params.getOrDefault("sorts", List.of("stake:DESC"));
    }

    private void assertPostResponse(Stock stock, SolidarityDailySummary solidarityDailySummary, StockResponse stockResponse) {
        assertThat(stockResponse.getCode(), is(stock.getCode()));
        assertThat(stockResponse.getName(), is(stock.getName()));
        assertThat(stockResponse.getStatus(), is(stock.getStatus()));
        assertThat(stockResponse.getTotalIssuedQuantity(), is(stock.getTotalIssuedQuantity()));
        assertThat(stockResponse.getRepresentativePhoneNumber(), is(stock.getRepresentativePhoneNumber()));
        assertThat(stockResponse.getStake(), is(solidarityDailySummary.getStake().floatValue()));
        assertThat(stockResponse.getMemberCount(), is(solidarityDailySummary.getMemberCount()));
    }

    private void assertPaging(Paging paging, long totalElements, List<String> sorts) {
        assertThat(paging.getPage(), is(pageNumber));
        assertThat(paging.getTotalElements(), is(totalElements));
        assertThat(paging.getTotalPages(), is((int) Math.ceil(totalElements / (SIZE * 1.0))));
        assertThat(paging.getSize(), is(SIZE));
        assertThat(paging.getSorts().size(), is(1));
        assertThat(paging.getSorts().get(0), is(sorts.get(0)));
    }

    @Nested
    class WhenSearchAllStocks {

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
                final long totalElements = stocksTestHolder.getItems().size();
                assertResponse(callApiAndGetResult(), totalElements);
            }

            private void assertResponse(GetStocksResponse result, long totalElements) {
                final Paging paging = result.getPaging();
                final List<StockResponse> stockResponses = result.getData();

                assertPaging(paging, totalElements, getSortsOrDefault(params));
                assertThat(stockResponses.size(), is(SIZE));
                assertPostResponse(stock4, solidarityDailySummary4, stockResponses.get(0));
                assertPostResponse(stock3, solidarityDailySummary3, stockResponses.get(1));
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
                final long totalElements = stocksTestHolder.getItems().size();
                assertResponse(callApiAndGetResult(), totalElements);
            }

            private void assertResponse(GetStocksResponse result, long totalElements) {
                final Paging paging = result.getPaging();
                final List<StockResponse> stockResponses = result.getData();

                assertPaging(paging, totalElements, getSortsOrDefault(params));
                assertThat(stockResponses.size(), is(SIZE));
                assertPostResponse(stock2, solidarityDailySummary2, stockResponses.get(0));
                assertPostResponse(stock1, solidarityDailySummary1, stockResponses.get(1));
            }
        }
    }

    @Nested
    class WhenSearchAllStocksOrderByStake {

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
                final long totalElements = stocksTestHolder.getItems().size();
                assertResponse(callApiAndGetResult(), totalElements);
            }

            private void assertResponse(GetStocksResponse result, long totalElements) {
                final Paging paging = result.getPaging();
                final List<StockResponse> stockResponses = result.getData();

                assertPaging(paging, totalElements, getSortsOrDefault(params));
                assertThat(stockResponses.size(), is(SIZE));
                assertPostResponse(stock4, solidarityDailySummary4, stockResponses.get(0));
                assertPostResponse(stock3, solidarityDailySummary3, stockResponses.get(1));
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
                final long totalElements = stocksTestHolder.getItems().size();
                assertResponse(callApiAndGetResult(), totalElements);
            }

            private void assertResponse(GetStocksResponse result, long totalElements) {
                final Paging paging = result.getPaging();
                final List<StockResponse> stockResponses = result.getData();

                assertPaging(paging, totalElements, getSortsOrDefault(params));
                assertThat(stockResponses.size(), is(SIZE));
                assertPostResponse(stock2, solidarityDailySummary2, stockResponses.get(0));
                assertPostResponse(stock1, solidarityDailySummary1, stockResponses.get(1));
            }
        }
    }

    @Nested
    class WhenSearchAllStocksOrderByMemberCount {

        @Nested
        class AndFirstPage {

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_1;
                params = Map.of(
                    "page", pageNumber.toString(),
                    "size", SIZE.toString(),
                    "sorts", List.of("memberCount:DESC")
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnPosts() throws Exception {
                final long totalElements = stocksTestHolder.getItems().size();
                assertResponse(callApiAndGetResult(), totalElements);
            }

            private void assertResponse(GetStocksResponse result, long totalElements) {
                final Paging paging = result.getPaging();
                final List<StockResponse> stockResponses = result.getData();

                assertPaging(paging, totalElements, getSortsOrDefault(params));
                assertThat(stockResponses.size(), is(SIZE));
                assertPostResponse(stock1, solidarityDailySummary1, stockResponses.get(0));
                assertPostResponse(stock2, solidarityDailySummary2, stockResponses.get(1));
            }
        }

        @Nested
        class AndSecondPage {

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_2;
                params = Map.of(
                    "page", pageNumber.toString(),
                    "size", SIZE.toString(),
                    "sorts", List.of("memberCount:DESC")
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnPosts() throws Exception {
                final long totalElements = stocksTestHolder.getItems().size();
                assertResponse(callApiAndGetResult(), totalElements);
            }

            private void assertResponse(GetStocksResponse result, long totalElements) {
                final Paging paging = result.getPaging();
                final List<StockResponse> stockResponses = result.getData();

                assertPaging(paging, totalElements, getSortsOrDefault(params));
                assertThat(stockResponses.size(), is(SIZE));
                assertPostResponse(stock3, solidarityDailySummary3, stockResponses.get(0));
                assertPostResponse(stock4, solidarityDailySummary4, stockResponses.get(1));
            }
        }

    }

    @Nested
    class WhenSearchAllStocksOrderByMarketValue {

        @Nested
        class AndFirstPage {

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_1;
                params = Map.of(
                    "page", pageNumber.toString(),
                    "size", SIZE.toString(),
                    "sorts", List.of("marketValue:DESC")
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnPosts() throws Exception {
                final long totalElements = stocksTestHolder.getItems().size();
                assertResponse(callApiAndGetResult(), totalElements);
            }

            private void assertResponse(GetStocksResponse result, long totalElements) {
                final Paging paging = result.getPaging();
                final List<StockResponse> stockResponses = result.getData();

                assertPaging(paging, totalElements, getSortsOrDefault(params));
                assertThat(stockResponses.size(), is(SIZE));
                assertPostResponse(stock4, solidarityDailySummary4, stockResponses.get(0));
                assertPostResponse(stock3, solidarityDailySummary3, stockResponses.get(1));
            }
        }

        @Nested
        class AndSecondPage {

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_2;
                params = Map.of(
                    "page", pageNumber.toString(),
                    "size", SIZE.toString(),
                    "sorts", List.of("marketValue:DESC")
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnPosts() throws Exception {
                final long totalElements = stocksTestHolder.getItems().size();
                assertResponse(callApiAndGetResult(), totalElements);
            }

            private void assertResponse(GetStocksResponse result, long totalElements) {
                final Paging paging = result.getPaging();
                final List<StockResponse> stockResponses = result.getData();

                assertPaging(paging, totalElements, getSortsOrDefault(params));
                assertThat(stockResponses.size(), is(SIZE));
                assertPostResponse(stock2, solidarityDailySummary2, stockResponses.get(0));
                assertPostResponse(stock1, solidarityDailySummary1, stockResponses.get(1));
            }
        }

    }

    @Nested
    class WhenSearchSpecificStockCode {

        private Stock stock;
        private SolidarityDailySummary solidarityDailySummary;

        @Nested
        class AndFirstStockCode {

            @BeforeEach
            void setUp() {
                stock = stock4;
                solidarityDailySummary = solidarityDailySummary4;

                pageNumber = PAGE_1;
                params = Map.of(
                    "code", stock.getCode(),
                    "page", pageNumber.toString(),
                    "size", SIZE.toString()
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnPosts() throws Exception {
                assertResponse(callApiAndGetResult());
            }

            private void assertResponse(GetStocksResponse result) {
                final Paging paging = result.getPaging();
                final List<StockResponse> stockResponses = result.getData();

                assertPaging(paging, 1L, getSortsOrDefault(params));
                assertThat(stockResponses.size(), is(1));
                assertPostResponse(stock, solidarityDailySummary, stockResponses.get(0));
            }
        }

        @Nested
        class AndSecondStockCode {

            @BeforeEach
            void setUp() {
                stock = stock2;
                solidarityDailySummary = solidarityDailySummary2;

                pageNumber = PAGE_1;
                params = Map.of(
                    "code", stock.getCode(),
                    "page", pageNumber.toString(),
                    "size", SIZE.toString()
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnPosts() throws Exception {
                assertResponse(callApiAndGetResult());
            }

            private void assertResponse(GetStocksResponse result) {
                final Paging paging = result.getPaging();
                final List<StockResponse> stockResponses = result.getData();

                assertPaging(paging, 1L, getSortsOrDefault(params));
                assertThat(stockResponses.size(), is(1));
                assertPostResponse(stock, solidarityDailySummary, stockResponses.get(0));
            }
        }

        @Nested
        class AndNotFoundStock {

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_1;
                params = Map.of(
                    "code", someStockCode(),
                    "page", pageNumber.toString(),
                    "size", SIZE.toString()
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnPosts() throws Exception {
                assertResponse(callApiAndGetResult());
            }

            private void assertResponse(GetStocksResponse result) {
                final Paging paging = result.getPaging();
                final List<StockResponse> stockResponses = result.getData();

                assertPaging(paging, 0L, getSortsOrDefault(params));
                assertThat(stockResponses.size(), is(0));
            }
        }
    }

    @Nested
    class WhenSearchPrivateStocks {

        @BeforeEach
        void setUp() {
            stock1.setIsPrivate(true);
            stock2.setIsPrivate(true);
            stock3.setIsPrivate(true);
            stock4.setIsPrivate(true);

            itUtil.updateStock(stock1);
            itUtil.updateStock(stock2);
            itUtil.updateStock(stock3);
            itUtil.updateStock(stock4);

            mockNotPrivateStocks();
        }

        private void mockNotPrivateStocks() {
            for (int i = 0; i < someIntegerBetween(1, 3); i++) {
                itUtil.createStock();
            }
        }

        @Nested
        class AndFirstPage {

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_1;
                params = Map.of(
                    "page", pageNumber.toString(),
                    "size", SIZE.toString(),
                    "isPrivate", Boolean.TRUE.toString()
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnPosts() throws Exception {
                final long totalElements = stocksTestHolder.getItems().size();
                assertResponse(callApiAndGetResult(), totalElements);
            }

            private void assertResponse(GetStocksResponse result, long totalElements) {
                final Paging paging = result.getPaging();
                final List<StockResponse> stockResponses = result.getData();

                assertPaging(paging, totalElements, getSortsOrDefault(params));
                assertThat(stockResponses.size(), is(SIZE));
                assertPostResponse(stock4, solidarityDailySummary4, stockResponses.get(0));
                assertPrivateStock(stockResponses.get(0));
                assertPostResponse(stock3, solidarityDailySummary3, stockResponses.get(1));
                assertPrivateStock(stockResponses.get(1));
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
                final long totalElements = stocksTestHolder.getItems().size();
                assertResponse(callApiAndGetResult(), totalElements);
            }

            private void assertResponse(GetStocksResponse result, long totalElements) {
                final Paging paging = result.getPaging();
                final List<StockResponse> stockResponses = result.getData();

                assertPaging(paging, totalElements, getSortsOrDefault(params));
                assertThat(stockResponses.size(), is(SIZE));
                assertPostResponse(stock2, solidarityDailySummary2, stockResponses.get(0));
                assertPrivateStock(stockResponses.get(0));
                assertPostResponse(stock1, solidarityDailySummary1, stockResponses.get(1));
                assertPrivateStock(stockResponses.get(1));
            }
        }

        private void assertPrivateStock(StockResponse stockResponse) {
            assertThat(stockResponse.getIsPrivate(), is(true));
        }

    }
}
