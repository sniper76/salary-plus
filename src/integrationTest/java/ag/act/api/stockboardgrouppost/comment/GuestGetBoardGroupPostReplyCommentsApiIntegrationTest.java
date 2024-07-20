package ag.act.api.stockboardgrouppost.comment;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.core.configuration.GlobalBoardManager;
import ag.act.entity.Board;
import ag.act.entity.Comment;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.CommentType;
import ag.act.model.CommentPagingResponse;
import ag.act.model.Paging;
import ag.act.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;
import java.util.Map;

import static ag.act.TestUtil.someBoardCategory;
import static ag.act.TestUtil.someBoardCategoryExcluding;
import static ag.act.TestUtil.toMultiValueMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GuestGetBoardGroupPostReplyCommentsApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String SORT = "createdAt:ASC";
    private static final String TARGET_URL = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}/comments/{commentId}/replies";

    @Autowired
    private GlobalBoardManager globalBoardManager;

    private Map<String, Object> params;
    private Stock globalStock;
    private Post post;
    private BoardGroup boardGroup;
    private User writer;
    private Comment comment;
    private Comment reply1;
    private Comment reply2;
    private String appVersion;

    @BeforeEach
    void setUP() {
        itUtil.init();

        globalStock = itUtil.findStock(globalBoardManager.getStockCode());
        writer = itUtil.createUser();

        params = Map.of(
            "page", PAGE_1,
            "size", SIZE,
            "sorts", SORT
        );
    }

    @Nested
    @DisplayName("GlobalBoard 일때")
    class WhenGlobalBoardGroup {

        @BeforeEach
        void setUp() {
            boardGroup = BoardGroup.GLOBALBOARD;
            Board board = getBoard(globalStock, someBoardCategory(boardGroup));
            post = itUtil.createPost(board, writer.getId());
            comment = itUtil.createComment(post.getId(), writer, CommentType.POST, Status.ACTIVE);
            createReplies();

            appVersion = X_APP_VERSION_WEB;
        }

        @Test
        @DisplayName("게스트는 답글 목록을 조회할 수 있다.")
        void shouldReturnSuccess() throws Exception {
            MvcResult mvcResult = callApi(status().isOk(), globalStock.getCode());

            CommentPagingResponse response = itUtil.getResult(mvcResult, CommentPagingResponse.class);
            assertPaging(response.getPaging(), 2);
            assertResponse(response);
        }
    }

    @Nested
    @DisplayName("GlobalEvent 일때")
    class WhenGlobalEventGroup {

        @BeforeEach
        void setUp() {
            boardGroup = BoardGroup.GLOBALEVENT;
            Board board = getBoard(globalStock, someBoardCategory(boardGroup));
            post = itUtil.createPost(board, writer.getId());
            comment = itUtil.createComment(post.getId(), writer, CommentType.POST, Status.ACTIVE);
            createReplies();

            appVersion = X_APP_VERSION_WEB;
        }

        @Test
        @DisplayName("게스트는 답글 목록을 조회할 수 있다.")
        void shouldReturnSuccess() throws Exception {
            MvcResult mvcResult = callApi(status().isOk(), globalStock.getCode());

            CommentPagingResponse response = itUtil.getResult(mvcResult, CommentPagingResponse.class);
            assertPaging(response.getPaging(), 2);
            assertResponse(response);
        }
    }

    @Nested
    @DisplayName("GlobalCommunity 일때")
    class WhenGlobalCommunity {

        @BeforeEach
        void setUp() {
            boardGroup = BoardGroup.GLOBALCOMMUNITY;
            Board board = getBoard(globalStock, someBoardCategory(boardGroup));
            post = itUtil.createPost(board, writer.getId());
            comment = itUtil.createComment(post.getId(), writer, CommentType.POST, Status.ACTIVE);
            createReplies();

            appVersion = X_APP_VERSION_WEB;
        }

        @Test
        @DisplayName("게스트는 답글 목록을 조회할 수 있다.")
        void shouldReturnSuccess() throws Exception {
            MvcResult mvcResult = callApi(status().isOk(), globalStock.getCode());

            CommentPagingResponse response = itUtil.getResult(mvcResult, CommentPagingResponse.class);
            assertPaging(response.getPaging(), 2);
            assertResponse(response);
        }
    }

    @Nested
    @DisplayName("종목의 게시판 중에서")
    class WhenStockBoardGroup {

        private Stock stock;

        @BeforeEach
        void setUp() {
            stock = itUtil.createStock();
        }

        @Nested
        @DisplayName("Debate 게시판일때")
        class WhenDebate {

            @BeforeEach
            void setUp() {
                boardGroup = BoardGroup.DEBATE;
                Board board = getBoard(stock, someBoardCategory(BoardGroup.DEBATE));
                post = itUtil.createPost(board, writer.getId());
                comment = itUtil.createComment(post.getId(), writer, CommentType.POST, Status.ACTIVE);
                createReplies();

                appVersion = X_APP_VERSION_WEB;
            }

            @Test
            @DisplayName("게스트는 답글 목록을 조회할 수 있다.")
            void shouldReturnSuccess() throws Exception {
                MvcResult mvcResult = callApi(status().isOk(), stock.getCode());

                CommentPagingResponse response = itUtil.getResult(mvcResult, CommentPagingResponse.class);
                assertPaging(response.getPaging(), 2);
                assertResponse(response);
            }
        }

        @Nested
        @DisplayName("Analysis 게시판 일때")
        class WhenAnalysisGroup {

            private Board board;

            @BeforeEach
            void setUp() {
                boardGroup = BoardGroup.ANALYSIS;
            }

            @Nested
            @DisplayName("주주연대 공지 카테고리일때")
            class WhenSolidarityLeaderLettersCategory {

                @BeforeEach
                void setUp() {
                    Board board = getBoard(stock, BoardCategory.SOLIDARITY_LEADER_LETTERS);
                    post = itUtil.createPost(board, writer.getId());
                    comment = itUtil.createComment(post.getId(), writer, CommentType.POST, Status.ACTIVE);
                    createReplies();

                    appVersion = X_APP_VERSION_WEB;
                }

                @Test
                @DisplayName("게스트는 답글 목록을 조회할 수 있다.")
                void shouldReturnSuccess() throws Exception {
                    MvcResult mvcResult = callApi(status().isOk(), stock.getCode());

                    CommentPagingResponse response = itUtil.getResult(mvcResult, CommentPagingResponse.class);
                    assertPaging(response.getPaging(), 2);
                    assertResponse(response);
                }
            }

            @Nested
            @DisplayName("그 외의 카테고리일때")
            class WhenOtherCategory {

                @BeforeEach
                void setUp() {
                    BoardCategory boardCategory = someBoardCategoryExcluding(boardGroup, BoardCategory.SOLIDARITY_LEADER_LETTERS);
                    Board board = getBoard(stock, boardCategory);
                    post = itUtil.createPost(board, writer.getId());
                    comment = itUtil.createComment(post.getId(), writer, CommentType.POST, Status.ACTIVE);
                    createReplies();

                    appVersion = X_APP_VERSION_WEB;
                }

                @Test
                @DisplayName("게스트는 답글 목록을 조회할 수 없다.")
                void shouldReturnUnAuthorized() throws Exception {
                    MvcResult mvcResult = callApi(status().isUnauthorized(), stock.getCode());

                    itUtil.assertErrorResponse(mvcResult, UNAUTHORIZED_STATUS, "인가되지 않은 접근입니다.");
                }
            }
        }
    }

    @Nested
    @DisplayName("Action 게시판일때")
    class WhenActionBoardGroup {

        private Stock stock;

        @BeforeEach
        void setUp() {
            boardGroup = BoardGroup.ACTION;
            BoardCategory boardCategory = someBoardCategory(boardGroup);
            stock = itUtil.createStock();
            Board board = getBoard(stock, boardCategory);
            post = itUtil.createPost(board, writer.getId());
            comment = itUtil.createComment(post.getId(), writer, CommentType.POST, Status.ACTIVE);
            createReplies();

            appVersion = X_APP_VERSION_WEB;
        }

        @Test
        @DisplayName("게스트는 댓글의 답글들을 조회할 수 없다.")
        void shouldReturnUnAuthorized() throws Exception {
            MvcResult mvcResult = callApi(status().isUnauthorized(), stock.getCode());

            itUtil.assertErrorResponse(mvcResult, UNAUTHORIZED_STATUS, "인가되지 않은 접근입니다.");
        }
    }

    private Board getBoard(Stock stock, BoardCategory boardCategory) {
        return itUtil.findBoard(stock.getCode(), boardCategory)
            .orElseGet(() -> itUtil.createBoard(stock, boardCategory.getBoardGroup(), boardCategory));
    }

    private void createReplies() {
        reply1 = itUtil.createComment(post.getId(), writer.getId(), comment.getId(), CommentType.REPLY_COMMENT, Status.ACTIVE);
        reply2 = itUtil.createComment(post.getId(), writer.getId(), comment.getId(), CommentType.REPLY_COMMENT, Status.ACTIVE);
    }

    private void assertResponse(CommentPagingResponse response) {
        List<ag.act.model.CommentResponse> data = response.getData();

        ag.act.model.CommentResponse commentResponse1 = data.get(0);
        ag.act.model.CommentResponse commentResponse2 = data.get(1);

        assertPaging(response.getPaging(), 2L);
        assertCommentResponse(commentResponse1, reply1);
        assertCommentResponse(commentResponse2, reply2);
    }

    private void assertPaging(Paging paging, long totalElements) {
        assertThat(paging.getPage(), is(PAGE_1));
        assertThat(paging.getTotalElements(), is(totalElements));
        assertThat(paging.getTotalPages(), is((int) Math.ceil(totalElements / (SIZE * 1.0))));
        assertThat(paging.getSize(), is(SIZE));
        assertThat(paging.getSorts().size(), is(1));
        assertThat(paging.getSorts().get(0), is(SORT));
    }

    private void assertCommentResponse(ag.act.model.CommentResponse response, Comment comment) {
        assertThat(response.getId(), is(comment.getId()));
        assertThat(response.getUserId(), is(writer.getId()));
        assertThat(response.getContent(), is(comment.getContent()));
        assertThat(response.getLiked(), is(false));
        assertThat(response.getLikeCount(), is(0L));
        assertThat(response.getReplyCommentCount(), is(nullValue()));
        assertThat(response.getReplyComments(), is(nullValue()));
        assertThat(response.getDeleted(), is(false));
        assertThat(response.getReported(), is(false));
        assertThat(response.getCreatedAt(), notNullValue());
        assertThat(response.getUpdatedAt(), notNullValue());
        assertThat(response.getEditedAt(), notNullValue());
        assertThat(response.getUserProfile(), notNullValue());
    }

    private MvcResult callApi(ResultMatcher resultMatcher, String stockCode) throws Exception {
        return mockMvc.perform(
                get(TARGET_URL, stockCode, boardGroup.name(), post.getId(), comment.getId())
                    .params(toMultiValueMap(params))
                    .contentType(APPLICATION_JSON)
                    .accept(APPLICATION_JSON)
                    .header(X_APP_VERSION, appVersion)
            ).andExpect(resultMatcher)
            .andReturn();
    }
}
