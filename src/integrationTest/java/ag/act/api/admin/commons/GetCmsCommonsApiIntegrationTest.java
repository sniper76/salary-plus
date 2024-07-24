package ag.act.api.admin.commons;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.StockGroup;
import ag.act.entity.User;
import ag.act.model.BoardCategoryResponse;
import ag.act.model.CmsCommonsDataResponse;
import ag.act.model.CmsCommonsResponse;
import ag.act.model.SimpleBoardGroupResponse;
import ag.act.model.SimpleStockGroupResponse;
import ag.act.model.SimpleStockResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static ag.act.TestUtil.someStockCode;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class GetCmsCommonsApiIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/admin/cms-commons";

    private String jwt;

    @BeforeEach
    void setUp() {
        itUtil.init();
        final User user = itUtil.createAdminUser();
        jwt = itUtil.createJwt(user.getId());
    }

    private CmsCommonsDataResponse getResponse(MvcResult response) throws Exception {
        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            CmsCommonsDataResponse.class
        );
    }

    private MvcResult callApi() throws Exception {
        return mockMvc
            .perform(
                get(TARGET_API)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(status().isOk())
            .andReturn();
    }

    @Nested
    class GetSimpleStockGroups {

        private List<SimpleStockGroupResponse> expectedStockGroups;
        private StockGroup deletedStockGroup;

        @BeforeEach
        void setUp() {

            create10StockGroups();
            deletedStockGroup = itUtil.createDeletedStockGroup(someString(0, 15));

            expectedStockGroups = itUtil.findAllActiveStockGroups()
                .stream()
                .map(stockGroup -> new SimpleStockGroupResponse().id(stockGroup.getId()).name(stockGroup.getName()))
                .sorted(Comparator.comparing(SimpleStockGroupResponse::getName))
                .toList();
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            assertResponse(getResponse(callApi()));
        }

        private void assertResponse(CmsCommonsDataResponse result) {
            final CmsCommonsResponse data = result.getData();

            assertThat(data.getStockGroups(), is(expectedStockGroups));
            assertThat(data.getStockGroups().contains(deletedStockGroup), is(false));
        }

        private void create10StockGroups() {
            IntStream.range(0, 10).forEach(index -> itUtil.createStockGroup(someAlphanumericString(20)));
        }

    }

    @Nested
    class GetSimpleStocks {

        private List<SimpleStockResponse> expectedStocks;

        @BeforeEach
        void setUp() {

            create10Stocks();

            expectedStocks = itUtil.findAllStocks()
                .stream()
                .map(stock -> new SimpleStockResponse().code(stock.getCode()).name(stock.getName()).standardCode(stock.getStandardCode()))
                .sorted(Comparator.comparing(SimpleStockResponse::getName))
                .toList();
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            assertResponse(getResponse(callApi()));
        }

        private void assertResponse(CmsCommonsDataResponse result) {
            final CmsCommonsResponse data = result.getData();

            assertThat(data.getStocks(), is(expectedStocks));
        }

        private void create10Stocks() {
            IntStream.range(0, 10).forEach(index -> itUtil.createStock(someStockCode()));
        }

    }

    @Nested
    class GetSimpleBoardGroups {

        private List<SimpleBoardGroupResponse> expectedBoardGroups;

        @BeforeEach
        void setUp() {
            expectedBoardGroups = itUtil.findAllBoardGroups()
                .stream()
                .map(boardGroup ->
                    new SimpleBoardGroupResponse()
                        .name(boardGroup.name())
                        .displayName(boardGroup.getDisplayName())
                        .categories(boardGroup.getCategories().stream()
                            .map(boardCategory -> new BoardCategoryResponse()
                                .name(boardCategory.name())
                                .displayName(boardCategory.getDisplayName())
                                .badgeColor(boardCategory.getBadgeColor().orElse(null)))
                            .toList()
                        )
                )
                .toList();
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            assertResponse(getResponse(callApi()));
        }

        private void assertResponse(CmsCommonsDataResponse result) {
            final CmsCommonsResponse data = result.getData();

            assertThat(data.getBoardGroups(), is(expectedBoardGroups));
        }

    }
}
