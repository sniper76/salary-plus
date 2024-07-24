package ag.act.api.stockboardgrouppost;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomBooleans.someBoolean;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class UpdatePostChangeCategoryApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}";

    private ag.act.model.UpdatePostRequest request;
    private String jwt;
    private Stock stock;
    private Board board;
    private Long postId;
    private User user;
    private Post post;

    @BeforeEach
    void setUp() {
        itUtil.init();
        user = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());
        stock = itUtil.createStock();
        board = itUtil.createBoard(stock, BoardGroup.ANALYSIS, BoardCategory.DAILY_ACT);
        post = itUtil.createPost(board, user.getId());
        post.setIsAnonymous(false);
        post = itUtil.createPoll(post);
        postId = post.getId();

        itUtil.createSolidarity(stock.getCode());
        itUtil.createUserHoldingStock(stock.getCode(), user);
    }

    private ag.act.model.UpdatePostRequest genRequest(String categoryName) {
        ag.act.model.UpdatePostRequest request = new ag.act.model.UpdatePostRequest();
        request.setBoardCategory(categoryName);
        request.setTitle(someString(10));
        request.setContent(someAlphanumericString(300));
        request.setIsAnonymous(Boolean.FALSE);
        request.setIsActive(someBoolean());

        return request;
    }

    @Nested
    class WhenUpdatePostChangeBoardCategory {

        @BeforeEach
        void setUp() {
            itUtil.createBoard(stock, BoardGroup.ANALYSIS, BoardCategory.SOLIDARITY_LEADER_LETTERS);

            request = genRequest(BoardCategory.SOLIDARITY_LEADER_LETTERS.name());
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final MvcResult response = callApi(status().isOk());

            final ag.act.model.PostDetailsDataResponse result = itUtil.getResult(response, ag.act.model.PostDetailsDataResponse.class);

            assertResponse(result);
        }

        private void assertResponse(ag.act.model.PostDetailsDataResponse result) {
            final ag.act.model.PostDetailsResponse createUpdateResponse = result.getData();
            final Board board = itUtil.findBoardByStockCodeAndCategory(stock.getCode(), BoardCategory.SOLIDARITY_LEADER_LETTERS)
                .orElseThrow(() -> new RuntimeException("[TEST] 게시판이 존재하지 않습니다."));

            assertThat(createUpdateResponse.getBoardId(), is(board.getId()));
        }
    }

    @Nested
    class WhenFailToUpdatePostChangeBoardCategory {

        @BeforeEach
        void setUp() {
            request = genRequest(BoardCategory.DIGITAL_DELEGATION.name());
        }

        @DisplayName("Should return 400 response code when call " + TARGET_API)
        @Test
        void shouldErrorSuccess() throws Exception {
            final MvcResult response = callApi(status().isBadRequest());

            itUtil.assertErrorResponse(response, 400, "동일 그룹내의 게시물만 카테고리 이동이 가능합니다.");
        }
    }

    @Nested
    class WhenBoardCategoryIsHolderListReadAndCopy {
        private BoardCategory boardCategory = BoardCategory.HOLDER_LIST_READ_AND_COPY;

        @BeforeEach
        void setUp() {
            board = itUtil.createBoard(stock, BoardGroup.ANALYSIS, boardCategory);
            post = itUtil.createPost(board, user.getId());
            request = genRequest(boardCategory.name());
        }

        @Test
        void shouldReturn400() throws Exception {
            MvcResult response = callApi(status().isBadRequest());

            itUtil.assertErrorResponse(response, 400, "%s 카테고리는 수정할 수 없습니다.".formatted(boardCategory.getDisplayName()));
        }
    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                patch(TARGET_API, stock.getCode(), board.getGroup(), postId)
                    .content(objectMapperUtil.toRequestBody(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(resultMatcher)
            .andReturn();
    }
}
