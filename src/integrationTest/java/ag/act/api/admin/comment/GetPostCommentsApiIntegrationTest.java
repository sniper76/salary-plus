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
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetPostCommentsApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}/comments";

    private String adminJwt;
    private Stock stock;
    private Board board;
    private Post post;
    private User user;
    private Comment firstComment;
    private Comment secondComment;

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
        firstComment = itUtil.createComment(post.getId(), user, CommentType.POST, Status.ACTIVE);
        secondComment = itUtil.createComment(post.getId(), user, CommentType.POST, Status.ACTIVE);
    }

    @Nested
    class WhenGetPostComments {

        @Test
        void shouldReturnSuccess() throws Exception {
            CommentPagingResponse result = callApiAndGetResult(adminJwt, status().isOk());

            assertResponse(result);
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
            assertThat(commentResponse1.getId(), is(secondComment.getId()));
            assertThat(commentResponse1.getUserId(), is(user.getId()));
            assertThat(commentResponse1.getContent(), is(secondComment.getContent()));
            assertThat(commentResponse1.getLiked(), is(false));
            assertThat(commentResponse1.getLikeCount(), is(0L));
            assertThat(commentResponse1.getReplyCommentCount(), is(0L));
            assertThat(commentResponse1.getDeleted(), is(false));
            assertThat(commentResponse1.getReported(), is(false));
            assertThat(commentResponse1.getCreatedAt(), notNullValue());
            assertThat(commentResponse1.getUpdatedAt(), notNullValue());
            assertThat(commentResponse1.getEditedAt(), notNullValue());

            final CommentResponse commentResponse2 = commentResponses.get(1);
            assertThat(commentResponse2.getId(), is(firstComment.getId()));
            assertThat(commentResponse2.getUserId(), is(user.getId()));
            assertThat(commentResponse2.getContent(), is(firstComment.getContent()));
            assertThat(commentResponse2.getLiked(), is(false));
            assertThat(commentResponse2.getLikeCount(), is(0L));
            assertThat(commentResponse2.getReplyCommentCount(), is(0L));
            assertThat(commentResponse2.getDeleted(), is(false));
            assertThat(commentResponse2.getReported(), is(false));
            assertThat(commentResponse2.getCreatedAt(), notNullValue());
            assertThat(commentResponse2.getUpdatedAt(), notNullValue());
            assertThat(commentResponse2.getEditedAt(), notNullValue());
        }

        private void assertPaging(Paging paging) {
            assertThat(paging.getPage(), is(1));
            assertThat(paging.getTotalPages(), is(1));
            assertThat(paging.getTotalElements(), is(2L));
            assertThat(paging.getSorts().size(), is(1));
            assertThat(paging.getSorts().get(0), is(CREATED_AT_DESC));
        }

        @Nested
        class WhenGetPostCommentsWithoutDeletedStatusComments {

            private Comment deletedComment;

            @BeforeEach
            void setUp() {
                deletedComment = itUtil.createComment(post.getId(), user, CommentType.POST, Status.DELETED);
            }

            @Test
            void shouldNotContainDeletedStatusComment() throws Exception {
                CommentPagingResponse result = callApiAndGetResult(adminJwt, status().isOk());

                assertComments(result.getData());
                assertPaging(result.getPaging());

                boolean isNotContainDeleteComment = result.getData()
                    .stream()
                    .noneMatch(commentResponse -> commentResponse.getId().equals(deletedComment.getId()));
                assertThat(isNotContainDeleteComment, is(true));
            }
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

                    // create 2 comments
                    firstComment = itUtil.createComment(post.getId(), user, CommentType.POST, Status.ACTIVE);
                    secondComment = itUtil.createComment(post.getId(), user, CommentType.POST, Status.ACTIVE);
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

    @NotNull
    private CommentPagingResponse callApiAndGetResult(String jwt, ResultMatcher resultMatcher) throws Exception {
        MvcResult response = mockMvc
            .perform(
                get(TARGET_API, stock.getCode(), board.getGroup(), post.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(resultMatcher)
            .andReturn();

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            CommentPagingResponse.class
        );
    }
}
