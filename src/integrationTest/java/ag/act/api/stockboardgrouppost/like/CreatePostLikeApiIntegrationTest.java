package ag.act.api.stockboardgrouppost.like;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.PostUserProfile;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.model.PostDataResponse;
import ag.act.model.PostResponse;
import ag.act.model.Status;
import ag.act.model.UserProfileResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.UnsupportedEncodingException;

import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CreatePostLikeApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}/likes";

    private String jwt;
    private Stock stock;
    private Board board;
    private User user;
    private Post post;
    private PostUserProfile postUserProfile;

    @BeforeEach
    void setUp() {
        itUtil.init();
        user = itUtil.createUser();
        stock = itUtil.createStock();
        board = itUtil.createBoard(stock);
        post = itUtil.createPost(board, user.getId());
        postUserProfile = post.getPostUserProfile();
    }

    @Nested
    class WhenCreatePostLikePost {

        private User currentUser;

        private boolean existsLikeInDatabase(Long postId, Long userId) {
            return itUtil.existsByPostIdAndUserId(postId, userId);
        }

        @Nested
        class WhenUserHasStock extends DefaultTestCases {
            @BeforeEach
            void setUp() {
                currentUser = user;
                jwt = itUtil.createJwt(currentUser.getId());
            }
        }

        @Nested
        class WhenUserHasNoStock extends DefaultTestCases {
            @BeforeEach
            void setUp() {
                currentUser = itUtil.createUser();
                jwt = itUtil.createJwt(currentUser.getId());
            }
        }

        @SuppressWarnings({"unused", "JUnitMalformedDeclaration"})
        class DefaultTestCases {

            @Order(1)
            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldLikeReturnSuccess() throws Exception {
                MvcResult response = callApi(post(TARGET_API, stock.getCode(), board.getGroup(), post.getId()));

                assertThat(existsLikeInDatabase(post.getId(), currentUser.getId()), is(true));
                assertResponse(response);
            }

            @Order(2)
            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldUnLikeReturnSuccess() throws Exception {
                MvcResult response = callApi(delete(TARGET_API, stock.getCode(), board.getGroup(), post.getId()));

                assertThat(existsLikeInDatabase(post.getId(), currentUser.getId()), is(false));
                assertResponse(response);
            }

            private void assertResponse(MvcResult response) throws JsonProcessingException, UnsupportedEncodingException {
                final PostDataResponse result = objectMapperUtil.toResponse(
                    response.getResponse().getContentAsString(),
                    PostDataResponse.class
                );

                final PostResponse createResponse = result.getData();

                assertThat(createResponse.getId(), is(notNullValue()));
                assertThat(createResponse.getBoardId(), is(board.getId()));
                assertThat(createResponse.getTitle(), is(post.getTitle()));
                assertThat(createResponse.getContent(), is(post.getContent()));
                assertThat(createResponse.getStatus(), is(Status.ACTIVE));

                final UserProfileResponse createResponseUserProfile = createResponse.getUserProfile();
                assertThat(createResponseUserProfile.getNickname(), is(postUserProfile.getNickname()));
                assertThat(createResponseUserProfile.getProfileImageUrl(), is(postUserProfile.getProfileImageUrl()));
                assertThat(createResponseUserProfile.getIndividualStockCountLabel(), is(postUserProfile.getIndividualStockCountLabel()));
                assertThat(createResponseUserProfile.getTotalAssetLabel(), is(postUserProfile.getTotalAssetLabel()));
            }
        }
    }

    @NotNull
    private MvcResult callApi(MockHttpServletRequestBuilder requestBuilder) throws Exception {
        return mockMvc
            .perform(
                requestBuilder
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(status().isOk())
            .andReturn();
    }
}
