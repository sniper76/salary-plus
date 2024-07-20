package ag.act.api.stockboardgrouppost.comment;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.AutomatedAuthorPush;
import ag.act.entity.Board;
import ag.act.entity.Comment;
import ag.act.entity.Post;
import ag.act.entity.Push;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.AutomatedPushContentType;
import ag.act.enums.AutomatedPushCriteria;
import ag.act.enums.BoardGroup;
import ag.act.enums.CommentType;
import ag.act.enums.push.PushTargetType;
import ag.act.model.CommentDataResponse;
import ag.act.model.CreateCommentRequest;
import ag.act.model.SimpleStringResponse;
import ag.act.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static ag.act.TestUtil.someBoardCategory;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

class AutomatedAuthorPushReplyApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API_CREATE =
        "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}/comments/{commentId}/replies";
    private static final String TARGET_API_DELETE = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}/comments/{commentId}";

    private Stock stock;
    private Board board;
    private Post post;
    private User author;
    private Comment parentComment;

    @BeforeEach
    void setUp() {
        author = itUtil.createUser();
        stock = itUtil.createStock();
        itUtil.createSolidarity(stock.getCode());
        itUtil.createUserHoldingStock(stock.getCode(), author);
        final BoardGroup boardGroup = someEnum(BoardGroup.class);
        board = itUtil.createBoard(stock, boardGroup, someBoardCategory(boardGroup));

        cleanUpAllAutomatedAuthorPushes();
    }

    private void cleanUpAllAutomatedAuthorPushes() {
        itUtil.deleteAllAutomatedAuthorPushes();
    }

    private CommentDataResponse callApiComment(String userJwt) throws Exception {
        final String requestBody = objectMapperUtil.toRequestBody(
            new CreateCommentRequest()
                .content(someAlphanumericString(10))
                .isAnonymous(Boolean.FALSE)
        );

        MvcResult response = mockMvc
            .perform(
                post(TARGET_API_CREATE, stock.getCode(), board.getGroup(), post.getId(), parentComment.getId())
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + userJwt)
            )
            .andExpect(status().isOk())
            .andReturn();

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            CommentDataResponse.class
        );
    }

    private SimpleStringResponse callApiDeleteReply(String userJwt, Comment comment) throws Exception {
        MvcResult response = mockMvc
            .perform(
                delete(TARGET_API_DELETE, stock.getCode(), board.getGroup(), post.getId(), comment.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + userJwt)
            )
            .andExpect(status().isOk())
            .andReturn();

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            SimpleStringResponse.class
        );
    }

    @Nested
    class WhenReplies {
        private User newUser;
        private String jwt;

        private List<Comment> createPostWithReplies(int count) {
            List<Comment> replyList = new ArrayList<>();
            post = itUtil.createPost(board, author.getId());
            parentComment = itUtil.createComment(post.getId(), author, CommentType.POST, Status.ACTIVE);

            for (int i = 0; i < count; i++) {
                final User user1 = itUtil.createUser();
                itUtil.createUserHoldingStock(stock.getCode(), user1);
                replyList.add(
                    itUtil.createComment(post.getId(), user1.getId(), parentComment.getId(), CommentType.REPLY_COMMENT, Status.ACTIVE)
                );
            }
            return replyList;
        }

        @Nested
        class AndTry1th {

            @BeforeEach
            void setUp() {
                post = itUtil.createPost(board, author.getId());
                parentComment = itUtil.createComment(post.getId(), author, CommentType.POST, Status.ACTIVE);

                newUser = itUtil.createUser();
                jwt = itUtil.createJwt(newUser.getId());
                itUtil.createUserHoldingStock(stock.getCode(), newUser);
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API_CREATE)
            @Test
            void shouldReturnSuccess() throws Exception {
                final CommentDataResponse response = callApiComment(jwt);

                assertThat(response.getData(), is(notNullValue()));

                final List<AutomatedAuthorPush> automatedAuthorPushes = itUtil.findAllAutomatedAuthorPushesByContentId(parentComment.getId());
                assertThat(automatedAuthorPushes.size(), is(1));

                final AutomatedAuthorPush automatedAuthorPush = automatedAuthorPushes.get(0);
                final Optional<Push> push = itUtil.findPush(automatedAuthorPush.getPushId());
                assertThat(push.isPresent(), is(true));
                assertThat(push.get().getTitle(), is(AutomatedPushCriteria.REPLY.getTitle()));
                assertThat(push.get().getContent(), is(AutomatedPushCriteria.REPLY.getMessage()));

                final Integer criteriaValue = automatedAuthorPush.getCriteriaValue();
                assertThat(criteriaValue, is(1));
            }
        }

        @Nested
        class And2thTo100th {

            @DisplayName("Should return 200 response code when call " + TARGET_API_CREATE)
            @ParameterizedTest(name = "{index} => commentCount=''{0}''")
            @ValueSource(ints = {2, 3, 4, 5, 6, 7, 8, 9, 10, 15, 20, 30, 50, 100})
            void shouldReturnSuccess(int commentCount) throws Exception {
                createPostWithReplies(commentCount - 1);

                newUser = itUtil.createUser();
                jwt = itUtil.createJwt(newUser.getId());
                itUtil.createUserHoldingStock(stock.getCode(), newUser);

                final CommentDataResponse response = callApiComment(jwt);

                assertThat(response.getData(), is(notNullValue()));

                final List<AutomatedAuthorPush> automatedAuthorPushes = itUtil.findAllAutomatedAuthorPushesByContentId(parentComment.getId());
                assertThat(automatedAuthorPushes.size(), is(1));

                final AutomatedAuthorPush automatedAuthorPush = automatedAuthorPushes.get(0);
                final Optional<Push> push = itUtil.findPush(automatedAuthorPush.getPushId());
                assertThat(push.isPresent(), is(true));
                assertThat(push.get().getContent(), is(AutomatedPushCriteria.REPLY.getMessage()));

                final Integer criteriaValue = automatedAuthorPush.getCriteriaValue();
                assertThat(criteriaValue, is(commentCount));
            }

            @Nested
            class WhenParentCommentDeleted {

                @BeforeEach
                void setUp() {
                    post = itUtil.createPost(board, author.getId());
                    parentComment = itUtil.createComment(post.getId(), author, CommentType.POST, Status.DELETED_BY_USER);
                }

                @Test
                void shouldReturnSuccess() throws Exception {
                    newUser = itUtil.createUser();
                    jwt = itUtil.createJwt(newUser.getId());
                    itUtil.createUserHoldingStock(stock.getCode(), newUser);

                    final CommentDataResponse response = callApiComment(jwt);

                    assertThat(response.getData(), is(notNullValue()));

                    final List<AutomatedAuthorPush> automatedAuthorPushes =
                        itUtil.findAllAutomatedAuthorPushesByContentId(parentComment.getId());
                    assertThat(automatedAuthorPushes.size(), is(1));

                    final AutomatedAuthorPush automatedAuthorPush = automatedAuthorPushes.get(0);
                    final Optional<Push> push = itUtil.findPush(automatedAuthorPush.getPushId());
                    assertThat(push.isPresent(), is(true));
                    assertThat(push.get().getContent(), is(AutomatedPushCriteria.REPLY.getMessage()));
                }
            }
        }

        @Nested
        class AndDeleteReply51th {
            private Comment deleteReply;

            @BeforeEach
            void setUp() {
                final List<Comment> postWithReplies = createPostWithReplies(51);
                Collections.shuffle(postWithReplies);
                deleteReply = postWithReplies.get(0);

                newUser = itUtil.findUser(deleteReply.getUserId());
                jwt = itUtil.createJwt(newUser.getId());

                final Push push = itUtil.createPush(
                    AutomatedPushCriteria.REPLY.getTitle(),
                    AutomatedPushCriteria.REPLY.getMessage(),
                    PushTargetType.AUTOMATED_AUTHOR,
                    post.getId()
                );
                itUtil.createAutomatedAuthorPush(
                    parentComment.getId(), push.getId(), 50, AutomatedPushCriteria.REPLY, AutomatedPushContentType.COMMENT
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API_DELETE)
            @Test
            void shouldReturnSuccess() throws Exception {
                final SimpleStringResponse response = callApiDeleteReply(jwt, deleteReply);

                assertThat(response.getStatus(), is("ok"));

                final List<AutomatedAuthorPush> automatedAuthorPushes = itUtil.findAllAutomatedAuthorPushesByContentId(parentComment.getId());
                assertThat(automatedAuthorPushes.size(), is(1));

                final AutomatedAuthorPush automatedAuthorPush = automatedAuthorPushes.get(0);
                final Optional<Push> push = itUtil.findPush(automatedAuthorPush.getPushId());
                assertThat(push.isPresent(), is(true));
                assertThat(push.get().getContent(), is(AutomatedPushCriteria.REPLY.getMessage()));

                final Integer criteriaValue = automatedAuthorPush.getCriteriaValue();
                assertThat(criteriaValue, is(50));
            }
        }
    }
}
