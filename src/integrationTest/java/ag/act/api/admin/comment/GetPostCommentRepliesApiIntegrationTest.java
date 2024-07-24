package ag.act.api.admin.comment;


import ag.act.AbstractCommonIntegrationTest;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static ag.act.TestUtil.assertTime;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("checkstyle:LineLength")
class GetPostCommentRepliesApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}/comments/{commentId}/replies";

    private String adminJwt;
    private Stock stock;
    private Board board;
    private Post post;
    private User user;
    private Comment comment;
    private Comment firstReply;
    private Comment secondReply;

    @BeforeEach
    void setUp() {
        itUtil.init();
        User adminUser = itUtil.createAdminUser();
        adminJwt = itUtil.createJwt(adminUser.getId());

        user = itUtil.createUser();
        stock = itUtil.createStock();
        board = itUtil.createBoard(stock);
        itUtil.createSolidarity(stock.getCode());
        itUtil.createUserHoldingStock(stock.getCode(), user);

        post = itUtil.createPost(board, user.getId());
        itUtil.createPost(board, user.getId());
        comment = itUtil.createComment(post.getId(), user, CommentType.POST, Status.ACTIVE);
        firstReply = itUtil.createComment(post.getId(), user.getId(), comment.getId(), CommentType.REPLY_COMMENT, Status.ACTIVE);
        secondReply = itUtil.createComment(post.getId(), user.getId(), comment.getId(), CommentType.REPLY_COMMENT, Status.ACTIVE);
    }

    @Nested
    class WhenGetPostCommentReplies {

        @Test
        void shouldReturnSuccess() throws Exception {
            final CommentPagingResponse result = callApiAndGetResult();

            assertResponse(result);
        }

        @Nested
        class WhenGetReplyCommentsWithoutDeletedStatus {

            private Comment thirdReply;

            @BeforeEach
            void setUp() {
                thirdReply = itUtil.createComment(post.getId(), user.getId(), comment.getId(), CommentType.REPLY_COMMENT, Status.DELETED);
            }

            @Test
            void shouldNotContainDeletedStatusComment() throws Exception {
                final CommentPagingResponse result = callApiAndGetResult();

                assertComments(result.getData());
                assertPaging(result.getPaging());

                boolean isNotContainThirdReply = result.getData()
                    .stream()
                    .noneMatch(commentResponse -> commentResponse.getId().equals(thirdReply.getId()));

                assertThat(isNotContainThirdReply, is(true));
            }
        }
    }

    @Nested
    class WhenCommentHasSomeDeletedCommentReplies {

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
            secondReply.setStatus(status);
            secondReply.setDeletedAt(LocalDateTime.now());
            secondReply = itUtil.updateComment(secondReply);

            // When
            final CommentPagingResponse result = callApiAndGetResult();

            // Then
            final CommentResponse commentResponse = result.getData().get(0);
            assertThat(commentResponse.getId(), is(secondReply.getId()));
            assertThat(commentResponse.getUserId(), is(user.getId()));
            assertThat(commentResponse.getContent(), is(expectedContent));
            assertThat(commentResponse.getLiked(), is(false));
            assertThat(commentResponse.getLikeCount(), is(0L));
            assertThat(commentResponse.getDeleted(), is(true));
            assertThat(commentResponse.getReported(), is(false));
            assertThat(commentResponse.getCreatedAt(), notNullValue());
            assertThat(commentResponse.getUpdatedAt(), notNullValue());
            assertThat(commentResponse.getEditedAt(), notNullValue());
            assertThat(commentResponse.getUserProfile(), nullValue());
            assertThat(commentResponse.getReplyComments(), nullValue());
            assertThat(commentResponse.getReplyCommentCount(), nullValue());
        }
    }

    private CommentPagingResponse callApiAndGetResult() throws Exception {
        MvcResult response = mockMvc
            .perform(
                get(TARGET_API, stock.getCode(), board.getGroup(), post.getId(), comment.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(adminJwt)))
            )
            .andExpect(status().isOk())
            .andReturn();

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            CommentPagingResponse.class
        );
    }

    private CommentPagingResponse callApiAndGetResult(String jwt, ResultMatcher resultMatcher) throws Exception {
        MvcResult response = mockMvc
            .perform(
                get(TARGET_API, stock.getCode(), board.getGroup(), post.getId(), comment.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(resultMatcher)
            .andReturn();

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            CommentPagingResponse.class
        );
    }

    private void assertResponse(CommentPagingResponse result) {
        final Paging paging = result.getPaging();
        final List<CommentResponse> commentResponses = result.getData();

        assertPaging(paging);
        assertComments(commentResponses);
    }

    private void assertComments(List<CommentResponse> commentResponses) {
        assertThat(commentResponses.size(), is(2));

        final CommentResponse commentResponse1 = commentResponses.get(0);
        assertThat(commentResponse1.getId(), is(secondReply.getId()));
        assertThat(commentResponse1.getUserId(), is(user.getId()));
        assertThat(commentResponse1.getContent(), is(secondReply.getContent()));
        assertThat(commentResponse1.getLiked(), is(false));
        assertThat(commentResponse1.getLikeCount(), is(0L));
        assertThat(commentResponse1.getDeleted(), is(false));
        assertThat(commentResponse1.getReported(), is(false));
        assertTime(commentResponse1.getCreatedAt(), notNullValue());
        assertTime(commentResponse1.getUpdatedAt(), notNullValue());
        assertTime(commentResponse1.getEditedAt(), notNullValue());
        assertThat(commentResponse1.getUserProfile(), notNullValue());
        assertThat(commentResponse1.getReplyComments(), nullValue());
        assertThat(commentResponse1.getReplyCommentCount(), nullValue());

        final CommentResponse commentResponse2 = commentResponses.get(1);
        assertThat(commentResponse2.getId(), is(firstReply.getId()));
        assertThat(commentResponse2.getUserId(), is(user.getId()));
        assertThat(commentResponse2.getContent(), is(firstReply.getContent()));
        assertThat(commentResponse2.getLiked(), is(false));
        assertThat(commentResponse2.getLikeCount(), is(0L));
        assertThat(commentResponse2.getDeleted(), is(false));
        assertThat(commentResponse2.getReported(), is(false));
        assertThat(commentResponse2.getCreatedAt(), notNullValue());
        assertThat(commentResponse2.getUpdatedAt(), notNullValue());
        assertThat(commentResponse2.getEditedAt(), notNullValue());
        assertThat(commentResponse2.getUserProfile(), notNullValue());
        assertThat(commentResponse2.getReplyComments(), nullValue());
        assertThat(commentResponse2.getReplyCommentCount(), nullValue());
    }

    private void assertPaging(Paging paging) {
        assertThat(paging.getPage(), is(1));
        assertThat(paging.getTotalPages(), is(1));
        assertThat(paging.getTotalElements(), is(2L));
        assertThat(paging.getSorts().size(), is(1));
        assertThat(paging.getSorts().get(0), is(CREATED_AT_DESC));
    }


    @Nested
    class WhenUserIsAcceptor {

        private User acceptorUser;
        private String acceptorUserJwt;

        @BeforeEach
        void setUp() {
            stock = itUtil.createStock();
            board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.DIGITAL_DELEGATION);
            acceptorUser = itUtil.createAcceptorUser();
            post = itUtil.createPost(board, user.getId()); // create post without digital document
            acceptorUserJwt = itUtil.createJwt(acceptorUser.getId());
        }

        @Nested
        class WhenPostNotHasDigitalDocument {

            @Test
            void shouldReturn400Response() throws Exception {
                callApiAndGetResult(acceptorUserJwt, status().isBadRequest());
            }
        }

        @Nested
        class WhenPostHasDigitalDocument {

            @BeforeEach
            void setUp() {
                itUtil.createDigitalDocument(post, stock, acceptorUser);
                comment = itUtil.createComment(post.getId(), user, CommentType.POST, Status.ACTIVE);

                // create reply comments
                firstReply = itUtil.createComment(post.getId(), user.getId(), comment.getId(), CommentType.REPLY_COMMENT, Status.ACTIVE);
                secondReply = itUtil.createComment(post.getId(), user.getId(), comment.getId(), CommentType.REPLY_COMMENT, Status.ACTIVE);
            }

            @Test
            void shouldReturnSuccess() throws Exception {
                CommentPagingResponse result = callApiAndGetResult(acceptorUserJwt, status().isOk());

                assertResponse(result);
            }
        }

        @Nested
        class WhenUserIsNotAcceptorOfPost {

            @BeforeEach
            void setUp() {
                User anotherAcceptorUser = itUtil.createAcceptorUser();
                itUtil.createDigitalDocument(post, stock, anotherAcceptorUser);
            }

            @Test
            void shouldReturn400Response() throws Exception {
                callApiAndGetResult(acceptorUserJwt, status().isBadRequest());
            }
        }
    }
}
