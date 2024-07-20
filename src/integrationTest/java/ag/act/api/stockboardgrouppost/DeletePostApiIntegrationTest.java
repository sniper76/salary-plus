package ag.act.api.stockboardgrouppost;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.model.SimpleStringResponse;
import ag.act.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DeletePostApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}";

    private String jwt;
    private Stock stock;
    private Board board;
    private Post post;
    private User user;

    @BeforeEach
    void setUp() {
        itUtil.init();
        user = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());
        stock = itUtil.createStock();
        board = itUtil.createBoard(stock);
        itUtil.createSolidarity(stock.getCode());
        itUtil.createUserHoldingStock(stock.getCode(), user);
        post = itUtil.createPost(board, user.getId());
    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                delete(TARGET_API, stock.getCode(), board.getGroup(), post.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    @Nested
    class WhenDeletePostSuccess {

        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult response = callApi(status().isOk());

            final SimpleStringResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                SimpleStringResponse.class
            );

            assertResponse(result);
        }

        private void assertResponse(SimpleStringResponse result) {
            assertThat(result.getStatus(), is("ok"));

            final Optional<Post> postOptional = itUtil.findPost(post.getId());
            assertThat(postOptional.isPresent(), is(true));
            assertThat(postOptional.get().getDeletedAt(), notNullValue());
            assertThat(postOptional.get().getStatus(), anyOf(is(Status.DELETED_BY_USER), is(Status.DELETED_BY_ADMIN)));
        }
    }

    @Nested
    class WhenTryToDeletePostThatIsAlreadyDeleted {

        @BeforeEach
        void setUp() {
            post.setStatus(Status.DELETED_BY_USER);
            post.setDeletedAt(LocalDateTime.now());
            itUtil.updatePost(post);
        }

        @Test
        void shouldReturn404() throws Exception {
            MvcResult response = callApi(status().isNotFound());

            itUtil.assertErrorResponse(response, 404, "이미 삭제된 게시글입니다.");
        }
    }

    @Nested
    class WhenBoardCategoryIsHolderListReadAndCopy {
        private BoardCategory boardCategory = BoardCategory.HOLDER_LIST_READ_AND_COPY;

        @BeforeEach
        void setUp() {
            board = itUtil.createBoard(stock, BoardGroup.ANALYSIS, boardCategory);
            post = itUtil.createPost(board, user.getId());
        }

        @Test
        void shouldReturn400() throws Exception {
            MvcResult response = callApi(status().isBadRequest());

            itUtil.assertErrorResponse(response, 400, "%s 카테고리는 삭제할 수 없습니다.".formatted(boardCategory.getDisplayName()));
        }
    }
}
