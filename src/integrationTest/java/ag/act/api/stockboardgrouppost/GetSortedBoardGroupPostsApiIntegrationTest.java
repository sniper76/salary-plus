package ag.act.api.stockboardgrouppost;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.model.PostResponse;
import ag.act.util.DateTimeUtil;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static ag.act.TestUtil.toMultiValueMap;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetSortedBoardGroupPostsApiIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts";
    private static final String DATE1 = "2023-08-01";
    private static final String DATE2 = "2023-08-02";
    private static final String DATE3 = "2023-08-03";

    private String jwt;
    private Stock stock;
    private Board board;

    @BeforeEach
    void setUp() {
        itUtil.init();
        User user = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());
        stock = itUtil.createStock();
        board = itUtil.createBoard(stock);
        itUtil.createSolidarity(stock.getCode());
        itUtil.createUserHoldingStock(stock.getCode(), user);

        final Post post1 = itUtil.createPost(board, user.getId());
        final LocalDateTime createdAt1 = generateCreatedAt(DATE1 + " 15:30:00");
        post1.setCreatedAt(createdAt1);
        post1.setActiveStartDate(createdAt1);
        post1.setLikeCount(2L);
        post1.setViewCount(30L);
        post1.setViewUserCount(10L);
        post1.setIsPinned(true); // 제일 먼저 나와야 함
        itUtil.updatePost(post1);

        final Post post2 = itUtil.createPost(board, user.getId());
        final LocalDateTime createdAt2 = generateCreatedAt(DATE2 + " 15:30:00");
        post2.setCreatedAt(createdAt2);
        post2.setActiveStartDate(createdAt2);
        post2.setLikeCount(3L);
        post2.setViewCount(10L);
        post2.setViewUserCount(20L);
        itUtil.updatePost(post2);

        final Post post3 = itUtil.createPost(board, user.getId());
        final LocalDateTime createdAt3 = generateCreatedAt(DATE3 + " 15:30:00");
        post3.setCreatedAt(createdAt3);
        post3.setActiveStartDate(createdAt3);
        post3.setLikeCount(1L);
        post3.setViewCount(20L);
        post3.setViewUserCount(30L);
        itUtil.updatePost(post3);

    }

    @NotNull
    private static LocalDateTime generateCreatedAt(String dateTimeString) {
        return DateTimeUtil.parseLocalDateTime(dateTimeString, "yyyy-MM-dd HH:mm:ss");
    }

    private MvcResult callApi() throws Exception {
        return mockMvc
            .perform(
                get(TARGET_API, stock.getCode(), board.getGroup())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(status().isOk())
            .andReturn();
    }

    private MvcResult callApi(Map<String, Object> params) throws Exception {
        return mockMvc
            .perform(
                get(TARGET_API, stock.getCode(), board.getGroup())
                    .params(toMultiValueMap(params))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(status().isOk())
            .andReturn();
    }

    @Test
    void shouldReturnPostsSortedByDefaultCreatedAt() throws Exception {
        MvcResult response = callApi();

        final ag.act.model.GetBoardGroupPostResponse result = objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            ag.act.model.GetBoardGroupPostResponse.class
        );

        assertPostsSortedByCreatedAtDesc(result);
    }

    @Test
    void shouldReturnPostsSortedByCreatedAtDesc() throws Exception {
        MvcResult response = callApi();

        final ag.act.model.GetBoardGroupPostResponse result = objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            ag.act.model.GetBoardGroupPostResponse.class
        );

        assertPostsSortedByCreatedAtDesc(result);
    }

    private void assertPostsSortedByCreatedAtDesc(ag.act.model.GetBoardGroupPostResponse result) {
        List<PostResponse> postResponses = result.getData();
        assertThat(postResponses.size(), is(3));
        assertCreatedAtAndActiveStartDate(postResponses.get(0), DATE1);
        assertCreatedAtAndActiveStartDate(postResponses.get(1), DATE3);
        assertCreatedAtAndActiveStartDate(postResponses.get(2), DATE2);
        assertThat(result.getPaging().getSorts().size(), is(1));
        itUtil.assertSort(result.getPaging().getSorts().get(0), CREATED_AT_DESC);
    }

    private void assertCreatedAtAndActiveStartDate(PostResponse postResponse, String date) {
        assertThat(postResponse.getCreatedAt().toString(), startsWith(date));
        assertThat(postResponse.getActiveStartDate().toString(), startsWith(date));
    }

    @Test
    void shouldReturnPostsSortedByLikeCountDesc() throws Exception {
        MvcResult response = callApi(Map.of("sorts", "likeCount:DESC"));

        final ag.act.model.GetBoardGroupPostResponse result = objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            ag.act.model.GetBoardGroupPostResponse.class
        );

        assertPostsSortedByLikeCountDesc(result);
    }

    private void assertPostsSortedByLikeCountDesc(ag.act.model.GetBoardGroupPostResponse result) {
        List<PostResponse> postResponses = result.getData();
        assertThat(postResponses.size(), is(3));
        assertThat(postResponses.get(0).getLikeCount(), is(2L));
        assertThat(postResponses.get(1).getLikeCount(), is(3L));
        assertThat(postResponses.get(2).getLikeCount(), is(1L));
        assertThat(result.getPaging().getSorts().size(), is(1));
        assertThat(result.getPaging().getSorts().get(0), is("likeCount:DESC"));
    }

    @Test
    void shouldReturnPostsSortedByViewCountDesc() throws Exception {
        MvcResult response = callApi(Map.of("sorts", "viewCount:DESC"));

        final ag.act.model.GetBoardGroupPostResponse result = objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            ag.act.model.GetBoardGroupPostResponse.class
        );

        assertPostsSortedByViewCountDesc(result, "viewCount:DESC");
    }

    @Test
    void shouldReturnPostsSortedByViewUserCountDesc() throws Exception {
        // TODO 이 테스트는 임시로 viewUserCount로 정렬을 시도해도, 서버에서 viewCount로 변경해서 정렬하고 결과를 리턴해주는 것을 테스트한다.
        //      나중에 전체적으로 다시 viewUserCount를 사용하는 것으로 바꾸면 이 메서드는 삭제해야 합니다. 2023-12-18

        MvcResult response = callApi(Map.of("sorts", "viewUserCount:DESC"));

        final ag.act.model.GetBoardGroupPostResponse result = objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            ag.act.model.GetBoardGroupPostResponse.class
        );

        assertPostsSortedByViewCountDesc(result, "viewUserCount:DESC");
    }

    private void assertPostsSortedByViewCountDesc(ag.act.model.GetBoardGroupPostResponse result, String sort) {
        List<PostResponse> postResponses = result.getData();
        assertThat(postResponses.size(), is(3));
        assertThat(postResponses.get(0).getViewCount(), is(30L));
        assertThat(postResponses.get(1).getViewCount(), is(20L));
        assertThat(postResponses.get(2).getViewCount(), is(10L));
        assertThat(result.getPaging().getSorts().size(), is(1));
        itUtil.assertSort(result.getPaging().getSorts().get(0), sort);
    }
}
