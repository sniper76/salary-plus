package ag.act.api.stockboardgrouppost.comment;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Comment;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.CommentType;
import ag.act.enums.ReportType;
import ag.act.model.CommentPagingResponse;
import ag.act.model.CommentResponse;
import ag.act.model.Paging;
import ag.act.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetBoardGroupPostCommentsApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}/comments";

    private String jwt;
    private Stock stock;
    private Board board;
    private Post post1;
    private User user;
    private Comment firstComment;
    private Comment secondComment;

    @BeforeEach
    void setUp() {
        itUtil.init();
        user = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());
        stock = itUtil.createStock();
        board = itUtil.createBoard(stock);
        itUtil.createSolidarity(stock.getCode());
        itUtil.createUserHoldingStock(stock.getCode(), user);

        post1 = itUtil.createPost(board, user.getId());
        itUtil.createPost(board, user.getId());
        firstComment = itUtil.createComment(post1.getId(), user, CommentType.POST, Status.ACTIVE);
        secondComment = itUtil.createComment(post1.getId(), user, CommentType.POST, Status.ACTIVE);
    }

    private CommentPagingResponse callApiAndGetResult() throws Exception {
        MvcResult response = mockMvc
            .perform(
                get(TARGET_API, stock.getCode(), board.getGroup(), post1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(status().isOk())
            .andReturn();

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            CommentPagingResponse.class
        );
    }

    @Nested
    class WhenGetBoardGroupPostComments {

        private Comment firstReplyComment;
        private Comment secondReplyComment;
        private long replyCommentCount;

        @BeforeEach
        void setUp() {
            firstReplyComment = itUtil.createComment(
                post1.getId(),
                user.getId(),
                firstComment.getId(),
                CommentType.REPLY_COMMENT,
                Status.ACTIVE
            );
            secondReplyComment = itUtil.createComment(
                post1.getId(),
                user.getId(),
                firstComment.getId(),
                CommentType.REPLY_COMMENT,
                Status.ACTIVE
            );
            replyCommentCount = 2L;
            firstComment.setReplyCommentCount(replyCommentCount);
            itUtil.updateComment(firstComment);
        }

        @Test
        void shouldReturnSuccess() throws Exception {
            final CommentPagingResponse result = callApiAndGetResult();

            assertResponse(result);
        }

        private void assertResponse(CommentPagingResponse result) {
            final Paging paging = result.getPaging();
            final List<ag.act.model.CommentResponse> commentResponses = result.getData();

            assertPaging(paging);
            assertComments(commentResponses);
        }

        private void assertComments(List<ag.act.model.CommentResponse> commentResponses) {
            assertThat(commentResponses.size(), is(2));

            final CommentResponse commentResponse1 = commentResponses.get(0);
            assertCommentResponse(commentResponse1, secondComment);

            final CommentResponse commentResponse2 = commentResponses.get(1);
            assertCommentResponse(commentResponse2, firstComment);
            assertReplyCommentResponse(commentResponse2);
        }

        private void assertCommentResponse(CommentResponse response, Comment comment) {
            assertThat(response.getId(), is(comment.getId()));
            assertThat(response.getUserId(), is(user.getId()));
            assertThat(response.getContent(), is(comment.getContent()));
            assertThat(response.getLiked(), is(false));
            assertThat(response.getLikeCount(), is(0L));
            assertThat(response.getReplyCommentCount(),
                is(comment.getType() == CommentType.REPLY_COMMENT ? null : comment.getReplyCommentCount()));
            assertThat(response.getReplyComments(), is(comment.getType() == CommentType.REPLY_COMMENT ? nullValue() : notNullValue()));
            assertThat(response.getDeleted(), is(false));
            assertThat(response.getReported(), is(false));
            assertThat(response.getCreatedAt(), notNullValue());
            assertThat(response.getUpdatedAt(), notNullValue());
            assertThat(response.getEditedAt(), notNullValue());
            assertThat(response.getUserProfile(), notNullValue());
        }

        private void assertReplyCommentResponse(CommentResponse commentResponse) {
            assertThat(commentResponse.getReplyCommentCount(), is(replyCommentCount));

            List<CommentResponse> replyComments = commentResponse.getReplyComments();
            assertCommentResponse(replyComments.get(0), firstReplyComment);
            assertCommentResponse(replyComments.get(1), secondReplyComment);
        }

        private void assertPaging(Paging paging) {
            assertThat(paging.getPage(), is(1));
            assertThat(paging.getTotalPages(), is(1));
            assertThat(paging.getTotalElements(), is(2L));
            assertThat(paging.getSorts().size(), is(1));
            assertThat(paging.getSorts().get(0), is(CREATED_AT_DESC));
        }

    }

    @Nested
    class WhenPostsHaveSomeReportedByMe {

        @BeforeEach
        void setUp() {
            itUtil.createReport(secondComment.getId(), user, ReportType.COMMENT);
            itUtil.createComment(post1.getId(), user.getId(), secondComment.getId(), CommentType.REPLY_COMMENT, Status.ACTIVE);
            secondComment.setReplyCommentCount(1L);
            itUtil.updateComment(secondComment);
        }

        @Test
        void shouldReturnCommentsWithSomeReportedByMe() throws Exception {
            final ag.act.model.CommentPagingResponse result = callApiAndGetResult();

            final CommentResponse commentResponse1 = result.getData().get(0);
            assertThat(commentResponse1.getId(), is(secondComment.getId()));
            assertThat(commentResponse1.getUserId(), is(user.getId()));
            assertThat(commentResponse1.getContent(), is("신고된 댓글입니다."));
            assertThat(commentResponse1.getLiked(), is(false));
            assertThat(commentResponse1.getLikeCount(), is(0L));
            assertThat(commentResponse1.getReplyCommentCount(), is(1L));
            assertThat(commentResponse1.getDeleted(), is(false));
            assertThat(commentResponse1.getReported(), is(true));
            assertThat(commentResponse1.getCreatedAt(), notNullValue());
            assertThat(commentResponse1.getUpdatedAt(), notNullValue());
            assertThat(commentResponse1.getEditedAt(), notNullValue());
            assertThat(commentResponse1.getUserProfile(), notNullValue());
            assertThat(commentResponse1.getReplyComments().size(), is(1));
        }
    }

    @Nested
    class WhenPostsHaveSomeDeletedComments {

        private static Stream<Arguments> valueProvider() {
            return Stream.of(
                Arguments.of(Status.DELETED_BY_ADMIN, "관리자에 의해 삭제된 댓글입니다."),
                Arguments.of(Status.DELETED_BY_USER, "삭제된 댓글입니다.")
            );
        }


        @ParameterizedTest(name = "{index} => status=''{0}'', expectedContent=''{1}''")
        @MethodSource("valueProvider")
        void shouldReturnCommentsWithSomeDeleted(Status status, String expectedContent) throws Exception {
            // Given
            secondComment.setStatus(status);
            secondComment.setDeletedAt(LocalDateTime.now());
            secondComment.setReplyCommentCount(1L);
            itUtil.createComment(post1.getId(), user.getId(), secondComment.getId(), CommentType.REPLY_COMMENT, Status.ACTIVE);

            secondComment = itUtil.updateComment(secondComment);

            // When
            final CommentPagingResponse result = callApiAndGetResult();

            // Then
            final CommentResponse commentResponse = result.getData().get(0);
            assertThat(commentResponse.getId(), is(secondComment.getId()));
            assertThat(commentResponse.getUserId(), is(user.getId()));
            assertThat(commentResponse.getContent(), is(expectedContent));
            assertThat(commentResponse.getLiked(), is(false));
            assertThat(commentResponse.getLikeCount(), is(0L));
            assertThat(commentResponse.getReplyCommentCount(), is(1L));
            assertThat(commentResponse.getDeleted(), is(true));
            assertThat(commentResponse.getReported(), is(false));
            assertThat(commentResponse.getCreatedAt(), notNullValue());
            assertThat(commentResponse.getUpdatedAt(), notNullValue());
            assertThat(commentResponse.getEditedAt(), notNullValue());
            assertThat(commentResponse.getUserProfile(), nullValue());
        }
    }
}
