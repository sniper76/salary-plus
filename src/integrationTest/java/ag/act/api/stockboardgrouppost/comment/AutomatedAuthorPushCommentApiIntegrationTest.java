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

import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

class AutomatedAuthorPushCommentApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API_CREATE = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}/comments";
    private static final String TARGET_API_DELETE = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}/comments/{commentId}";

    private Stock stock;
    private Board board;
    private Post post;
    private User author;

    @BeforeEach
    void setUp() {
        author = itUtil.createUser();
        stock = itUtil.createStock();
        itUtil.createSolidarity(stock.getCode());
        itUtil.createUserHoldingStock(stock.getCode(), author);
        board = itUtil.createBoard(stock, BoardGroup.ANALYSIS, BoardGroup.ANALYSIS.getCategories().get(0));
    }

    private CommentDataResponse callApiComment(String userJwt) throws Exception {
        MvcResult response = mockMvc
            .perform(
                post(TARGET_API_CREATE, stock.getCode(), board.getGroup(), post.getId())
                    .content(objectMapperUtil.toRequestBody(new CreateCommentRequest()
                        .content(someAlphanumericString(10)).isAnonymous(Boolean.FALSE)))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(userJwt)))
            )
            .andExpect(status().isOk())
            .andReturn();

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            CommentDataResponse.class
        );
    }

    private SimpleStringResponse callApiDeleteComment(String userJwt, Comment comment) throws Exception {
        MvcResult response = mockMvc
            .perform(
                delete(TARGET_API_DELETE, stock.getCode(), board.getGroup(), post.getId(), comment.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(userJwt)))
            )
            .andExpect(status().isOk())
            .andReturn();

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            SimpleStringResponse.class
        );
    }

    @Nested
    class WhenComments {
        private User newUser;
        private String jwt;

        private List<Comment> createPostWithComments(int count) {
            List<Comment> commentList = new ArrayList<>();
            post = itUtil.createPost(board, author.getId());

            for (int i = 0; i < count; i++) {
                final User user1 = itUtil.createUser();
                itUtil.createUserHoldingStock(stock.getCode(), user1);
                commentList.add(itUtil.createComment(post.getId(), user1, CommentType.POST, Status.ACTIVE));
            }
            return commentList;
        }

        @Nested
        class AndTry1th {

            @BeforeEach
            void setUp() {
                post = itUtil.createPost(board, author.getId());

                newUser = itUtil.createUser();
                jwt = itUtil.createJwt(newUser.getId());
                itUtil.createUserHoldingStock(stock.getCode(), newUser);
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API_CREATE)
            @Test
            void shouldReturnSuccess() throws Exception {
                final CommentDataResponse response = callApiComment(jwt);

                assertThat(response.getData(), is(notNullValue()));

                final List<AutomatedAuthorPush> automatedAuthorPushes = itUtil.findAllAutomatedAuthorPushesByContentId(post.getId());
                assertThat(automatedAuthorPushes.size(), is(1));

                final AutomatedAuthorPush automatedAuthorPush = automatedAuthorPushes.get(0);
                final Optional<Push> push = itUtil.findPush(automatedAuthorPush.getPushId());
                assertThat(push.isPresent(), is(true));
                assertThat(push.get().getContent(), is(AutomatedPushCriteria.COMMENT.getMessage()));

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
                createPostWithComments(commentCount - 1);

                newUser = itUtil.createUser();
                jwt = itUtil.createJwt(newUser.getId());
                itUtil.createUserHoldingStock(stock.getCode(), newUser);

                final CommentDataResponse response = callApiComment(jwt);

                assertThat(response.getData(), is(notNullValue()));

                final List<AutomatedAuthorPush> automatedAuthorPushes = itUtil.findAllAutomatedAuthorPushesByContentId(post.getId());
                assertThat(automatedAuthorPushes.size(), is(1));

                final AutomatedAuthorPush automatedAuthorPush = automatedAuthorPushes.get(0);
                final Optional<Push> push = itUtil.findPush(automatedAuthorPush.getPushId());
                assertThat(push.isPresent(), is(true));
                assertThat(push.get().getTitle(), is(AutomatedPushCriteria.COMMENT.getTitle()));
                assertThat(push.get().getContent(), is(AutomatedPushCriteria.COMMENT.getMessage()));

                final Integer criteriaValue = automatedAuthorPush.getCriteriaValue();
                assertThat(criteriaValue, is(commentCount));
            }
        }

        @Nested
        class AndDeleteComment51th {
            private Comment deleteComment;

            @BeforeEach
            void setUp() {
                final List<Comment> postWithComments = createPostWithComments(51);
                Collections.shuffle(postWithComments);
                deleteComment = postWithComments.get(0);

                newUser = itUtil.findUser(deleteComment.getUserId());
                jwt = itUtil.createJwt(newUser.getId());

                final Push push = itUtil.createPush(
                    AutomatedPushCriteria.COMMENT.getTitle(),
                    AutomatedPushCriteria.COMMENT.getMessage(),
                    PushTargetType.AUTOMATED_AUTHOR,
                    post.getId()
                );
                itUtil.createAutomatedAuthorPush(
                    post.getId(), push.getId(), 50, AutomatedPushCriteria.COMMENT, AutomatedPushContentType.COMMENT
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API_DELETE)
            @Test
            void shouldReturnSuccess() throws Exception {
                final SimpleStringResponse response = callApiDeleteComment(jwt, deleteComment);

                assertThat(response.getStatus(), is("ok"));

                final List<AutomatedAuthorPush> automatedAuthorPushes = itUtil.findAllAutomatedAuthorPushesByContentId(post.getId());
                assertThat(automatedAuthorPushes.size(), is(1));

                final AutomatedAuthorPush automatedAuthorPush = automatedAuthorPushes.get(0);
                final Optional<Push> push = itUtil.findPush(automatedAuthorPush.getPushId());
                assertThat(push.isPresent(), is(true));
                assertThat(push.get().getTitle(), is(AutomatedPushCriteria.COMMENT.getTitle()));
                assertThat(push.get().getContent(), is(AutomatedPushCriteria.COMMENT.getMessage()));

                final Integer criteriaValue = automatedAuthorPush.getCriteriaValue();
                assertThat(criteriaValue, is(50));
            }
        }
    }
}
