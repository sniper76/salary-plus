package ag.act.api.stockboardgrouppost;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Solidarity;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.model.CreatePostRequest;
import ag.act.model.PostDetailsDataResponse;
import ag.act.model.PostDetailsResponse;
import ag.act.model.Status;
import ag.act.model.UserProfileResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import static ag.act.TestUtil.someBoardCategory;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class CreatePostOnAnalysisBoardApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts";
    private static final BoardGroup BOARD_GROUP = BoardGroup.ANALYSIS;

    private BoardCategory boardCategory;
    private User user;
    private String jwt;
    private CreatePostRequest request;
    private Stock stock;
    private Board board;

    @BeforeEach
    public void setUp() {
        itUtil.init();
        stock = itUtil.createStock();
        boardCategory = someBoardCategory(BOARD_GROUP);
        board = itUtil.createBoard(stock, BOARD_GROUP, boardCategory);
        request = generateRequest();
    }

    @Nested
    class WhenNormalUser {

        @BeforeEach
        void setUp() {
            user = itUtil.createUser();
            itUtil.createUserHoldingStock(stock.getCode(), user);
            jwt = itUtil.createJwt(user.getId());
        }

        @Test
        void shouldReturn400Response() throws Exception {
            callApiAndGetResult(status().isBadRequest());
        }
    }

    @Nested
    class WhenSolidarityLeaderUser {

        @BeforeEach
        void setUp() {
            user = itUtil.createUser();
            jwt = itUtil.createJwt(user.getId());
            itUtil.createUserHoldingStock(stock.getCode(), user);
            Solidarity solidarity = itUtil.createSolidarity(stock.getCode());
            itUtil.createSolidarityLeader(solidarity, user.getId());
        }

        @Test
        void shouldReturnSuccess() throws Exception {
            PostDetailsDataResponse result = callApiAndGetResult(status().isOk());

            assertResponse(result.getData());
        }

        private void assertResponse(PostDetailsResponse response) {
            assertThat(response.getId(), is(notNullValue()));
            assertThat(response.getTitle(), is(request.getTitle()));
            assertThat(response.getContent(), is(request.getContent()));
            assertThat(response.getBoardId(), is(board.getId()));
            assertThat(response.getPostImageList(), is(empty()));
            assertThat(response.getStatus(), is(Status.ACTIVE));

            final UserProfileResponse userProfile = response.getUserProfile();
            assertThat(userProfile.getNickname(), is(user.getNickname()));
        }

    }

    @Nested
    class WhenAcceptorUser {

        @BeforeEach
        void setUp() {
            user = itUtil.createAcceptorUser();
            itUtil.createUserHoldingStock(stock.getCode(), user);
            jwt = itUtil.createJwt(user.getId());
        }

        @Test
        void shouldReturn400Response() throws Exception {
            callApiAndGetResult(status().isBadRequest());
        }
    }

    private CreatePostRequest generateRequest() {
        CreatePostRequest request = new CreatePostRequest();
        request.setBoardCategory(boardCategory.name());
        request.setContent(someAlphanumericString(20));
        request.setTitle(someString(10));
        request.isAnonymous(Boolean.FALSE);
        return request;
    }

    private PostDetailsDataResponse callApiAndGetResult(ResultMatcher resultMatcher) throws Exception {
        MvcResult response = mockMvc
            .perform(
                post(TARGET_API, stock.getCode(), board.getGroup().name())
                    .content(objectMapperUtil.toRequestBody(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(resultMatcher)
            .andReturn();

        return objectMapperUtil.toResponse(response.getResponse().getContentAsString(), PostDetailsDataResponse.class);
    }
}
