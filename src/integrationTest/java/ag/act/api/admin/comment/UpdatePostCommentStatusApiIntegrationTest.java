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
import ag.act.model.CommentUpdateStatusRequest;
import ag.act.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.stream.Stream;

import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("checkstyle:LineLength")
class UpdatePostCommentStatusApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}/comments/{commentId}/status";

    private String adminJwt;
    private Stock stock;
    private Board board;
    private Post post;
    private User user;
    private Comment comment;
    private CommentUpdateStatusRequest request;

    @BeforeEach
    void setUp() {
        final User adminUser = itUtil.createAdminUser();
        adminJwt = itUtil.createJwt(adminUser.getId());

        user = itUtil.createUser();
        stock = itUtil.createStock();
        board = itUtil.createBoard(stock);
        itUtil.createSolidarity(stock.getCode());
        itUtil.createUserHoldingStock(stock.getCode(), user);

        post = itUtil.createPost(board, user.getId());
        itUtil.createPost(board, user.getId());
        comment = itUtil.createComment(post.getId(), user, CommentType.POST, Status.ACTIVE);
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
        private static Stream<Arguments> valueProvider() {
            return Stream.of(
                Arguments.of(Status.DELETED),
                Arguments.of(Status.DELETED_BY_USER),
                Arguments.of(Status.INACTIVE_BY_ADMIN),
                Arguments.of(Status.INACTIVE_BY_USER),
                Arguments.of(Status.PROCESSING),
                Arguments.of(Status.WITHDRAWAL_REQUESTED)
            );
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @ParameterizedTest(name = "{index} => status=''{0}''")
        @MethodSource("valueProvider")
        void shouldReturn(Status status) throws Exception {
            request = new CommentUpdateStatusRequest()
                .status(status.name());

            final MvcResult response = callApi(status().isBadRequest());

            itUtil.assertErrorResponse(response, BAD_REQUEST_STATUS, "삭제/삭제취소 상태만 수정 가능합니다.");
        }
    }

    @Nested
    class WhenSuccess {

        @Nested
        class WhenCommentActiveToDeletedAdmin {
            @BeforeEach
            void setUp() {
                request = new CommentUpdateStatusRequest()
                    .status(Status.DELETED_BY_ADMIN.name());
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
                assertThat(commentResponse.getUserId(), is(user.getId()));
                assertThat(commentResponse.getContent(), is("관리자에 의해 삭제된 댓글입니다."));
                assertThat(commentResponse.getStatus(), is(request.getStatus()));
            }
        }

        @Nested
        class WhenCommentDeletedAdminToActive {
            @BeforeEach
            void setUp() {
                comment.setStatus(Status.DELETED_BY_ADMIN);
                comment = itUtil.updateComment(comment);

                request = new CommentUpdateStatusRequest()
                    .status(Status.ACTIVE.name());
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
                assertThat(commentResponse.getUserId(), is(user.getId()));
                assertThat(commentResponse.getContent(), is(comment.getContent()));
                assertThat(commentResponse.getStatus(), is(request.getStatus()));
            }
        }
    }
}
