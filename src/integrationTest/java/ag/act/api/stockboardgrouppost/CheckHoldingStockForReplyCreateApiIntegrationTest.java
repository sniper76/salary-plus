package ag.act.api.stockboardgrouppost;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Comment;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.BoardGroup;
import ag.act.enums.CommentType;
import ag.act.enums.RoleType;
import ag.act.model.CommentDataResponse;
import ag.act.model.CommentPagingResponse;
import ag.act.model.CommentResponse;
import ag.act.model.CreateCommentRequest;
import ag.act.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

class CheckHoldingStockForReplyCreateApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}/comments/{commentId}/replies";

    private CreateCommentRequest request;
    private String jwt;
    private Stock stock;
    private Board board;
    private User user;
    private Post post;
    private Comment comment;

    @BeforeEach
    void setUp() {
        stock = itUtil.createStock();
        final BoardGroup boardGroup = someEnum(BoardGroup.class);
        board = itUtil.createBoard(stock, boardGroup, boardGroup.getCategories().get(0));
    }

    private CreateCommentRequest genRequest() {
        return new CreateCommentRequest()
            .content(someAlphanumericString(300))
            .isAnonymous(Boolean.FALSE);
    }

    private MvcResult callPostApiWithJwt(ResultMatcher matcher, String jwt, CreateCommentRequest request) throws Exception {
        return mockMvc
            .perform(
                post(TARGET_API, stock.getCode(), board.getGroup(), post.getId(), comment.getId())
                    .content(objectMapperUtil.toRequestBody(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(matcher)
            .andReturn();
    }

    private MvcResult callGetApiWithJwt(ResultMatcher matcher, String jwt) throws Exception {
        return mockMvc
            .perform(
                get(TARGET_API, stock.getCode(), board.getGroup(), post.getId(), comment.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(matcher)
            .andReturn();
    }

    private void assertResponse(CommentDataResponse result) {
        final CommentResponse commentResponse = result.getData();
        assertThat(commentResponse.getContent(), is(request.getContent()));
    }

    @Nested
    class WhenCreateReply {

        @Nested
        class WhenNormalUser {
            @BeforeEach
            void setUp() {
                user = itUtil.createUser();
                jwt = itUtil.createJwt(user.getId());
                post = itUtil.createPost(board, user.getId());
                comment = itUtil.createComment(post.getId(), user, CommentType.POST, Status.ACTIVE);
                request = genRequest();
            }

            @Nested
            class WhenErrorDoesNotHaveUserHoldingStock {
                @DisplayName("Should return 403 response code when call " + TARGET_API)
                @Test
                void shouldReturnError() throws Exception {
                    MvcResult response = callPostApiWithJwt(status().isForbidden(), jwt, request);

                    itUtil.assertErrorResponse(response, 403, "보유하고 있지 않은 주식입니다.");
                }
            }

            @Nested
            class WhenSuccess {
                @BeforeEach
                void setUp() {
                    itUtil.createUserHoldingStock(stock.getCode(), user);
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnSuccess() throws Exception {
                    MvcResult response = callPostApiWithJwt(status().isOk(), jwt, request);

                    final CommentDataResponse result = objectMapperUtil.toResponse(
                        response.getResponse().getContentAsString(),
                        CommentDataResponse.class
                    );

                    assertResponse(result);
                }
            }
        }

        @Nested
        class WhenAdminUserDoesNotHaveUserHoldingStock {
            @BeforeEach
            void setUp() {
                user = itUtil.createUser();
                jwt = itUtil.createJwt(user.getId());
                itUtil.createUserRole(user, RoleType.ADMIN);
                post = itUtil.createPost(board, user.getId());
                comment = itUtil.createComment(post.getId(), user, CommentType.POST, Status.ACTIVE);
                request = genRequest();
            }

            @Nested
            class WhenSuccess {
                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnSuccess() throws Exception {
                    MvcResult response = callPostApiWithJwt(status().isOk(), jwt, request);

                    final CommentDataResponse result = objectMapperUtil.toResponse(
                        response.getResponse().getContentAsString(),
                        CommentDataResponse.class
                    );

                    assertResponse(result);
                }
            }
        }
    }

    @Nested
    class WhenGetList {

        @Nested
        class WhenNormalUser {
            @BeforeEach
            void setUp() {
                user = itUtil.createUser();
                jwt = itUtil.createJwt(user.getId());
                post = itUtil.createPost(board, user.getId());
                comment = itUtil.createComment(post.getId(), user, CommentType.POST, Status.ACTIVE);
            }

            @Nested
            class WhenSuccess {
                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnSuccess() throws Exception {
                    MvcResult response = callGetApiWithJwt(status().isOk(), jwt);

                    final CommentPagingResponse result = objectMapperUtil.toResponse(
                        response.getResponse().getContentAsString(),
                        CommentPagingResponse.class
                    );
                    final List<CommentResponse> commentResponsesList = result.getData();
                    assertThat(commentResponsesList.size(), is(0));
                }
            }
        }

        @Nested
        class WhenAdminUserDoesNotHaveUserHoldingStock {
            @BeforeEach
            void setUp() {
                user = itUtil.createUser();
                jwt = itUtil.createJwt(user.getId());
                itUtil.createUserRole(user, RoleType.ADMIN);
                post = itUtil.createPost(board, user.getId());
                comment = itUtil.createComment(post.getId(), user, CommentType.POST, Status.ACTIVE);
            }

            @Nested
            class WhenSuccess {
                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnSuccess() throws Exception {
                    MvcResult response = callGetApiWithJwt(status().isOk(), jwt);

                    final CommentPagingResponse result = objectMapperUtil.toResponse(
                        response.getResponse().getContentAsString(),
                        CommentPagingResponse.class
                    );
                    final List<CommentResponse> commentResponsesList = result.getData();
                    assertThat(commentResponsesList.size(), is(0));
                }
            }
        }
    }
}
