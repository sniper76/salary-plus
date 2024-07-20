package ag.act.api.admin.push;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Push;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.AutomatedPushContentType;
import ag.act.enums.AutomatedPushCriteria;
import ag.act.enums.push.PushSearchType;
import ag.act.enums.push.PushTargetType;
import ag.act.model.GetPushDataResponse;
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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomLongs.somePositiveLong;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

class GetAutomatedPushListApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/automated-pushes";

    private String jwt;
    private String searchKeyword;
    private String searchType;
    private Board board;

    private Push expectedPush1;
    private Push expectedPush2;
    private User searchUser;
    private AutomatedPushCriteria automatedPushCriteria;
    private AutomatedPushContentType automatedPushContentType;

    @BeforeEach
    void setUp() {
        dbCleaner.clean();

        final String stockCode = someStockCode();

        final User user = itUtil.createAdminUser();
        jwt = itUtil.createJwt(user.getId());
        final Stock stock = itUtil.createStock(stockCode);
        board = itUtil.createBoard(stock);
    }

    private void assertPushListItemResponse(PushDetailsResponse pushListItemResponse, Push expectedPush) {
        assertThat(pushListItemResponse.getId(), is(expectedPush.getId()));
        assertThat(pushListItemResponse.getTitle(), is(expectedPush.getTitle()));
        assertThat(pushListItemResponse.getContent(), is(expectedPush.getContent()));
        assertTime(pushListItemResponse.getCreatedAt(), expectedPush.getCreatedAt());
        assertTime(pushListItemResponse.getUpdatedAt(), expectedPush.getUpdatedAt());
        assertTime(pushListItemResponse.getTargetDatetime(), expectedPush.getTargetDatetime());
        assertTime(pushListItemResponse.getSentEndDatetime(), expectedPush.getSentEndDatetime());
        assertTime(pushListItemResponse.getSentStartDatetime(), expectedPush.getSentStartDatetime());
    }

    private GetPushDataResponse callApi() throws Exception {
        MvcResult response = mockMvc
            .perform(
                get(TARGET_API)
                    .param("searchKeyword", searchKeyword)
                    .param("searchType", searchType)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(status().isOk())
            .andReturn();

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            GetPushDataResponse.class
        );
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
                searchUser = itUtil.createUser();
                final User orderUser = itUtil.createUser();
                final Post searchPost = itUtil.createPost(board, searchUser.getId(), Boolean.FALSE);
                final Post orderPost = itUtil.createPost(board, orderUser.getId(), Boolean.FALSE);

                automatedPushCriteria = AutomatedPushCriteria.COMMENT;
                automatedPushContentType = AutomatedPushContentType.POST;

                expectedPush = itUtil.createPush(
                    automatedPushCriteria.getTitle(),
                    "aaa%s kkk".formatted(searchKeyword),
                    PushTargetType.AUTOMATED_AUTHOR,
                    somePositiveLong()
                );

                itUtil.createAutomatedAuthorPush(
                    searchPost.getId(), expectedPush.getId(), 10, automatedPushCriteria, automatedPushContentType
                );
                itUtil.createAutomatedAuthorPush(
                    orderPost.getId(),
                    itUtil.createPush(someAlphanumericString(20), PushTargetType.AUTOMATED_AUTHOR, somePositiveLong()).getId(),
                    20, automatedPushCriteria, automatedPushContentType
                );
                itUtil.createAutomatedAuthorPush(
                    orderPost.getId(),
                    itUtil.createPush(someAlphanumericString(25), PushTargetType.STOCK, somePositiveLong()).getId(),
                    20, automatedPushCriteria, automatedPushContentType
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                assertResponse(callApi());
            }

            private void assertResponse(GetPushDataResponse result) {

                final List<PushDetailsResponse> pushDetailsResponses = result.getData();

                assertThat(pushDetailsResponses.size(), is(1));
                assertPushListItemResponse(pushDetailsResponses.get(0), expectedPush);
            }

        }

        @Nested
        class AndSearchKeywordIsEmpty {

            private List<Push> expectedPushes;

            @BeforeEach
            void setUp() {
                searchKeyword = "";
                searchUser = itUtil.createUser();
                final User orderUser = itUtil.createUser();
                final Post searchPost = itUtil.createPost(board, searchUser.getId(), Boolean.FALSE);
                final Post orderPost = itUtil.createPost(board, orderUser.getId(), Boolean.FALSE);

                automatedPushCriteria = AutomatedPushCriteria.COMMENT;
                automatedPushContentType = AutomatedPushContentType.POST;

                expectedPushes = List.of(
                    itUtil.createPush(someAlphanumericString(10), PushTargetType.AUTOMATED_AUTHOR, somePositiveLong()),
                    itUtil.createPush(someAlphanumericString(15), PushTargetType.AUTOMATED_AUTHOR, somePositiveLong()),
                    itUtil.createPush(someAlphanumericString(20), PushTargetType.AUTOMATED_AUTHOR, somePositiveLong()),
                    itUtil.createPush(someAlphanumericString(25), PushTargetType.AUTOMATED_AUTHOR, somePositiveLong()),
                    itUtil.createPush(someAlphanumericString(15), PushTargetType.STOCK)
                );
                for (int index = 0; index < expectedPushes.size(); index++) {
                    itUtil.createAutomatedAuthorPush(
                        index == expectedPushes.size() - 1 ? orderPost.getId() : searchPost.getId(),
                        expectedPushes.get(index).getId(),
                        20, automatedPushCriteria, automatedPushContentType
                    );
                }
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnAllPushesInCreatedAtDesc() throws Exception {
                assertResponse(callApi());
            }

            private void assertResponse(GetPushDataResponse result) {

                final List<PushDetailsResponse> pushListItemResponseList = result.getData();

                assertThat(pushListItemResponseList.size(), is(4));

                assertPushListItemResponse(pushListItemResponseList.get(0), expectedPushes.get(3));
                assertPushListItemResponse(pushListItemResponseList.get(1), expectedPushes.get(2));
                assertPushListItemResponse(pushListItemResponseList.get(2), expectedPushes.get(1));
                assertPushListItemResponse(pushListItemResponseList.get(3), expectedPushes.get(0));
            }
        }
    }

    @Nested
    class WhenSearchName {
        private String userName;

        @BeforeEach
        void setUp() {
            searchKeyword = someAlphanumericString(10);
            userName = someAlphanumericString(5) + searchKeyword + someAlphanumericString(5);

            searchType = PushSearchType.AUTHOR_NAME.name();
            searchUser = itUtil.createUser();
            searchUser.setName(userName);
            itUtil.updateUser(searchUser);

            final User anotherUser = createAnotherUser();
            final Post searchPost = itUtil.createPost(board, searchUser.getId(), Boolean.FALSE);
            final Post orderPost = itUtil.createPost(board, anotherUser.getId(), Boolean.FALSE);

            automatedPushCriteria = AutomatedPushCriteria.COMMENT;
            automatedPushContentType = AutomatedPushContentType.POST;
            expectedPush1 = itUtil.createPush(someAlphanumericString(10), PushTargetType.AUTOMATED_AUTHOR, somePositiveLong());
            expectedPush2 = itUtil.createPush(someAlphanumericString(15), PushTargetType.AUTOMATED_AUTHOR, somePositiveLong());
            itUtil.createAutomatedAuthorPush(
                searchPost.getId(), expectedPush1.getId(), 10, automatedPushCriteria, automatedPushContentType
            );
            itUtil.createAutomatedAuthorPush(
                searchPost.getId(), expectedPush2.getId(), 20, automatedPushCriteria, automatedPushContentType
            );
            itUtil.createAutomatedAuthorPush(
                orderPost.getId(),
                itUtil.createPush(someAlphanumericString(20), PushTargetType.AUTOMATED_AUTHOR, somePositiveLong()).getId(),
                20, automatedPushCriteria, automatedPushContentType
            );
            itUtil.createAutomatedAuthorPush(
                orderPost.getId(),
                itUtil.createPush(someAlphanumericString(25), PushTargetType.AUTOMATED_AUTHOR, somePositiveLong()).getId(),
                20, automatedPushCriteria, automatedPushContentType
            );
        }

        private User createAnotherUser() {
            final User anotherUser = itUtil.createUser();
            anotherUser.setName(someAlphanumericString(20));
            itUtil.updateUser(anotherUser);
            return anotherUser;
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            assertResponse(callApi());
        }

        private void assertResponse(GetPushDataResponse result) {

            final List<PushDetailsResponse> pushDetailsResponses = result.getData();

            assertThat(pushDetailsResponses.size(), is(2));
            assertPushListItemResponse(pushDetailsResponses.get(0), expectedPush2);
            assertPushListItemResponse(pushDetailsResponses.get(1), expectedPush1);
            assertThat(pushDetailsResponses.get(0).getUser().getName(), is(userName));
            assertThat(pushDetailsResponses.get(1).getUser().getName(), is(userName));
        }

    }

    @Nested
    class WhenSearchNickname {

        private String userNickname;

        @BeforeEach
        void setUp() {
            searchKeyword = someAlphanumericString(10);
            userNickname = someAlphanumericString(5) + searchKeyword + someAlphanumericString(5);

            searchType = PushSearchType.AUTHOR_NICKNAME.name();
            searchUser = itUtil.createUser();
            searchUser.setNickname(userNickname);
            itUtil.updateUser(searchUser);

            final User anotherUser = createAnotherUser();
            final Post searchPost = itUtil.createPost(board, searchUser.getId(), Boolean.FALSE);
            final Post orderPost = itUtil.createPost(board, anotherUser.getId(), Boolean.FALSE);

            automatedPushCriteria = AutomatedPushCriteria.COMMENT;
            automatedPushContentType = AutomatedPushContentType.POST;
            expectedPush1 = itUtil.createPush(someAlphanumericString(10), PushTargetType.AUTOMATED_AUTHOR, somePositiveLong());
            expectedPush2 = itUtil.createPush(someAlphanumericString(15), PushTargetType.AUTOMATED_AUTHOR, somePositiveLong());
            itUtil.createAutomatedAuthorPush(
                searchPost.getId(), expectedPush1.getId(), 10, automatedPushCriteria, automatedPushContentType
            );
            itUtil.createAutomatedAuthorPush(
                searchPost.getId(), expectedPush2.getId(), 20, automatedPushCriteria, automatedPushContentType
            );
            itUtil.createAutomatedAuthorPush(
                orderPost.getId(),
                itUtil.createPush(someAlphanumericString(20), PushTargetType.AUTOMATED_AUTHOR, somePositiveLong()).getId(),
                20, automatedPushCriteria, automatedPushContentType
            );
            itUtil.createAutomatedAuthorPush(
                orderPost.getId(),
                itUtil.createPush(someAlphanumericString(25), PushTargetType.AUTOMATED_AUTHOR, somePositiveLong()).getId(),
                20, automatedPushCriteria, automatedPushContentType
            );
        }

        private User createAnotherUser() {
            final User orderUser = itUtil.createUser();
            orderUser.setNickname(someAlphanumericString(20));
            itUtil.updateUser(orderUser);
            return orderUser;
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            GetPushDataResponse result = callApi();

            final List<PushDetailsResponse> pushDetailsResponses = result.getData();

            assertThat(pushDetailsResponses.size(), is(2));
            assertPushListItemResponse(pushDetailsResponses.get(0), expectedPush2);
            assertPushListItemResponse(pushDetailsResponses.get(1), expectedPush1);
            assertThat(pushDetailsResponses.get(0).getUser().getNickname(), is(userNickname));
            assertThat(pushDetailsResponses.get(1).getUser().getNickname(), is(userNickname));
        }
    }

    @Nested
    class WhenSearchTitle {
        private String title;

        @BeforeEach
        void setUp() {
            searchKeyword = someAlphanumericString(10);
            title = someAlphanumericString(5) + searchKeyword + someAlphanumericString(5);

            searchType = PushSearchType.PUSH_TITLE.name();

            final User authorUser = itUtil.createUser();
            final Post post = itUtil.createPost(board, authorUser.getId(), Boolean.FALSE);

            automatedPushCriteria = AutomatedPushCriteria.COMMENT;
            automatedPushContentType = AutomatedPushContentType.POST;

            final PushTargetType pushTargetType = PushTargetType.AUTOMATED_AUTHOR;

            expectedPush1 = itUtil.createPush(title, someAlphanumericString(10), pushTargetType, somePositiveLong());

            itUtil.createAutomatedAuthorPush(
                post.getId(), expectedPush1.getId(), 10, automatedPushCriteria, automatedPushContentType
            );
            itUtil.createAutomatedAuthorPush(
                post.getId(),
                itUtil.createPush(
                    someAlphanumericString(10), someAlphanumericString(20), pushTargetType, somePositiveLong()).getId(),
                20, automatedPushCriteria, automatedPushContentType
            );
            itUtil.createAutomatedAuthorPush(
                post.getId(),
                itUtil.createPush(
                    someAlphanumericString(10), someAlphanumericString(20), pushTargetType, somePositiveLong()).getId(),
                20, automatedPushCriteria, automatedPushContentType
            );
            itUtil.createAutomatedAuthorPush(
                post.getId(),
                itUtil.createPush(
                    someAlphanumericString(10), someAlphanumericString(25), pushTargetType, somePositiveLong()).getId(),
                20, automatedPushCriteria, automatedPushContentType
            );
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            assertResponse(callApi());
        }

        private void assertResponse(GetPushDataResponse result) {

            final List<PushDetailsResponse> pushDetailsResponses = result.getData();

            assertThat(pushDetailsResponses.size(), is(1));
            assertPushListItemResponse(pushDetailsResponses.get(0), expectedPush1);
            assertThat(pushDetailsResponses.get(0).getTitle(), is(title));
        }
    }
}
