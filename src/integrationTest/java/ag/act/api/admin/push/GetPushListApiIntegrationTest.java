package ag.act.api.admin.push;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Push;
import ag.act.entity.Stock;
import ag.act.entity.StockGroup;
import ag.act.entity.User;
import ag.act.enums.push.PushSearchType;
import ag.act.enums.push.PushTargetType;
import ag.act.model.PushDetailsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static ag.act.TestUtil.assertTime;
import static ag.act.TestUtil.someStockCode;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomLongs.somePositiveLong;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class GetPushListApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/pushes";

    private String jwt;
    private Stock stock;
    private String searchKeyword;
    private String searchType;
    private StockGroup stockGroup;

    @BeforeEach
    void setUp() {
        final String stockCode = someStockCode();

        itUtil.init();
        dbCleaner.clean();

        final User user = itUtil.createAdminUser();
        jwt = itUtil.createJwt(user.getId());
        stock = itUtil.createStock(stockCode);
        stockGroup = itUtil.createStockGroup(someString(20));
    }

    @Nested
    class WhenPushSearchTypeIsPushTitle {
        @BeforeEach
        void setUp() {
            searchType = PushSearchType.PUSH_TITLE.name();
        }

        @Nested
        class AndSearchKeywordProvided {

            private Push expectedPush;

            @BeforeEach
            void setUp() {
                searchKeyword = "TEST_PUSH_TITLE";

                final String title = someString(10) + searchKeyword + someString(10);
                expectedPush = itUtil.createPush(title, someString(10), PushTargetType.ALL);
                itUtil.createPush(someAlphanumericString(15), stock, PushTargetType.STOCK);
                itUtil.createPush(someAlphanumericString(20), stock, PushTargetType.STOCK);
                itUtil.createPush(someAlphanumericString(20), PushTargetType.AUTOMATED_AUTHOR, somePositiveLong());
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                assertResponse(callApi());
            }

            private void assertResponse(ag.act.model.GetPushDataResponse result) {

                final List<PushDetailsResponse> pushDetailsResponses = result.getData();

                assertThat(pushDetailsResponses.size(), is(1));
                assertPushListItemResponse(pushDetailsResponses.get(0), expectedPush);
                assertPushListItemResponseForTopic(pushDetailsResponses.get(0));
            }
        }
    }

    @Nested
    class WhenPushSearchTypeIsPushContent {

        @BeforeEach
        void setUp() {
            searchType = PushSearchType.PUSH_CONTENT.name();
        }

        @Nested
        class AndSearchKeywordProvided {

            private Push expectedPush;

            @BeforeEach
            void setUp() {
                searchKeyword = "TEST_PUSH_CONTENT";

                expectedPush = itUtil.createPush(someString(10) + searchKeyword + someString(10), PushTargetType.ALL);
                itUtil.createPush(someAlphanumericString(15), stock, PushTargetType.STOCK);
                itUtil.createPush(someAlphanumericString(20), stock, PushTargetType.STOCK);
                itUtil.createPush(someAlphanumericString(20), PushTargetType.AUTOMATED_AUTHOR, somePositiveLong());
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                assertResponse(callApi());
            }

            private void assertResponse(ag.act.model.GetPushDataResponse result) {

                final List<PushDetailsResponse> pushDetailsResponses = result.getData();

                assertThat(pushDetailsResponses.size(), is(1));
                assertPushListItemResponse(pushDetailsResponses.get(0), expectedPush);
                assertPushListItemResponseForTopic(pushDetailsResponses.get(0));
            }

        }

        @Nested
        class AndSearchKeywordIsEmpty {

            private List<Push> expectedPushes;

            @BeforeEach
            void setUp() {
                searchKeyword = "";

                expectedPushes = List.of(
                    itUtil.createPush(someAlphanumericString(10), PushTargetType.ALL),
                    itUtil.createPush(someAlphanumericString(15), stock, PushTargetType.STOCK),
                    itUtil.createPush(someAlphanumericString(20), stock, PushTargetType.STOCK),
                    itUtil.createPush(someAlphanumericString(30), PushTargetType.ALL),
                    itUtil.createPush(someAlphanumericString(25), PushTargetType.AUTOMATED_AUTHOR, somePositiveLong())
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnAllPushesInCreatedAtDesc() throws Exception {
                assertResponse(callApi());
            }

            private void assertResponse(ag.act.model.GetPushDataResponse result) {

                final List<PushDetailsResponse> pushListItemResponseList = result.getData();

                assertThat(pushListItemResponseList.size(), is(4));

                assertPushListItemResponse(pushListItemResponseList.get(0), expectedPushes.get(3));
                assertPushListItemResponseForTopic(pushListItemResponseList.get(0));

                assertPushListItemResponse(pushListItemResponseList.get(1), expectedPushes.get(2));
                assertPushListItemResponseForStock(pushListItemResponseList.get(1), expectedPushes.get(2).getStock());

                assertPushListItemResponse(pushListItemResponseList.get(2), expectedPushes.get(1));
                assertPushListItemResponseForStock(pushListItemResponseList.get(2), expectedPushes.get(1).getStock());

                assertPushListItemResponse(pushListItemResponseList.get(3), expectedPushes.get(0));
                assertPushListItemResponseForTopic(pushListItemResponseList.get(3));
            }
        }
    }

    @Nested
    class WhenSearchStockName {

        private Push expectedPush1;
        private Push expectedPush2;

        @BeforeEach
        void setUp() {
            searchType = PushSearchType.STOCK_NAME.name();
            searchKeyword = stock.getName().substring(2, 5);

            itUtil.createPush(someString(10) + searchKeyword + someString(10), PushTargetType.ALL);
            expectedPush1 = itUtil.createPush(someAlphanumericString(15), stock, PushTargetType.STOCK);
            expectedPush2 = itUtil.createPush(someAlphanumericString(20), stock, PushTargetType.STOCK);
            itUtil.createPush(someAlphanumericString(20), PushTargetType.AUTOMATED_AUTHOR, somePositiveLong());
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            assertResponse(callApi());
        }

        private void assertResponse(ag.act.model.GetPushDataResponse result) {

            final List<PushDetailsResponse> pushDetailsResponses = result.getData();

            assertThat(pushDetailsResponses.size(), is(2));
            assertPushListItemResponse(pushDetailsResponses.get(0), expectedPush2);
            assertPushListItemResponseForStock(pushDetailsResponses.get(0), stock);
            assertPushListItemResponse(pushDetailsResponses.get(1), expectedPush1);
            assertPushListItemResponseForStock(pushDetailsResponses.get(1), stock);
        }

    }

    @Nested
    class WhenSearchStockGroupName {

        private Push expectedPush1;

        private Push expectedPush2;

        @BeforeEach
        void setUp() {
            searchType = PushSearchType.STOCK_GROUP_NAME.name();
            searchKeyword = stockGroup.getName().substring(2, 5);

            itUtil.createPush(someString(10) + searchKeyword + someString(10), PushTargetType.ALL);
            expectedPush1 = itUtil.createPush(someAlphanumericString(15), stockGroup, PushTargetType.STOCK_GROUP);
            expectedPush2 = itUtil.createPush(someAlphanumericString(20), stockGroup, PushTargetType.STOCK_GROUP);
            itUtil.createPush(someAlphanumericString(20), PushTargetType.AUTOMATED_AUTHOR, somePositiveLong());
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            ag.act.model.GetPushDataResponse result = callApi();

            final List<PushDetailsResponse> pushDetailsResponses = result.getData();

            assertThat(pushDetailsResponses.size(), is(2));
            assertPushListItemResponse(pushDetailsResponses.get(0), expectedPush2);
            assertPushListItemResponseForStockGroup(pushDetailsResponses.get(0), stockGroup);
            assertPushListItemResponse(pushDetailsResponses.get(1), expectedPush1);
            assertPushListItemResponseForStockGroup(pushDetailsResponses.get(1), stockGroup);
        }
    }

    private void assertPushListItemResponseForTopic(PushDetailsResponse pushListItemResponse) {
        assertThat(pushListItemResponse.getStockGroupId(), nullValue());
        assertThat(pushListItemResponse.getStockGroupName(), nullValue());
        assertThat(pushListItemResponse.getStockCode(), nullValue());
        assertThat(pushListItemResponse.getStockName(), nullValue());
        assertThat(pushListItemResponse.getTopic(), notNullValue());
    }

    private void assertPushListItemResponseForStock(PushDetailsResponse pushListItemResponse, Stock stock) {
        assertThat(pushListItemResponse.getStockGroupId(), nullValue());
        assertThat(pushListItemResponse.getStockGroupName(), nullValue());
        assertThat(pushListItemResponse.getStockCode(), is(stock.getCode()));
        assertThat(pushListItemResponse.getStockName(), is(stock.getName()));
        assertThat(pushListItemResponse.getTopic(), nullValue());
    }

    private void assertPushListItemResponseForStockGroup(PushDetailsResponse pushListItemResponse, StockGroup stockGroup) {
        assertThat(pushListItemResponse.getStockGroupId(), is(stockGroup.getId()));
        assertThat(pushListItemResponse.getStockGroupName(), is(stockGroup.getName()));
        assertThat(pushListItemResponse.getStockCode(), nullValue());
        assertThat(pushListItemResponse.getStockName(), nullValue());
        assertThat(pushListItemResponse.getTopic(), nullValue());
    }

    private void assertPushListItemResponse(PushDetailsResponse pushListItemResponse, Push expectedPush) {
        assertThat(pushListItemResponse.getId(), is(expectedPush.getId()));
        assertThat(pushListItemResponse.getTitle(), is(expectedPush.getTitle()));
        assertThat(pushListItemResponse.getContent(), is(expectedPush.getContent()));
        assertThat(pushListItemResponse.getStockTargetType(), is(expectedPush.getPushTargetType().name()));
        assertThat(pushListItemResponse.getSendType(), is(expectedPush.getSendType().name()));
        assertThat(pushListItemResponse.getSendStatus(), is(expectedPush.getSendStatus().name()));
        assertThat(pushListItemResponse.getResult(), is(expectedPush.getResult()));
        assertThat(pushListItemResponse.getLinkType(), is(expectedPush.getLinkType().name()));
        assertThat(pushListItemResponse.getLinkUrl(), is(expectedPush.getLinkUrl()));
        assertTime(pushListItemResponse.getCreatedAt(), expectedPush.getCreatedAt());
        assertTime(pushListItemResponse.getUpdatedAt(), expectedPush.getUpdatedAt());
        assertTime(pushListItemResponse.getTargetDatetime(), expectedPush.getTargetDatetime());
        assertTime(pushListItemResponse.getSentEndDatetime(), expectedPush.getSentEndDatetime());
        assertTime(pushListItemResponse.getSentStartDatetime(), expectedPush.getSentStartDatetime());
    }

    private ag.act.model.GetPushDataResponse callApi() throws Exception {
        MvcResult response = mockMvc
            .perform(
                get(TARGET_API)
                    .param("searchKeyword", searchKeyword)
                    .param("searchType", searchType)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(status().isOk())
            .andReturn();

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            ag.act.model.GetPushDataResponse.class
        );
    }
}
