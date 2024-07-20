package ag.act.api.stockboardgrouppost;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.core.configuration.GlobalBoardManager;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.model.Paging;
import ag.act.model.PostResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomBooleans.someBoolean;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;

class GetBoardGroupPostsByGlobalCommunityApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts";

    @Autowired
    private GlobalBoardManager globalBoardManager;
    private String jwt;
    private Stock stock;
    private Board freeDebateBoard;
    private List<Post> posts;
    private String stockCode;

    @BeforeEach
    void setUp() {
        dbCleaner.clean();
        itUtil.init();

        User user = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());
        stockCode = globalBoardManager.getStockCode();
        stock = itUtil.findStock(stockCode);
        freeDebateBoard = itUtil.findBoard(stockCode, BoardCategory.FREE_DEBATE).orElseThrow();

        posts = createSomePostsOnFreeDebateBoard(user);

        createSomePostsOnAnotherBoard(user);
    }

    private List<Post> createSomePostsOnFreeDebateBoard(User user) {
        return IntStream.range(0, someIntegerBetween(3, 9))
            .mapToObj(i -> createPost(user.getId(), someBoolean()))
            .sorted(Comparator
                .comparing(Post::getIsPinned) // isPinned=true first
                .thenComparing(Post::getCreatedAt).reversed() // createdAt desc
            )
            .toList();
    }

    private Post createPost(Long userId, Boolean isPinned) {
        return itUtil.createPost(freeDebateBoard, userId, Boolean.FALSE, isPinned);
    }

    private void createSomePostsOnAnotherBoard(User user) {
        Stock otherStock = itUtil.createStock(someStockCode());
        Board board = itUtil.createBoard(otherStock, BoardGroup.ANALYSIS, BoardCategory.DAILY_ACT);
        itUtil.createSolidarity(otherStock.getCode());
        itUtil.createUserHoldingStock(otherStock.getCode(), user);

        itUtil.createPost(board, user.getId());
        itUtil.createPost(board, user.getId());
    }

    private void assertResponse(ag.act.model.GetBoardGroupPostResponse result) {
        final Paging paging = result.getPaging();
        final List<PostResponse> postResponses = result.getData();

        assertPaging(paging);
        assertPosts(postResponses);
    }

    private void assertPosts(List<PostResponse> postResponses) {
        assertThat(postResponses.size(), is(posts.size()));

        for (int i = 0; i < postResponses.size(); i++) {
            final PostResponse postResponse = postResponses.get(i);
            final Post post = posts.get(i);

            assertThat(postResponse.getId(), is(post.getId()));
            assertThat(postResponse.getStock().getCode(), is(stock.getCode()));
            assertThat(postResponse.getBoardId(), is(freeDebateBoard.getId()));
            assertThat(postResponse.getTitle(), is(post.getTitle()));
            assertThat(postResponse.getStatus(), is(post.getStatus()));
            assertThat(postResponse.getReported(), is(false));
            assertThat(postResponse.getDeleted(), is(false));
            assertThat(postResponse.getIsAuthorAdmin(), is(false));
        }
    }

    private void assertPaging(Paging paging) {
        assertThat(paging.getPage(), is(1));
        assertThat(paging.getTotalPages(), is(1));
        assertThat(paging.getTotalElements(), is((long) posts.size()));
        assertThat(paging.getSorts().size(), is(1));
        itUtil.assertSort(paging.getSorts().get(0), CREATED_AT_DESC);
    }

    @Nested
    class WhenGetPostsInFreeDebateBoardGroup {

        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    get(TARGET_API, stockCode, freeDebateBoard.getGroup().name())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isOk())
                .andReturn();

            final ag.act.model.GetBoardGroupPostResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                ag.act.model.GetBoardGroupPostResponse.class
            );

            assertResponse(result);
        }
    }
}
