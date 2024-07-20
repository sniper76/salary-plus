package ag.act.api.stockboardgrouppost;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.model.PostDetailsDataResponse;
import ag.act.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDateTime;

import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GetDeletedBoardGroupPostDetailsApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}";

    private String jwt;
    private Post post;
    private Board board;
    private Stock stock;
    private User author;
    private String stockCode = someStockCode();

    @BeforeEach
    void setUp() {
        itUtil.init();
        author = itUtil.createUser();
        User user = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());

        stock = itUtil.createStock(stockCode);
        board = itUtil.createBoard(stock);
        itUtil.createUserHoldingStock(stockCode, author);
        itUtil.createUserHoldingStock(stockCode, user);
    }

    @Nested
    class NormalPost {

        @BeforeEach
        void setUp() {
            post = deletePost(itUtil.createPost(board, author.getId()), Status.DELETED_BY_USER);
        }

        @Test
        void shouldReturnDeletedPost() throws Exception {
            MvcResult mvcResult = callApi(status().isOk(), post.getId());

            PostDetailsDataResponse response = toResponse(mvcResult);
            assertResponse(response.getData(), post);
        }
    }

    @Nested
    class PostWithDeletedStatus {

        private Post post;

        @BeforeEach
        void setUp() {
            post = deletePost(itUtil.createPost(board, author.getId()), Status.DELETED);
        }

        @Test
        void shouldReturn404Response() throws Exception {
            callApi(status().isNotFound(), post.getId());
        }
    }

    @Nested
    class WithPoll {

        private Post postWithPoll;

        @BeforeEach
        void setUp() {
            post = itUtil.createPost(board, author.getId());
            postWithPoll = deletePost(itUtil.createPoll(post), Status.DELETED_BY_USER);
        }

        @Test
        void shouldNotReturnPollData() throws Exception {
            MvcResult mvcResult = callApi(status().isOk(), postWithPoll.getId());

            PostDetailsDataResponse response = toResponse(mvcResult);
            assertResponse(response.getData(), postWithPoll);
        }
    }

    @Nested
    class DeletedPostByAdmin {

        private Post post;

        @BeforeEach
        void setUp() {
            post = deletePost(itUtil.createPost(board, author.getId()), Status.DELETED_BY_ADMIN);
        }

        @Test
        void shouldReturn404Response() throws Exception {
            callApi(status().isNotFound(), post.getId());
        }
    }

    private void assertResponse(ag.act.model.PostDetailsResponse postDetailsResponse, Post post) {
        assertThat(postDetailsResponse.getId(), is(post.getId()));
        assertThat(postDetailsResponse.getStock().getCode(), is(stockCode));
        assertThat(postDetailsResponse.getBoardId(), is(board.getId()));
        assertThat(postDetailsResponse.getTitle(), is("삭제된 게시글입니다."));
        assertThat(postDetailsResponse.getContent(), is("삭제된 게시글입니다."));
        assertThat(postDetailsResponse.getStatus(), is(Status.DELETED_BY_USER));
        assertThat(postDetailsResponse.getReported(), is(false));
        assertThat(postDetailsResponse.getDeleted(), is(true));
        assertThat(postDetailsResponse.getIsAuthorAdmin(), is(false));
        assertThat(postDetailsResponse.getPoll(), nullValue());
        assertThat(postDetailsResponse.getPolls(), nullValue());
        assertThat(postDetailsResponse.getDigitalDocument(), nullValue());
        assertThat(postDetailsResponse.getDigitalProxy(), nullValue());
        assertThat(postDetailsResponse.getIsActive(), is(false));
    }

    private MvcResult callApi(ResultMatcher matcher, Long postId) throws Exception {
        return mockMvc
            .perform(
                get(TARGET_API, stockCode, board.getGroup().name(), postId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(matcher)
            .andReturn();
    }

    private ag.act.model.PostDetailsDataResponse toResponse(MvcResult mvcResult) throws Exception {
        return objectMapperUtil.toResponse(
            mvcResult.getResponse().getContentAsString(),
            ag.act.model.PostDetailsDataResponse.class);
    }

    private Post deletePost(Post post, Status status) {
        post.setStatus(status);
        post.setDeletedAt(LocalDateTime.now());
        return itUtil.updatePost(post);
    }
}
