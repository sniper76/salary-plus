package ag.act.api.stockboardgrouppost.comment;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Comment;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.CommentType;
import ag.act.model.Status;
import ag.act.service.stockboardgrouppost.comment.CommentUserLikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LikeAndUnlikeCommentApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}/comments/{commentId}/likes";

    @Autowired
    private CommentUserLikeService commentUserLikeService;
    private String jwt;
    private Stock stock;
    private Board board;
    private User currentUser;
    private Post post;
    private Comment comment;

    @BeforeEach
    void setUp() {
        itUtil.init();
        currentUser = itUtil.createUser();
        jwt = itUtil.createJwt(currentUser.getId());

        stock = itUtil.createStock();
        board = itUtil.createBoard(stock);
        post = itUtil.createPost(board, currentUser.getId());
        comment = itUtil.createComment(post.getId(), currentUser.getId(), null, CommentType.POST, Status.ACTIVE);
    }

    @Order(1)
    @DisplayName("Should return 200 response code when call " + TARGET_API)
    @Test
    void shouldLikeReturnSuccess() throws Exception {
        MvcResult response = mockMvc
            .perform(
                post(TARGET_API, stock.getCode(), board.getGroup(), post.getId(), comment.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(status().isOk())
            .andReturn();

        itUtil.assertSimpleOkay(response);
        assertThat(existCommentUserLike(post.getId(), currentUser.getId(), comment.getId()), is(true));
    }

    @Order(2)
    @DisplayName("Should return 200 response code when call " + TARGET_API)
    @Test
    void shouldUnLikeReturnSuccess() throws Exception {
        MvcResult response = mockMvc
            .perform(
                delete(TARGET_API, stock.getCode(), board.getGroup(), post.getId(), comment.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(status().isOk())
            .andReturn();

        itUtil.assertSimpleOkay(response);
        assertThat(existCommentUserLike(post.getId(), currentUser.getId(), comment.getId()), is(false));
    }

    private boolean existCommentUserLike(Long postId, Long userId, Long commentId) {
        return commentUserLikeService.findCommentUserLike(postId, userId, commentId).isPresent();
    }
}
