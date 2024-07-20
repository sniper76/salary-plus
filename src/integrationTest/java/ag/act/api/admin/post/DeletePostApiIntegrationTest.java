package ag.act.api.admin.post;


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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DeletePostApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/stocks/{stockCode}/board-groups/{boardGroup}/posts/{postId}";

    private String stockCode;
    private String adminJwt;
    private Board board;
    private Long postId;
    private User adminUser;
    private Stock stock;

    @BeforeEach
    void setUp() {
        itUtil.init();
        adminUser = itUtil.createAdminUser();
        adminJwt = itUtil.createJwt(adminUser.getId());

        stockCode = someStockCode();
        stock = itUtil.createStock(stockCode);
        board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.DIGITAL_DELEGATION);
    }

    @Nested
    class WhenAdminDeleteUserPost {

        @Nested
        class AndAuthorAdmin {

            @BeforeEach
            void setUp() {
                User user = itUtil.createAdminUser();
                Post post = itUtil.createPost(board, user.getId());
                postId = post.getId();
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                MvcResult response = callApi(status().isOk());

                final SimpleStringResponse result = objectMapperUtil.toResponse(
                    response.getResponse().getContentAsString(),
                    SimpleStringResponse.class
                );

                assertThat(result.getStatus(), is("ok"));
                assertDocumentResponse(Status.DELETED);
            }
        }

        @Nested
        class AndAuthorNotAdmin {

            @BeforeEach
            void setUp() {
                User user = itUtil.createUser();
                Post post = itUtil.createPost(board, user.getId());
                postId = post.getId();
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                MvcResult response = callApi(status().isOk());

                final SimpleStringResponse result = objectMapperUtil.toResponse(
                    response.getResponse().getContentAsString(),
                    SimpleStringResponse.class
                );

                assertThat(result.getStatus(), is("ok"));
                assertDocumentResponse(Status.DELETED_BY_ADMIN);
            }
        }
    }

    @Nested
    class WhenAdminDeleteAdminPost {

        @BeforeEach
        void setUp() {
            Post post = itUtil.createPost(board, adminUser.getId());
            postId = post.getId();
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult response = callApi(status().isOk());

            final SimpleStringResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                SimpleStringResponse.class
            );

            assertThat(result.getStatus(), is("ok"));
            assertDocumentResponse(Status.DELETED);
        }
    }

    @Nested
    class WhenBoardCategoryIsLeaderElection {

        @BeforeEach
        void setUp() {
            board = itUtil.createBoard(stock, BoardGroup.ANALYSIS, BoardCategory.SOLIDARITY_LEADER_ELECTION);
            Post post = itUtil.createPost(board, adminUser.getId());
            postId = post.getId();
        }

        @Test
        void shouldError() throws Exception {
            MvcResult response = callApi(status().isBadRequest());
            itUtil.assertErrorResponse(response, 400, "대표선출 카테고리는 삭제할 수 없습니다.");
        }
    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                delete(TARGET_API, stockCode, board.getGroup().name(), postId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + adminJwt)
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    private void assertDocumentResponse(Status status) {
        final Post afterPost = itUtil.findPostNoneNull(postId);

        assertThat(afterPost.getBoardId(), is(board.getId()));
        assertThat(afterPost.getStatus(), is(status));
    }
}
