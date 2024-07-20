package ag.act.api.admin.comment;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.constants.MessageConstants;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.CommentType;
import ag.act.model.CommentDataResponse;
import ag.act.model.CommentResponse;
import ag.act.model.CreateCommentRequest;
import ag.act.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class CreateCommentApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/stocks/{stock}/board-groups/{boardGroup}/posts/{postId}/comments";

    private Stock stock;
    private Board board;
    private User adminUser1;
    private User adminUser2;
    private Post post;

    @BeforeEach
    void setUp() {
        itUtil.init();
        adminUser1 = itUtil.createAdminUser();
        adminUser2 = itUtil.createAdminUser();

        stock = itUtil.createStock();
        board = itUtil.createBoard(stock);
        post = itUtil.createPost(board, adminUser1.getId(), someString(10), Boolean.FALSE);
        itUtil.createSolidarity(stock.getCode());
        itUtil.createUserHoldingStock(stock.getCode(), adminUser1);

        final User user1 = itUtil.createUser();
        itUtil.createComment(post.getId(), user1, CommentType.POST, Status.ACTIVE);
        final User user2 = itUtil.createUser();
        itUtil.createComment(post.getId(), user2, CommentType.POST, Status.ACTIVE);
        final User user3 = itUtil.createUser();
        itUtil.createComment(post.getId(), user3, CommentType.POST, Status.ACTIVE);
    }

    private CreateCommentRequest genRequest(Boolean isAnonymous) {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setContent(someAlphanumericString(300));
        request.setIsAnonymous(isAnonymous);

        return request;
    }

    @Nested
    class WhenIsAdminWriteMoreComments {

        @DisplayName("Should when different admin users make comments")
        @Test
        void shouldReturnSuccess() throws Exception {
            assertResponse(callApi(genRequest(false), adminUser1), adminUser1.getNickname());

            assertResponse(callApi(genRequest(true), adminUser1), MessageConstants.ANONYMOUS_NAME);
            assertResponse(callApi(genRequest(true), adminUser2), "%s%d".formatted(MessageConstants.ANONYMOUS_NAME, 1));
            assertResponse(callApi(genRequest(true), adminUser1), MessageConstants.ANONYMOUS_NAME);
            assertResponse(callApi(genRequest(true), adminUser2), "%s%d".formatted(MessageConstants.ANONYMOUS_NAME, 1));

            assertResponse(callApi(genRequest(true), adminUser1), MessageConstants.ANONYMOUS_NAME);
            assertResponse(callApi(genRequest(true), adminUser1), MessageConstants.ANONYMOUS_NAME);
            assertResponse(callApi(genRequest(true), adminUser1), MessageConstants.ANONYMOUS_NAME);
            assertResponse(callApi(genRequest(true), adminUser1), MessageConstants.ANONYMOUS_NAME);//admin 5 over
        }

        private CommentDataResponse callApi(CreateCommentRequest request, User loginUser) throws Exception {

            final String jwt = itUtil.createJwt(loginUser.getId());

            final MvcResult response = mockMvc
                .perform(
                    post(TARGET_API, stock.getCode(), board.getGroup(), post.getId())
                        .content(objectMapperUtil.toRequestBody(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isOk())
                .andReturn();

            return objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                CommentDataResponse.class
            );
        }

        private void assertResponse(CommentDataResponse result, String nickname) {
            final CommentResponse createUpdateResponse = result.getData();
            assertThat(createUpdateResponse.getUserProfile().getNickname(), is(nickname));
        }
    }
}
