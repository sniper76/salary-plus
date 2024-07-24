package ag.act.api.stockboardgrouppost;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.converter.DateTimeConverter;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.model.GetBoardGroupPostResponse;
import ag.act.model.PostResponse;
import ag.act.util.DateTimeUtil;
import ag.act.util.KoreanDateTimeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static ag.act.TestUtil.toMultiValueMap;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetBoardGroupPostsByGlobalEventApiIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts";
    private static final String DATE1 = "2023-12-01 15:30:00";
    private static final String DATE2 = "2023-12-02 15:30:00";
    private static final String DATE3 = "2023-12-03 15:30:00";
    private static final String DATE4 = "2023-12-04 15:30:00";
    private static final String DATE5 = "2023-12-05 15:30:00";
    private static final LocalDate APP_RENEWAL_LOCALDATE = LocalDate.parse("2023-08-25");

    private String jwt;
    private Stock stock;
    private Board board;

    @BeforeEach
    void setUp() {
        itUtil.init();
        mockAppRenewalDate();
        jwt = itUtil.createJwt(itUtil.createUser().getId());
        stock = itUtil.createStock();

        final BoardGroup boardGroup = BoardGroup.GLOBALEVENT;
        List<BoardCategory> boardCategories = boardGroup.getCategories();
        final BoardCategory boardCategory = boardCategories.get(0);

        board = itUtil.createBoard(stock, boardGroup, boardCategory);
    }

    private void mockAppRenewalDate() {
        given(appRenewalDateProvider.get()).willReturn(APP_RENEWAL_LOCALDATE);
    }

    private LocalDateTime generateCreatedAt(String dateTimeString) {
        return DateTimeUtil.parseLocalDateTime(dateTimeString, "yyyy-MM-dd HH:mm:ss");
    }

    private MvcResult callApi(Map<String, Object> params, ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                get(TARGET_API, stock.getCode(), board.getGroup())
                    .params(toMultiValueMap(params))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    @Nested
    class WhenError {

        @Nested
        class WhenBoardCategoryEventAndNotice {
            @Test
            void shouldReturnBadRequest() throws Exception {
                final Map<String, Object> paramMap = Map.of("boardCategories", "EVENT,NOTICE");
                final MvcResult mvcResult = callApi(paramMap, status().isBadRequest());
                itUtil.assertErrorResponse(mvcResult, 400, "[EVENT, NOTICE] 카테고리 조합은 조회가 불가능합니다.");
            }
        }

        @Nested
        class WhenBoardCategoryCampaignAndNotice {
            @Test
            void shouldReturnBadRequest() throws Exception {
                final Map<String, Object> paramMap = Map.of("boardCategories", "CAMPAIGN,NOTICE");
                final MvcResult mvcResult = callApi(paramMap, status().isBadRequest());
                itUtil.assertErrorResponse(mvcResult, 400, "[CAMPAIGN, NOTICE] 카테고리 조합은 조회가 불가능합니다.");
            }
        }

        @Nested
        class WhenBothBoardCategoryAndBoardCategories {
            @Test
            void shouldReturnBadRequest() throws Exception {
                final Map<String, Object> paramMap = Map.of("boardCategory", "EVENT", "boardCategories", "EVENT,CAMPAIGN");
                final MvcResult mvcResult = callApi(paramMap, status().isBadRequest());
                itUtil.assertErrorResponse(mvcResult, 400, "boardCategory 와 boardCategories 는 동시에 사용할 수 없습니다.");
            }
        }
    }

    @Nested
    class WhenSuccess {
        private User user;
        private Board campaignBoard;
        private Board eventBoard;

        @BeforeEach
        void setUp() {
            board.setCategory(BoardCategory.EVENT);
            eventBoard = itUtil.updateBoard(board);

            campaignBoard = itUtil.createBoard(stock, board.getGroup(), BoardCategory.CAMPAIGN);

            user = itUtil.createUser();
            itUtil.createSolidarity(stock.getCode());
            itUtil.createUserHoldingStock(stock.getCode(), user);

            final Post post1 = itUtil.createPost(eventBoard, user.getId());
            final LocalDateTime createdAt1 = generateCreatedAt(DATE1);
            post1.setCreatedAt(createdAt1);
            post1.setActiveStartDate(createdAt1);
            post1.setLikeCount(2L);
            post1.setViewCount(30L);
            post1.setViewUserCount(10L);
            post1.setIsPinned(true); // 제일 먼저 나와야 함
            itUtil.updatePost(post1);

            final Post campaignPost1 = itUtil.createPost(campaignBoard, user.getId());
            final LocalDateTime campaignCreatedAt1 = generateCreatedAt(DATE5);
            campaignPost1.setCreatedAt(campaignCreatedAt1);
            campaignPost1.setActiveStartDate(campaignCreatedAt1);
            campaignPost1.setLikeCount(3L);
            campaignPost1.setViewCount(10L);
            campaignPost1.setViewUserCount(20L);
            itUtil.updatePost(campaignPost1);

            final Post post2 = itUtil.createPost(eventBoard, user.getId());
            final LocalDateTime createdAt2 = generateCreatedAt(DATE4);
            post2.setCreatedAt(createdAt2);
            post2.setActiveStartDate(createdAt2);
            post2.setLikeCount(1L);
            post2.setViewCount(20L);
            post2.setViewUserCount(30L);
            itUtil.updatePost(post2);

            final Post campaignPost2 = itUtil.createPost(campaignBoard, user.getId());
            final LocalDateTime campaignCreatedAt2 = generateCreatedAt(DATE3);
            campaignPost2.setCreatedAt(campaignCreatedAt2);
            campaignPost2.setActiveStartDate(campaignCreatedAt2);
            campaignPost2.setLikeCount(3L);
            campaignPost2.setViewCount(10L);
            campaignPost2.setViewUserCount(20L);
            itUtil.updatePost(campaignPost2);

            final Post post3 = itUtil.createPost(eventBoard, user.getId());
            final LocalDateTime createdAt3 = generateCreatedAt(DATE2);
            post3.setCreatedAt(createdAt3);
            post3.setActiveStartDate(createdAt3);
            post3.setLikeCount(1L);
            post3.setViewCount(20L);
            post3.setViewUserCount(30L);
            itUtil.updatePost(post3);

            final Post campaignPostFuture = itUtil.createPost(campaignBoard, user.getId());
            final LocalDateTime campaignCreatedAtFuture = KoreanDateTimeUtil.getNowInKoreanTime().plusDays(1).toLocalDateTime();
            campaignPostFuture.setCreatedAt(campaignCreatedAtFuture);
            campaignPostFuture.setActiveStartDate(campaignCreatedAtFuture);
            campaignPostFuture.setLikeCount(3L);
            campaignPostFuture.setViewCount(10L);
            campaignPostFuture.setViewUserCount(20L);
            itUtil.updatePost(campaignPostFuture);
        }

        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult response = callApi(Map.of("boardCategories", "EVENT,CAMPAIGN"), status().isOk());

            final GetBoardGroupPostResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                GetBoardGroupPostResponse.class
            );

            assertPostsSortedByCreatedAtDesc(result);
        }
    }

    private void assertPostsSortedByCreatedAtDesc(GetBoardGroupPostResponse result) {
        List<PostResponse> postResponses = result.getData();
        assertThat(postResponses.size(), is(5));
        assertCreatedAtAndActiveStartDate(postResponses.get(0), DATE1);
        assertCreatedAtAndActiveStartDate(postResponses.get(1), DATE5);
        assertCreatedAtAndActiveStartDate(postResponses.get(2), DATE4);
        assertCreatedAtAndActiveStartDate(postResponses.get(3), DATE3);
        assertCreatedAtAndActiveStartDate(postResponses.get(4), DATE2);
        assertThat(result.getPaging().getSorts().size(), is(1));
        itUtil.assertSort(result.getPaging().getSorts().get(0), CREATED_AT_DESC);
    }

    private void assertCreatedAtAndActiveStartDate(PostResponse postResponse, String date) {
        assertThat(postResponse.getCreatedAt(), is(getInstant(date)));
        assertThat(postResponse.getActiveStartDate(), is(getInstant(date)));
    }

    private Instant getInstant(String date) {
        return DateTimeConverter.convert(DateTimeUtil.parseLocalDateTime(date, "yyyy-MM-dd HH:mm:ss"));
    }
}
