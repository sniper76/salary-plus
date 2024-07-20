package ag.act.api.stockboardgrouppost;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.TestUtil;
import ag.act.constants.MessageConstants;
import ag.act.entity.Board;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.model.CreatePostRequest;
import ag.act.model.PostDetailsDataResponse;
import ag.act.model.PostDetailsResponse;
import ag.act.parser.DateTimeParser;
import ag.act.util.DateTimeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;
import static shiver.me.timbers.data.random.RandomThings.someThing;

class CheckDateMaxAnonymousCountApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts";
    private static final String TARGET_API_COMMENT = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}/comments";
    public static final int MAX_ANONYMOUS_POST_CREATIONS = 3;

    private String jwt;
    private Stock stock;
    private Board board;
    private User user;

    @BeforeEach
    void setUp() {
        itUtil.init();
        user = itUtil.createUserBeforePinRegistered();
        jwt = itUtil.createJwt(user.getId());
        stock = itUtil.createStock();
        board = itUtil.createBoard(stock, BoardGroup.DEBATE, BoardCategory.DEBATE);
        itUtil.createSolidarity(stock.getCode());
        itUtil.createUserHoldingStock(stock.getCode(), user);
    }

    @Nested
    class WhenCreatePostToday {

        private CreatePostRequest request;

        @BeforeEach
        void setUp() {
            request = genRequest();
        }

        @Nested
        class WhenUserAnonymousLimitExceededToday {
            @BeforeEach
            void setUp() {
                String writeDate = DateTimeUtil.getFormattedCurrentTimeInKorean("yyyyMMdd");
                itUtil.createUserAnonymousCount(user.getId(), writeDate, 0, MAX_ANONYMOUS_POST_CREATIONS);
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequest() throws Exception {
                MvcResult response = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(response, 400, "익명 게시글 일일 작성횟수를 초과하였습니다.");
            }
        }

        @Nested
        class WhenUserAnonymousLimitExceededPastDay {
            @BeforeEach
            void setUp() {
                String writeDate = DateTimeParser.toDate(TestUtil.someLocalDateTimeInThePastDaysBetween(5, 15));
                itUtil.createUserAnonymousCount(user.getId(), writeDate, 0, MAX_ANONYMOUS_POST_CREATIONS);
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                PostDetailsDataResponse result = itUtil.getResult(callApi(status().isOk()), PostDetailsDataResponse.class);

                assertResponse(result);
            }
        }

        private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
            return mockMvc
                .perform(
                    post(TARGET_API, stock.getCode(), board.getGroup())
                        .content(objectMapperUtil.toRequestBody(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(resultMatcher)
                .andReturn();
        }

        private void assertResponse(PostDetailsDataResponse result) {
            final PostDetailsResponse createUpdateResponse = result.getData();
            assertThat(createUpdateResponse.getUserProfile().getNickname(), is(MessageConstants.ANONYMOUS_NAME));
        }

        private CreatePostRequest genRequest() {
            CreatePostRequest request = new CreatePostRequest();
            request.setBoardCategory(board.getCategory().name());
            request.setTitle(someString(10));
            request.setContent(someAlphanumericString(300));
            request.setIsAnonymous(Boolean.TRUE);
            request.setIsActive(someThing(Boolean.TRUE, Boolean.FALSE, null, null));

            return request;
        }
    }
}
