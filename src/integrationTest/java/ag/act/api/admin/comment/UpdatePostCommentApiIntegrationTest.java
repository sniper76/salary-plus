package ag.act.api.admin.comment;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Comment;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.CommentType;
import ag.act.model.CommentDataResponse;
import ag.act.model.CommentResponse;
import ag.act.model.Status;
import ag.act.model.UpdateCommentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import static ag.act.TestUtil.TestHtmlContent;
import static ag.act.TestUtil.someHtmlContent;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("checkstyle:LineLength")
class UpdatePostCommentApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}/comments/{commentId}";

    private String adminJwt;
    private Stock stock;
    private Board board;
    private Post post;
    private User user;
    private Comment comment;
    private UpdateCommentRequest request;
    private User adminUser;
    private TestHtmlContent testHtmlContent;

    @BeforeEach
    void setUp() {
        adminUser = itUtil.createAdminUser();
        adminJwt = itUtil.createJwt(adminUser.getId());

        user = itUtil.createUser();
        stock = itUtil.createStock();
        board = itUtil.createBoard(stock);
        itUtil.createSolidarity(stock.getCode());
        itUtil.createUserHoldingStock(stock.getCode(), user);

        post = itUtil.createPost(board, user.getId());
        itUtil.createPost(board, user.getId());

        comment = itUtil.createComment(post.getId(), adminUser, CommentType.POST, Status.ACTIVE);
    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                patch(TARGET_API, stock.getCode(), board.getGroup(), post.getId(), comment.getId())
                    .content(objectMapperUtil.toRequestBody(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(adminJwt)))
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    @Nested
    class WhenError {
        @BeforeEach
        void setUp() {
            request = genRequest();

            comment.setUserId(user.getId());
            itUtil.updateComment(comment);
        }

        @Test
        void shouldReturn() throws Exception {
            final MvcResult response = callApi(status().isBadRequest());

            itUtil.assertErrorResponse(response, HttpStatus.BAD_REQUEST.value(), "관리자 권한으로 작성된 댓글/답글만 수정 가능합니다.");
        }

        @Nested
        class WhenUserIsAcceptor {
            private String acceptorJwt;

            @BeforeEach
            void setUp() {
                final User acceptorUser = itUtil.createAcceptorUser();
                itUtil.createDigitalDocument(post, stock, acceptorUser);

                request = genRequest();

                acceptorJwt = itUtil.createJwt(acceptorUser.getId());

                comment.setUserId(user.getId());
                itUtil.updateComment(comment);
            }

            @Test
            void shouldReturn() throws Exception {
                final MvcResult response = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(response, HttpStatus.BAD_REQUEST.value(), "관리자만 가능한 기능입니다.");
            }

            private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
                return mockMvc
                    .perform(
                        patch(TARGET_API, stock.getCode(), board.getGroup(), post.getId(), comment.getId())
                            .content(objectMapperUtil.toRequestBody(request))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .headers(headers(jwt(acceptorJwt)))
                    )
                    .andExpect(resultMatcher)
                    .andReturn();
            }
        }
    }

    @Nested
    class WhenSuccess {
        @BeforeEach
        void setUp() {
            request = genRequest();
        }

        @Test
        void shouldReturnSuccess() throws Exception {
            final MvcResult mvcResult = callApi(status().isOk());

            final CommentDataResponse response = objectMapperUtil.toResponse(
                mvcResult.getResponse().getContentAsString(),
                CommentDataResponse.class
            );

            assertResponse(response);
        }

        private void assertResponse(CommentDataResponse result) {
            final CommentResponse commentResponse = result.getData();

            assertThat(commentResponse.getId(), is(comment.getId()));
            assertThat(commentResponse.getUserId(), is(adminUser.getId()));
            assertThat(commentResponse.getContent(), is(testHtmlContent.expectedHtml()));
            assertThat(commentResponse.getStatus(), is(comment.getStatus().name()));
        }
    }

    private UpdateCommentRequest genRequest() {
        testHtmlContent = someHtmlContent(Boolean.FALSE);
        return new UpdateCommentRequest()
            .content(testHtmlContent.html());
    }
}
