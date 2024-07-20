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
import ag.act.model.CommentResponse;
import ag.act.model.Paging;
import ag.act.model.Status;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;
import java.util.Map;

import static ag.act.TestUtil.someBoardCategory;
import static ag.act.TestUtil.toMultiValueMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class GuestGetCommentsApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_URL = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}/comments";

    @Autowired
    private GlobalBoardManager globalBoardManager;

    private Map<String, Object> params;

    private Stock globalStock;
    private Stock stock;
    private Post post;
    private User writer;
    private BoardGroup boardGroup;
    private Comment comment1;
    private Comment comment2;
    private String appVersion;

    @BeforeEach
    void setUp() {
        itUtil.init();
        dbCleaner.clean();
        writer = itUtil.createUser();
        globalStock = itUtil.findStock(globalBoardManager.getStockCode());
    }

    @AfterEach
    void tearDown() {
        dbCleaner.clean();
    }

    @Nested
    class WhenGlobalBoardPostComments {

        @BeforeEach
        void setUp() {
            boardGroup = BoardGroup.GLOBALBOARD;
            Board board = getBoard(globalStock, someBoardCategory(boardGroup));
            post = itUtil.createPost(board, writer.getId());

            comment1 = itUtil.createComment(post.getId(), writer, CommentType.POST, Status.ACTIVE);
            comment2 = itUtil.createComment(post.getId(), writer, CommentType.POST, Status.ACTIVE);

            params = Map.of(
                "page", PAGE_1,
                "size", SIZE,
                "sort", CREATED_AT_DESC
            );
            appVersion = X_APP_VERSION_WEB;
        }

        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult mvcResult = callApi(status().isOk(), globalStock.getCode());

            CommentPagingResponse response = itUtil.getResult(mvcResult, CommentPagingResponse.class);
            assertResponse(response);
        }
    }

    @Nested
    class WhenGlobalEvent {

        @BeforeEach
        void setUp() {
            boardGroup = BoardGroup.GLOBALEVENT;
            Board board = getBoard(globalStock, someBoardCategory(boardGroup));
            post = itUtil.createPost(board, writer.getId());

            comment1 = itUtil.createComment(post.getId(), writer, CommentType.POST, Status.ACTIVE);
            comment2 = itUtil.createComment(post.getId(), writer, CommentType.POST, Status.ACTIVE);

            params = Map.of(
                "page", PAGE_1,
                "size", SIZE,
                "sort", CREATED_AT_DESC
            );
            appVersion = X_APP_VERSION_WEB;
        }

        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult mvcResult = callApi(status().isOk(), globalStock.getCode());

            CommentPagingResponse response = itUtil.getResult(mvcResult, CommentPagingResponse.class);
            assertResponse(response);
        }
    }

    @Nested
    class WhenGlobalCommunity {

        @BeforeEach
        void setUp() {
            boardGroup = BoardGroup.GLOBALCOMMUNITY;
            Board board = getBoard(globalStock, someBoardCategory(boardGroup));
            post = itUtil.createPost(board, writer.getId());

            comment1 = itUtil.createComment(post.getId(), writer, CommentType.POST, Status.ACTIVE);
            comment2 = itUtil.createComment(post.getId(), writer, CommentType.POST, Status.ACTIVE);

            params = Map.of(
                "page", PAGE_1,
                "size", SIZE,
                "sort", CREATED_AT_DESC
            );
            appVersion = X_APP_VERSION_WEB;
        }

        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult mvcResult = callApi(status().isOk(), globalStock.getCode());

            CommentPagingResponse response = itUtil.getResult(mvcResult, CommentPagingResponse.class);
            assertResponse(response);
        }
    }

    @Nested
    class WhenStockDebateCategory {

        @BeforeEach
        void setUp() {
            stock = itUtil.createStock();
            boardGroup = BoardGroup.DEBATE;
            Board board = getBoard(stock, BoardCategory.DEBATE);
            post = itUtil.createPost(board, writer.getId());
            comment1 = itUtil.createComment(post.getId(), writer, CommentType.POST, Status.ACTIVE);
            comment2 = itUtil.createComment(post.getId(), writer, CommentType.POST, Status.ACTIVE);

            params = Map.of(
                "page", PAGE_1,
                "size", SIZE,
                "sort", CREATED_AT_DESC
            );
            appVersion = X_APP_VERSION_WEB;
        }

        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult mvcResult = callApi(status().isOk(), stock.getCode());

            CommentPagingResponse response = itUtil.getResult(mvcResult, CommentPagingResponse.class);
            assertResponse(response);
        }
    }

    @Nested
    class WhenAnalysisAndSolidarityLeaderLettersCategory {

        @BeforeEach
        void setUp() {
            stock = itUtil.createStock();
            boardGroup = BoardGroup.ANALYSIS;
            Board board = getBoard(stock, BoardCategory.SOLIDARITY_LEADER_LETTERS);
            post = itUtil.createPost(board, writer.getId());
            comment1 = itUtil.createComment(post.getId(), writer, CommentType.POST, Status.ACTIVE);
            comment2 = itUtil.createComment(post.getId(), writer, CommentType.POST, Status.ACTIVE);

            params = Map.of(
                "page", PAGE_1,
                "size", SIZE,
                "sort", CREATED_AT_DESC
            );
            appVersion = X_APP_VERSION_WEB;
        }

        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult mvcResult = callApi(status().isOk(), stock.getCode());

            CommentPagingResponse response = itUtil.getResult(mvcResult, CommentPagingResponse.class);
            assertResponse(response);
        }
    }

    private void assertResponse(CommentPagingResponse response) {
        List<CommentResponse> data = response.getData();

        CommentResponse commentResponse1 = data.get(1);
        CommentResponse commentResponse2 = data.get(0);

        assertPaging(response.getPaging(), 2L);
        assertCommentResponse(commentResponse1, comment1);
        assertCommentResponse(commentResponse2, comment2);
    }

    private void assertPaging(Paging paging, long totalElements) {
        assertThat(paging.getPage(), is(PAGE_1));
        assertThat(paging.getTotalElements(), is(totalElements));
        assertThat(paging.getTotalPages(), is((int) Math.ceil(totalElements / (SIZE * 1.0))));
        assertThat(paging.getSize(), is(SIZE));
        assertThat(paging.getSorts().size(), is(1));
        assertThat(paging.getSorts().get(0), is(CREATED_AT_DESC));
    }

    private void assertCommentResponse(CommentResponse response, Comment comment) {
        assertThat(response.getId(), is(comment.getId()));
        assertThat(response.getUserId(), is(writer.getId()));
        assertThat(response.getContent(), is(comment.getContent()));
        assertThat(response.getLiked(), is(false));
        assertThat(response.getLikeCount(), is(0L));
        assertThat(response.getReplyCommentCount(), is(comment.getReplyCommentCount()));
        assertThat(response.getReplyComments(), is(notNullValue()));
        assertThat(response.getDeleted(), is(false));
        assertThat(response.getReported(), is(false));
        assertThat(response.getCreatedAt(), notNullValue());
        assertThat(response.getUpdatedAt(), notNullValue());
        assertThat(response.getEditedAt(), notNullValue());
        assertThat(response.getUserProfile(), notNullValue());
    }

    private MvcResult callApi(ResultMatcher resultMatcher, String stockCode) throws Exception {
        return mockMvc.perform(
                get(TARGET_URL, stockCode, boardGroup.name(), post.getId())
                    .params(toMultiValueMap(params))
                    .contentType(APPLICATION_JSON)
                    .accept(APPLICATION_JSON)
                    .header(X_APP_VERSION, appVersion)
            ).andExpect(resultMatcher)
            .andReturn();
    }

    private Board getBoard(Stock stock, BoardCategory boardCategory) {
        return itUtil.findBoard(stock.getCode(), boardCategory)
            .orElseGet(() -> itUtil.createBoard(stock, boardGroup, boardCategory));
    }
}
