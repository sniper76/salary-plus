package ag.act.api.stockboardgrouppost.like;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.AutomatedAuthorPush;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Push;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.AutomatedPushContentType;
import ag.act.enums.AutomatedPushCriteria;
import ag.act.enums.BoardGroup;
import ag.act.enums.push.PushTargetType;
import ag.act.model.PostDetailsDataResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AutomatedAuthorPushLikeApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}/likes";
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

    private PostDetailsDataResponse callApiLiked(String userJwt) throws Exception {
        MvcResult response = mockMvc
            .perform(
                post(TARGET_API, stock.getCode(), board.getGroup(), post.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + userJwt)
            )
            .andExpect(status().isOk())
            .andReturn();

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            PostDetailsDataResponse.class
        );
    }

    private PostDetailsDataResponse callApiUnliked(String userJwt) throws Exception {
        MvcResult response = mockMvc
            .perform(
                delete(TARGET_API, stock.getCode(), board.getGroup(), post.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + userJwt)
            )
            .andExpect(status().isOk())
            .andReturn();

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            PostDetailsDataResponse.class
        );
    }

    @Nested
    class WhenLikes {
        private User user9;
        private User user10;
        private String jwt;

        private List<User> createPostWithLiked(int count) {
            List<User> userList = new ArrayList<>();
            post = itUtil.createPost(board, author.getId());

            for (int i = 0; i < count; i++) {
                final User user1 = itUtil.createUser();
                itUtil.createUserHoldingStock(stock.getCode(), user1);
                itUtil.createPostUserLike(user1, post);
                userList.add(user1);
            }
            return userList;
        }

        @Nested
        class AndTry9th {

            @BeforeEach
            void setUp() {
                createPostWithLiked(8);

                user9 = itUtil.createUser();
                jwt = itUtil.createJwt(user9.getId());
                itUtil.createUserHoldingStock(stock.getCode(), user9);
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                final PostDetailsDataResponse response = callApiLiked(jwt);

                assertThat(response.getData(), notNullValue());

                final List<AutomatedAuthorPush> automatedAuthorPushes = itUtil.findAllAutomatedAuthorPushesByContentId(post.getId());
                assertThat(automatedAuthorPushes.size(), is(0));
                assertThat(itUtil.findAllPushesByPostId(post.getId()).size(), is(0));
            }
        }

        @Nested
        class AndTry10th {

            @BeforeEach
            void setUp() {
                createPostWithLiked(9);

                user10 = itUtil.createUser();
                jwt = itUtil.createJwt(user10.getId());
                itUtil.createUserHoldingStock(stock.getCode(), user10);
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                final PostDetailsDataResponse response = callApiLiked(jwt);

                assertThat(response.getData(), notNullValue());

                final List<AutomatedAuthorPush> automatedAuthorPushes = itUtil.findAllAutomatedAuthorPushesByContentId(post.getId());
                assertThat(automatedAuthorPushes.size(), is(1));

                final AutomatedAuthorPush automatedAuthorPush = automatedAuthorPushes.get(0);
                final Optional<Push> push = itUtil.findPush(automatedAuthorPush.getPushId());
                assertThat(push.isPresent(), is(true));
                assertThat(push.get().getTitle(), is(AutomatedPushCriteria.LIKE.getTitle()));
                assertThat(push.get().getContent(), is(AutomatedPushCriteria.LIKE.getMessage()));

                final Integer criteriaValue = automatedAuthorPush.getCriteriaValue();
                assertThat(criteriaValue, is(10));
            }
        }

        @Nested
        class AndUnlikeWhen11th {

            @BeforeEach
            void setUp() {
                final List<User> userList = createPostWithLiked(11);
                Collections.shuffle(userList);

                jwt = itUtil.createJwt(userList.get(0).getId());

                final Push push = itUtil.createPush(
                    AutomatedPushCriteria.LIKE.getTitle(),
                    AutomatedPushCriteria.LIKE.getMessage(),
                    PushTargetType.AUTOMATED_AUTHOR,
                    post.getId()
                );
                itUtil.createAutomatedAuthorPush(
                    post.getId(), push.getId(), 10, AutomatedPushCriteria.LIKE, AutomatedPushContentType.POST
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                final PostDetailsDataResponse response = callApiUnliked(jwt);

                assertThat(response.getData(), notNullValue());

                final List<AutomatedAuthorPush> automatedAuthorPushes = itUtil.findAllAutomatedAuthorPushesByContentId(post.getId());
                assertThat(automatedAuthorPushes.size(), is(1));

                final AutomatedAuthorPush automatedAuthorPush = automatedAuthorPushes.get(0);
                assertThat(itUtil.findPush(automatedAuthorPush.getPushId()).isPresent(), is(true));

                final Integer criteriaValue = automatedAuthorPush.getCriteriaValue();
                assertThat(criteriaValue, is(10));
            }
        }
    }
}
