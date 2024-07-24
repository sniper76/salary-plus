package ag.act.api.admin.push;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.StockGroup;
import ag.act.entity.User;
import ag.act.enums.AppLinkType;
import ag.act.enums.push.PushSendStatus;
import ag.act.enums.push.PushSendType;
import ag.act.enums.push.PushTargetType;
import ag.act.enums.push.PushTopic;
import ag.act.model.PushDetailsResponse;
import ag.act.util.AppLinkUrlGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static ag.act.TestUtil.assertTime;
import static ag.act.TestUtil.someInstantInTheFuture;
import static ag.act.TestUtil.someStockCode;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.either;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

class CreatePushApiIntegrationTest extends AbstractCommonIntegrationTest {
    @Autowired
    private AppLinkUrlGenerator appLinkUrlGenerator;

    private static final String TARGET_API = "/api/admin/pushes";
    private static final String NOTIFICATION_URL = "/notification";
    private static final String MAIN_HOME_URL = "/main";
    private static final String NEWS_HOME_URL = "/globalboard";

    private String jwt;
    private Stock stock;
    private StockGroup stockGroup;
    private Post post;
    private ag.act.model.CreatePushRequest request;

    @BeforeEach
    void setUp() {
        itUtil.init();
        final User user = itUtil.createAdminUser();
        jwt = itUtil.createJwt(user.getId());
        stock = itUtil.createStock(someStockCode());
        stockGroup = itUtil.createStockGroup(someAlphanumericString(10));

        User writer = itUtil.createUser();
        Stock postStock = itUtil.createStock();
        Board board = itUtil.createBoard(postStock);
        post = itUtil.createPost(board, writer.getId());
    }

    @Nested
    class WhenCreatePush {
        @Nested
        class WhenCreateWithPrevSpec {
            @Nested
            class AndRequestTargetIsAll {

                @BeforeEach
                void setUp() {
                    request = genRequestPrevSpec();
                }

                @Nested
                class AndPushSendTypeIsScheduled {

                    @BeforeEach
                    void setUp() {
                        request.setSendType(PushSendType.SCHEDULE.name());
                    }

                    @DisplayName("Should return 200 response code when call " + TARGET_API)
                    @Test
                    void shouldReturnSuccess() throws Exception {
                        assertResponse(callApi());
                    }

                    private void assertResponse(ag.act.model.PushDetailsDataResponse result) {

                        final PushDetailsResponse pushDetailsResponse = result.getData();

                        assertPushDetailsResponseScheduledSendType(pushDetailsResponse);
                        assertPushListItemResponseForTopic(pushDetailsResponse);
                    }
                }

                @Nested
                class AndPushSendTypeIsImmediately {

                    @BeforeEach
                    void setUp() {
                        request.setSendType(PushSendType.IMMEDIATELY.name());
                    }

                    @Test
                    void shouldReturnSuccess() throws Exception {
                        assertResponse(callApi());
                    }

                    private void assertResponse(ag.act.model.PushDetailsDataResponse result) {

                        final PushDetailsResponse pushDetailsResponse = result.getData();

                        assertPushDetailsResponseImmediatelySendType(pushDetailsResponse);
                        assertPushListItemResponseForTopic(pushDetailsResponse);
                    }
                }
            }
        }

        @Nested
        class AndRequestTargetIsAll {

            @BeforeEach
            void setUp() {
                request = genRequest();
            }

            @Nested
            class AndPushSendTypeIsScheduled {

                @BeforeEach
                void setUp() {
                    request.setSendType(PushSendType.SCHEDULE.name());
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnSuccess() throws Exception {
                    assertResponse(callApi());
                }

                private void assertResponse(ag.act.model.PushDetailsDataResponse result) {

                    final PushDetailsResponse pushDetailsResponse = result.getData();

                    assertPushDetailsResponseScheduledSendType(pushDetailsResponse);
                    assertPushListItemResponseForTopic(pushDetailsResponse);
                }
            }

            @Nested
            class AndPushSendTypeIsImmediately {

                @BeforeEach
                void setUp() {
                    request.setSendType(PushSendType.IMMEDIATELY.name());
                    request.setLinkType(AppLinkType.LINK.name());
                    request.setTargetDatetime(null);
                }

                @Test
                void shouldReturnSuccess() throws Exception {
                    assertResponse(callApi());
                }

                private void assertResponse(ag.act.model.PushDetailsDataResponse result) {

                    final PushDetailsResponse pushDetailsResponse = result.getData();

                    assertPushDetailsResponseImmediatelySendType(pushDetailsResponse);
                    assertPushListItemResponseForTopic(pushDetailsResponse);
                }
            }

        }

        @Nested
        class AndRequestTargetIsStock {

            @BeforeEach
            void setUp() {
                request = genRequest(stock.getCode());
            }

            @Nested
            class AndPushSendTypeIsScheduled {

                @BeforeEach
                void setUp() {
                    request.setSendType(PushSendType.SCHEDULE.name());
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnSuccess() throws Exception {
                    assertResponse(callApi());
                }

                private void assertResponse(ag.act.model.PushDetailsDataResponse result) {

                    final PushDetailsResponse pushDetailsResponse = result.getData();

                    assertPushDetailsResponseScheduledSendType(pushDetailsResponse);
                    assertPushListItemResponseForStock(pushDetailsResponse, stock);
                }
            }

            @Nested
            class AndPushSendTypeIsImmediately {

                @BeforeEach
                void setUp() {
                    request.setSendType(PushSendType.IMMEDIATELY.name());
                    request.setTargetDatetime(null);
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnSuccess() throws Exception {
                    assertResponse(callApi());
                }

                private void assertResponse(ag.act.model.PushDetailsDataResponse result) {

                    final PushDetailsResponse pushDetailsResponse = result.getData();

                    assertPushDetailsResponseImmediatelySendType(pushDetailsResponse);
                    assertPushListItemResponseForStock(pushDetailsResponse, stock);
                }
            }

            @Nested
            class AndLinkTypeIsLink {
                @BeforeEach
                void setUp() {
                    request.setSendType(PushSendType.IMMEDIATELY.name());
                    request.setLinkType(AppLinkType.LINK.name());
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnSuccess() throws Exception {
                    final PushDetailsResponse detailsResponse = callApi().getData();

                    assertPushDetailsResponseImmediatelySendType(detailsResponse);
                    assertPushListItemResponseForStock(detailsResponse, stock);
                    assertThat(detailsResponse.getLinkUrl(), is(appLinkUrlGenerator.generateBoardGroupPostLinkUrl(post)));
                }
            }

            @Nested
            class AndLinkTypeIsNotification {
                @BeforeEach
                void setUp() {
                    request.setSendType(PushSendType.IMMEDIATELY.name());
                    request.setLinkType(AppLinkType.NOTIFICATION.name());
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnSuccess() throws Exception {
                    final PushDetailsResponse detailsResponse = callApi().getData();

                    assertPushDetailsResponseImmediatelySendType(detailsResponse);
                    assertPushListItemResponseForStock(detailsResponse, stock);
                    assertThat(detailsResponse.getLinkUrl(), is(NOTIFICATION_URL));
                }
            }

            @Nested
            class AndLinkTypeIsMainHome {
                @BeforeEach
                void setUp() {
                    request.setSendType(PushSendType.IMMEDIATELY.name());
                    request.setLinkType(AppLinkType.MAIN_HOME.name());
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnSuccess() throws Exception {
                    final PushDetailsResponse detailsResponse = callApi().getData();

                    assertPushDetailsResponseImmediatelySendType(detailsResponse);
                    assertPushListItemResponseForStock(detailsResponse, stock);
                    assertThat(detailsResponse.getLinkUrl(), is(MAIN_HOME_URL));
                }
            }

            @Nested
            class AndLinkTypeIsNewHome {
                @BeforeEach
                void setUp() {
                    request.setSendType(PushSendType.IMMEDIATELY.name());
                    request.setLinkType(AppLinkType.NEWS_HOME.name());
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnSuccess() throws Exception {
                    final PushDetailsResponse detailsResponse = callApi().getData();

                    assertPushDetailsResponseImmediatelySendType(detailsResponse);
                    assertPushListItemResponseForStock(detailsResponse, stock);
                    assertThat(detailsResponse.getLinkUrl(), is(NEWS_HOME_URL));
                }
            }

            @Nested
            class AndLinkTypeIsStockHome {
                @BeforeEach
                void setUp() {
                    request.setSendType(PushSendType.IMMEDIATELY.name());
                    request.setLinkType(AppLinkType.STOCK_HOME.name());
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnSuccess() throws Exception {
                    final PushDetailsResponse detailsResponse = callApi().getData();

                    assertPushDetailsResponseImmediatelySendType(detailsResponse);
                    assertPushListItemResponseForStock(detailsResponse, stock);
                    assertThat(
                        detailsResponse.getLinkUrl(),
                        is(appLinkUrlGenerator.generateStockHomeLinkUrl(stock.getCode()))
                    );
                }
            }

            @Nested
            class AndLinkTypeIsNone {
                @BeforeEach
                void setUp() {
                    request.setSendType(PushSendType.IMMEDIATELY.name());
                    request.setLinkType(AppLinkType.NONE.name());
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnSuccess() throws Exception {
                    final PushDetailsResponse detailsResponse = callApi().getData();

                    assertPushDetailsResponseImmediatelySendType(detailsResponse);
                    assertPushListItemResponseForStock(detailsResponse, stock);
                    assertThat(detailsResponse.getLinkUrl(), nullValue());
                }
            }
        }

        @Nested
        class AndRequestTargetIsStockGroup {

            @BeforeEach
            void setUp() {
                request = genRequest(stockGroup.getId());
            }

            @Nested
            class AndPushSendTypeIsScheduled {

                @BeforeEach
                void setUp() {
                    request.setSendType(PushSendType.SCHEDULE.name());
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnSuccess() throws Exception {
                    assertResponse(callApi());
                }

                private void assertResponse(ag.act.model.PushDetailsDataResponse result) {

                    final PushDetailsResponse pushDetailsResponse = result.getData();

                    assertPushDetailsResponseScheduledSendType(pushDetailsResponse);
                    assertPushListItemResponseForStockGroup(pushDetailsResponse, stockGroup);
                }
            }

            @Nested
            class AndPushSendTypeIsImmediately {

                @BeforeEach
                void setUp() {
                    request.setSendType(PushSendType.IMMEDIATELY.name());
                    request.setTargetDatetime(null);
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnSuccess() throws Exception {
                    assertResponse(callApi());
                }

                private void assertResponse(ag.act.model.PushDetailsDataResponse result) {

                    final PushDetailsResponse pushDetailsResponse = result.getData();

                    assertPushDetailsResponseImmediatelySendType(pushDetailsResponse);
                    assertPushListItemResponseForStockGroup(pushDetailsResponse, stockGroup);
                }
            }

        }

        private void assertPushDetailsResponseScheduledSendType(PushDetailsResponse pushDetailsResponse) {
            assertPushDetailsResponse(pushDetailsResponse);
            assertTime(pushDetailsResponse.getTargetDatetime(), request.getTargetDatetime());
            assertThat(pushDetailsResponse.getSentEndDatetime(), nullValue());
            assertThat(pushDetailsResponse.getSentStartDatetime(), nullValue());
        }

        private void assertPushDetailsResponseImmediatelySendType(PushDetailsResponse pushDetailsResponse) {
            assertPushDetailsResponse(pushDetailsResponse);
            assertThat(pushDetailsResponse.getTargetDatetime(), notNullValue());
            assertThat(pushDetailsResponse.getSentEndDatetime(), nullValue());
            assertThat(pushDetailsResponse.getSentStartDatetime(), nullValue());
        }

        private void assertPushDetailsResponse(PushDetailsResponse pushDetailsResponse) {
            assertThat(pushDetailsResponse.getId(), notNullValue());
            assertThat(pushDetailsResponse.getTitle(), is(request.getTitle().trim()));
            assertThat(pushDetailsResponse.getContent(), is(request.getContent().trim()));
            assertThat(
                pushDetailsResponse.getStockTargetType(),
                either(is(request.getStockTargetType())).or(is(request.getStockTargetType()))
            );
            assertThat(pushDetailsResponse.getSendType(), is(request.getSendType()));
            assertThat(pushDetailsResponse.getSendStatus(), is(PushSendStatus.READY.name()));
            assertThat(pushDetailsResponse.getResult(), nullValue());
            assertThat(pushDetailsResponse.getCreatedAt(), notNullValue());
            assertThat(pushDetailsResponse.getUpdatedAt(), notNullValue());
        }

        private void assertPushListItemResponseForTopic(PushDetailsResponse pushDetailsResponse) {
            assertThat(pushDetailsResponse.getStockCode(), nullValue());
            assertThat(pushDetailsResponse.getStockName(), nullValue());
            assertThat(pushDetailsResponse.getStockGroupId(), nullValue());
            assertThat(pushDetailsResponse.getStockGroupName(), nullValue());
            assertThat(pushDetailsResponse.getTopic(), is(PushTopic.NOTICE.name()));
        }

        private void assertPushListItemResponseForStock(PushDetailsResponse pushDetailsResponse, Stock stock) {
            assertThat(pushDetailsResponse.getStockCode(), is(stock.getCode()));
            assertThat(pushDetailsResponse.getStockName(), is(stock.getName()));
            assertThat(pushDetailsResponse.getStockGroupId(), nullValue());
            assertThat(pushDetailsResponse.getStockGroupName(), nullValue());
            assertThat(pushDetailsResponse.getTopic(), nullValue());
        }

        private void assertPushListItemResponseForStockGroup(PushDetailsResponse pushDetailsResponse, StockGroup stockGroup) {
            assertThat(pushDetailsResponse.getStockGroupId(), is(stockGroup.getId()));
            assertThat(pushDetailsResponse.getStockGroupName(), is(stockGroup.getName()));
            assertThat(pushDetailsResponse.getStockCode(), nullValue());
            assertThat(pushDetailsResponse.getStockName(), nullValue());
            assertThat(pushDetailsResponse.getTopic(), nullValue());
        }

        private ag.act.model.PushDetailsDataResponse callApi() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    post(TARGET_API)
                        .content(objectMapperUtil.toRequestBody(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .headers(headers(jwt(jwt)))
                )
                .andExpect(status().isOk())
                .andReturn();

            return objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                ag.act.model.PushDetailsDataResponse.class
            );
        }

        @Nested
        class CreateWithoutTitle {

            @BeforeEach
            void setUp() {
                request = genRequest();
            }

            private ag.act.model.CreatePushRequest genRequest() {
                return new ag.act.model.CreatePushRequest()
                    .content(someAlphanumericString(10))
                    .stockCode(null)
                    .stockTargetType(PushTargetType.ALL.name())
                    .sendType(someEnum(PushSendType.class).name())
                    .targetDatetime(someInstantInTheFuture())
                    .linkType(AppLinkType.NONE.name())
                    .postId(post.getId());
            }

            @Test
            void shouldReturnSuccess() throws Exception {
                assertResponse(callApi());
            }

            private void assertResponse(ag.act.model.PushDetailsDataResponse result) {
                final PushDetailsResponse pushDetailsResponse = result.getData();

                assertPushDetailsResponse(pushDetailsResponse);
            }

            private void assertPushDetailsResponse(PushDetailsResponse pushDetailsResponse) {
                assertThat(pushDetailsResponse.getId(), notNullValue());
                assertThat(pushDetailsResponse.getTitle(), is("액트"));
                assertThat(pushDetailsResponse.getContent(), is(request.getContent().trim()));
                assertThat(
                    pushDetailsResponse.getStockTargetType(),
                    either(is(request.getStockTargetType())).or(is(request.getStockTargetType()))
                );
                assertThat(pushDetailsResponse.getSendType(), is(request.getSendType()));
                assertThat(pushDetailsResponse.getSendStatus(), is(PushSendStatus.READY.name()));
                assertThat(pushDetailsResponse.getResult(), nullValue());
                assertThat(pushDetailsResponse.getCreatedAt(), notNullValue());
                assertThat(pushDetailsResponse.getUpdatedAt(), notNullValue());
            }
        }
    }

    @Nested
    class FailToCreatePush {

        @BeforeEach
        void setUp() {
            request = genRequest();
        }

        @Nested
        class WhenContentIsMissing {
            @BeforeEach
            void setUp() {
                request.setContent("");
            }

            @Test
            void shouldReturnErrorResponse() throws Exception {
                callApiToFail("내용을 확인해주세요.");
            }
        }

        @Nested
        class WhenPushSendTypeIsMissing {
            @BeforeEach
            void setUp() {
                request.setSendType("");
            }

            @Test
            void shouldReturnErrorResponse() throws Exception {
                callApiToFail("발송타입을 확인해주세요.");
            }
        }

        @Nested
        class WhenStockTargetTypeIsMissing {
            @BeforeEach
            void setUp() {
                request.setStockTargetType("");
            }

            @Test
            void shouldReturnErrorResponse() throws Exception {
                callApiToFail("타겟타입을 확인해주세요.");
            }
        }

        @Nested
        class WhenStockTargetTypeIsStock {
            @BeforeEach
            void setUp() {
                request.setStockTargetType(PushTargetType.STOCK.name());
            }

            @Nested
            class AndStockCodeIsMissing {
                @BeforeEach
                void setUp() {
                    request.setStockCode("");
                }

                @Test
                void shouldReturnErrorResponse() throws Exception {
                    callApiToFail("종목코드를 확인해주세요.");
                }
            }

            @Nested
            class AndStockNotFoundByStockCode {
                @BeforeEach
                void setUp() {
                    request.setStockCode(someStockCode());
                }

                @Test
                void shouldReturnErrorResponse() throws Exception {
                    callApiToFail("종목코드를 확인해주세요.");
                }
            }
        }

        @Nested
        class WhenStockTargetTypeIsStockGroup {
            @BeforeEach
            void setUp() {
                request.setStockTargetType(PushTargetType.STOCK_GROUP.name());
            }

            @Nested
            class AndStockGroupIdIsMissing {
                @BeforeEach
                void setUp() {
                    request.setStockGroupId(null);
                }

                @Test
                void shouldReturnErrorResponse() throws Exception {
                    callApiToFail("종목그룹 아이디를 확인해주세요.");
                }
            }

            @Nested
            class AndStockGroupNotFoundByStockCode {
                @BeforeEach
                void setUp() {
                    request.setStockGroupId(someLong());
                }

                @Test
                void shouldReturnErrorResponse() throws Exception {
                    callApiToFail("종목그룹 아이디를 확인해주세요.");
                }
            }
        }

        @Nested
        class WhenPushSendTypeIsScheduled {

            @BeforeEach
            void setUp() {
                request.setSendType(PushSendType.SCHEDULE.name());
            }

            @Nested
            class AndTargetDatetimeIsMissing {

                @BeforeEach
                void setUp() {
                    request.setTargetDatetime(null);
                }

                @Test
                void shouldReturnErrorResponse() throws Exception {
                    callApiToFail("발송시간을 확인해주세요.");
                }
            }
        }

        @Nested
        class WhenCreatePushToPost {
            @BeforeEach
            void setUp() {
                request.setLinkType(AppLinkType.LINK.name());
            }

            @Nested
            class AndPostIdNull {
                @BeforeEach
                void setUp() {
                    request.setPostId(null);
                }

                @Test
                void shouldReturnErrorResponse() throws Exception {
                    callApiToFail("게시물 아이디를 확인해주세요.");
                }
            }

            @Nested
            class AndPostNotExist {
                @BeforeEach
                void setUp() {
                    request.setPostId(someLong());
                }

                @Test
                void shouldReturnErrorResponse() throws Exception {
                    callApiToFail("해당 게시글을 찾을 수 없습니다.");
                }
            }
        }

        private void callApiToFail(String expectedErrorMessage) throws Exception {
            MvcResult response = mockMvc
                .perform(
                    post(TARGET_API)
                        .content(objectMapperUtil.toRequestBody(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .headers(headers(jwt(jwt)))
                )
                .andExpect(status().isBadRequest())
                .andReturn();

            itUtil.assertErrorResponse(response, 400, expectedErrorMessage);
        }
    }

    private ag.act.model.CreatePushRequest genRequest() {
        return new ag.act.model.CreatePushRequest()
            .title(someAlphanumericString(10))
            .content(someAlphanumericString(10))
            .stockCode(null)
            .stockTargetType(PushTargetType.ALL.name())
            .sendType(someEnum(PushSendType.class).name())
            .targetDatetime(someInstantInTheFuture())
            .linkType(AppLinkType.NONE.name())
            .postId(post.getId());
    }

    private ag.act.model.CreatePushRequest genRequest(String stockCode) {
        return new ag.act.model.CreatePushRequest()
            .title(someAlphanumericString(10))
            .content(someAlphanumericString(10))
            .stockCode(stockCode)
            .stockTargetType(PushTargetType.STOCK.name())
            .sendType(someEnum(PushSendType.class).name())
            .targetDatetime(someInstantInTheFuture())
            .linkType(AppLinkType.NONE.name())
            .postId(post.getId());
    }

    private ag.act.model.CreatePushRequest genRequest(Long stockGroupId) {
        return new ag.act.model.CreatePushRequest()
            .title(someAlphanumericString(10))
            .content(someAlphanumericString(10))
            .stockGroupId(stockGroupId)
            .stockTargetType(PushTargetType.STOCK_GROUP.name())
            .sendType(someEnum(PushSendType.class).name())
            .targetDatetime(someInstantInTheFuture())
            .linkType(AppLinkType.NONE.name())
            .postId(post.getId());
    }

    private ag.act.model.CreatePushRequest genRequestPrevSpec() {
        return new ag.act.model.CreatePushRequest()
            .title(someAlphanumericString(10))
            .content(someAlphanumericString(10))
            .stockCode(null)
            .stockTargetType(PushTargetType.ALL.name())
            .sendType(someEnum(PushSendType.class).name())
            .targetDatetime(someInstantInTheFuture());
    }
}
