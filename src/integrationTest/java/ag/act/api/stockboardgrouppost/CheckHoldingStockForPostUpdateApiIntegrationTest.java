package ag.act.api.stockboardgrouppost;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.BoardGroup;
import ag.act.model.UpdatePostRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class CheckHoldingStockForPostUpdateApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}";

    private UpdatePostRequest request;
    private String jwt;
    private Stock stock;
    private Board board;
    private User user;
    private Post post;

    @BeforeEach
    void setUp() {
        stock = itUtil.createStock();
    }

    private UpdatePostRequest genRequest() {
        UpdatePostRequest request = new UpdatePostRequest();
        request.setBoardCategory(board.getCategory().name());
        request.setTitle(someString(10));
        request.setContent(someAlphanumericString(300));
        request.setIsActive(Boolean.TRUE);
        request.setIsAnonymous(Boolean.FALSE);
        request.setIsExclusiveToHolders(Boolean.FALSE);

        return request;
    }

    private MvcResult callUpdateApiWithJwt(ResultMatcher matcher, String jwt, UpdatePostRequest request) throws Exception {
        return mockMvc
            .perform(
                patch(TARGET_API, stock.getCode(), board.getGroup(), post.getId())
                    .content(objectMapperUtil.toRequestBody(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(matcher)
            .andReturn();
    }

    private MvcResult callDeleteApiWithJwt(ResultMatcher matcher, String jwt) throws Exception {
        return mockMvc
            .perform(
                delete(TARGET_API, stock.getCode(), board.getGroup(), post.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(matcher)
            .andReturn();
    }

    @Nested
    class WhenUpdatePost {

        @BeforeEach
        void setUp() {
            user = itUtil.createUser();
            jwt = itUtil.createJwt(user.getId());
            final BoardGroup boardGroup = someEnum(BoardGroup.class);
            board = itUtil.createBoard(stock, boardGroup, boardGroup.getCategories().get(0));
            post = itUtil.createPost(board, user.getId(), Boolean.FALSE);
            request = genRequest();
        }

        @Nested
        class WhenErrorDoesNotHaveUserHoldingStock {

            @DisplayName("Should return 403 response code when call " + TARGET_API)
            @Test
            void shouldReturnError() throws Exception {
                MvcResult response = callUpdateApiWithJwt(status().isForbidden(), jwt, request);

                itUtil.assertErrorResponse(response, 403, "주주가 아니라 글 수정이 불가능합니다.");
            }
        }
    }

    @Nested
    class WhenDeletePost {

        @BeforeEach
        void setUp() {
            user = itUtil.createUser();
            jwt = itUtil.createJwt(user.getId());
            final BoardGroup boardGroup = someEnum(BoardGroup.class);
            board = itUtil.createBoard(stock, boardGroup, boardGroup.getCategories().get(0));
            post = itUtil.createPost(board, user.getId());
        }

        @Nested
        class WhenSuccess {

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                callDeleteApiWithJwt(status().isOk(), jwt);
            }
        }
    }
}
