package ag.act.api.stockboardgrouppost;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.PostUserView;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.model.PostDataResponse;
import ag.act.model.PostResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.EnumSet;

import static ag.act.TestUtil.assertTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomThings.someThing;

class GetBoardGroupPostDetailApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/stocks/{stockCode}/board-groups/{boardGroup}/posts/{postId}";

    private String jwt;
    private Stock stock;
    private Board board;
    private Post post;
    private User user;

    @BeforeEach
    void setUp() {
        itUtil.init();
        user = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());
        stock = itUtil.createStock();
        board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.SURVEYS);
        itUtil.createSolidarity(stock.getCode());
        itUtil.createUserHoldingStock(stock.getCode(), user);

        post = itUtil.createPost(board, user.getId());
    }

    private MvcResult callApi() throws Exception {
        return mockMvc
            .perform(
                get(TARGET_API, stock.getCode(), board.getGroup(), post.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, "Bearer " + jwt)
            )
            .andExpect(status().isOk())
            .andReturn();
    }

    private PostDataResponse getResponse(MvcResult mvcResult) throws Exception {
        return itUtil.getResult(mvcResult, PostDataResponse.class);
    }

    @Nested
    class Success {
        private LocalDateTime now;

        @Nested
        class AndIsNewTrue {
            @BeforeEach
            void setUp() {
                now = LocalDateTime.now();
                post.setCreatedAt(now);
                post = itUtil.updatePost(post);
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                final ag.act.model.PostDataResponse result = getResponse(callApi());
                assertResponse(result);
                itUtil.assertPostTitleAndContent(post, result.getData());
                assertThat(result.getData().getIsNew(), is(true));
            }
        }

        @Nested
        class AndIsNewFalse {
            @BeforeEach
            void setUp() {
                now = LocalDateTime.now();
                post.setCreatedAt(now.minusDays(3));
                post = itUtil.updatePost(post);
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                final ag.act.model.PostDataResponse result = getResponse(callApi());
                assertResponse(result);
                itUtil.assertPostTitleAndContent(post, result.getData());
                assertThat(result.getData().getIsNew(), is(false));
            }
        }

        private void assertResponse(ag.act.model.PostDataResponse result) {
            PostResponse postResponse = result.getData();

            final Post post = itUtil.findPostNoneNull(postResponse.getId());

            assertThat(postResponse.getUserProfile(), notNullValue());
            assertThat(postResponse.getBoardCategory().getName(), is(board.getCategory().name()));
            assertThat(postResponse.getBoardGroup(), is(board.getGroup().name()));
            assertThat(postResponse.getPoll(), is(nullValue()));
            assertThat(postResponse.getDigitalProxy(), is(nullValue()));
            assertThat(postResponse.getStock().getCode(), is(stock.getCode()));
            assertThat(postResponse.getId(), is(post.getId()));
            assertThat(postResponse.getTitle(), is(post.getTitle()));
            assertThat(postResponse.getBoardId(), is(board.getId()));
            assertThat(postResponse.getContent(), is(post.getContent()));
            assertThat(postResponse.getStatus(), is(post.getStatus()));
            assertThat(postResponse.getUserId(), is(user.getId()));
            assertThat(postResponse.getLikeCount(), is(post.getLikeCount()));
            assertThat(postResponse.getCommentCount(), is(post.getCommentCount()));
            assertThat(postResponse.getIsActive(), is(true));
            assertThat(postResponse.getLiked(), is(false));
            assertThat(postResponse.getViewCount(), is(1L));
            assertThat(postResponse.getIsAuthorAdmin(), is(false));
            assertTime(postResponse.getCreatedAt(), post.getCreatedAt());
            assertTime(postResponse.getUpdatedAt(), post.getUpdatedAt());
            assertTime(postResponse.getDeletedAt(), post.getDeletedAt());
            assertTime(postResponse.getEditedAt(), post.getEditedAt());
            assertThat(postResponse.getPostImageList(), is(empty()));
        }
    }

    @Nested
    class WhenStockCodeIsWrong {

        private Stock wrongStock;

        @BeforeEach
        void setUp() {
            wrongStock = itUtil.createStock();
            itUtil.createUserHoldingStock(wrongStock.getCode(), user);
        }

        @DisplayName("Should return 400 response code when call " + TARGET_API)
        @Test
        void shouldReturnBadRequestException() throws Exception {
            final MvcResult response = callApiForFail(wrongStock.getCode(), board.getGroup());
            itUtil.assertErrorResponse(response, 400, "게시글과 %s 종목이 일치하지 않습니다.".formatted(wrongStock.getCode()));
        }
    }

    @Nested
    class WhenBoardGroupIsWrong {

        private BoardGroup wrongBoardGroup;

        @BeforeEach
        void setUp() {

            final EnumSet<BoardGroup> boardGroups = EnumSet.copyOf(BoardGroup.getStockBoardGroups());
            boardGroups.remove(board.getGroup());
            wrongBoardGroup = someThing(boardGroups.toArray(new BoardGroup[0]));
        }

        @DisplayName("Should return 400 response code when call " + TARGET_API)
        @Test
        void shouldReturnBadRequestException() throws Exception {
            final MvcResult response = callApiForFail(stock.getCode(), wrongBoardGroup);
            itUtil.assertErrorResponse(response, 400, "%s에서 해당 게시글을 찾을 수 없습니다.".formatted(wrongBoardGroup));
        }
    }

    @Nested
    class WhenUserViewPost {
        @Nested
        class AndAlreadyViewPostBefore {
            Long viewCountBeforeCallingApi;

            @BeforeEach
            void setUp() {
                PostUserView postUserView = itUtil.createPostUserView(user, post);
                viewCountBeforeCallingApi = postUserView.getCount();
            }

            @DisplayName("Should return 200 response and view count when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                final ag.act.model.PostDataResponse result = getResponse(callApi());
                assertThat(result.getData().getViewCount(), is(viewCountBeforeCallingApi + 1L));
            }
        }

        @Nested
        class AndAlreadyAnotherUserViewedSamePost {
            Long viewCountBeforeCallingApi;

            @BeforeEach
            void setUp() {
                final User anotherUser = itUtil.createUser();
                PostUserView postUserView = itUtil.createPostUserView(anotherUser, post);
                viewCountBeforeCallingApi = postUserView.getCount();
            }

            @DisplayName("Should return 200 response code and view count when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                final ag.act.model.PostDataResponse result = getResponse(callApi());
                assertThat(result.getData().getViewCount(), is(viewCountBeforeCallingApi + 1L));
            }
        }

        @Nested
        class WhenAnotherPostsExists {
            @BeforeEach
            void setUp() {
                final User anotherUser = itUtil.createUser();
                final Post anotherPost = itUtil.createPost(board, anotherUser.getId());
                itUtil.createPostUserView(anotherUser, anotherPost);
            }

            @DisplayName("Should return 200 response when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                final ag.act.model.PostDataResponse result = getResponse(callApi());
                assertThat(result.getData().getViewCount(), is(1L));
            }
        }
    }

    @Nested
    class WhenUserLikedPost {
        @BeforeEach
        void setUp() {
            itUtil.createPostUserLike(user, post);
        }

        @Test
        void shouldReturnSuccessWithLiked() throws Exception {
            final ag.act.model.PostDataResponse result = getResponse(callApi());
            assertThat(result.getData().getLiked(), is(true));
        }
    }

    private MvcResult callApiForFail(String stockCode, BoardGroup boardGroup) throws Exception {
        return mockMvc
            .perform(
                get(TARGET_API, stockCode, boardGroup, post.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(status().isBadRequest())
            .andReturn();
    }
}
