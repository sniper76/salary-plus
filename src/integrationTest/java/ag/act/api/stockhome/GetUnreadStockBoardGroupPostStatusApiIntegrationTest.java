package ag.act.api.stockhome;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.PostsViewType;
import ag.act.model.UnreadStockBoardGroupPostStatusDataResponse;
import ag.act.model.UnreadStockBoardGroupPostStatusResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.HashMap;
import java.util.Map;

import static ag.act.TestUtil.someBoardGroupForStock;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someNumericString;

class GetUnreadStockBoardGroupPostStatusApiIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/stocks/{stockCode}/unread-board-group-post-status";

    private final Map<BoardGroup, Boolean> expectedUnreadBoardGroupPostStatusMap = new HashMap<>(Map.ofEntries(
        Map.entry(BoardGroup.ACTION, Boolean.FALSE),
        Map.entry(BoardGroup.ANALYSIS, Boolean.FALSE),
        Map.entry(BoardGroup.DEBATE, Boolean.FALSE))
    );
    private User currentUser;
    private String jwt;
    private Stock stock;

    @BeforeEach
    void setUp() {
        itUtil.init();

        currentUser = itUtil.createUser(someNumericString(6));
        jwt = itUtil.createJwt(currentUser.getId());
        stock = itUtil.createStock();
        itUtil.createSolidarity(stock.getCode());
        itUtil.createUserHoldingStock(stock.getCode(), currentUser);
    }

    @Nested
    class WhenNoPostAndNoRead {

        @Test
        void shouldReturnUnreadStockBoardGroupPostStatusResponse() throws Exception {
            UnreadStockBoardGroupPostStatusDataResponse dataResponse =
                itUtil.getResult(callApi(status().isOk()), UnreadStockBoardGroupPostStatusDataResponse.class);

            assertUnreadStockBoardGroupPostStatusResponse(dataResponse.getUnreadStockBoardGroupPostStatus());
        }
    }

    @Nested
    class WhenNewPostAndNoRead {
        @BeforeEach
        void setUp() {
            final BoardGroup stockBoardGroup = someBoardGroupForStock();
            newPost(stock, stockBoardGroup);

            expectedUnreadBoardGroupPostStatusMap.put(stockBoardGroup, Boolean.TRUE);
        }

        @Test
        void shouldReturnUnreadStockBoardGroupPostStatusResponse() throws Exception {
            UnreadStockBoardGroupPostStatusDataResponse dataResponse =
                itUtil.getResult(callApi(status().isOk()), UnreadStockBoardGroupPostStatusDataResponse.class);

            assertUnreadStockBoardGroupPostStatusResponse(dataResponse.getUnreadStockBoardGroupPostStatus());
        }
    }

    @Nested
    class WhenNewPostAndRead {
        @BeforeEach
        void setUp() {
            final BoardGroup stockBoardGroup = someBoardGroupForStock();
            newPost(stock, stockBoardGroup);

            userReadPosts(stock, currentUser, stockBoardGroup, null, PostsViewType.BOARD_GROUP);
        }

        @Test
        void shouldReturnUnreadStockBoardGroupPostStatusResponse() throws Exception {
            UnreadStockBoardGroupPostStatusDataResponse dataResponse =
                itUtil.getResult(callApi(status().isOk()), UnreadStockBoardGroupPostStatusDataResponse.class);

            assertUnreadStockBoardGroupPostStatusResponse(dataResponse.getUnreadStockBoardGroupPostStatus());
        }
    }

    @Nested
    class WhenNewPostAndOtherUserRead {
        @BeforeEach
        void setUp() {
            final User otherUser = itUtil.createUser();
            itUtil.createUserHoldingStock(stock.getCode(), otherUser);

            final BoardGroup stockBoardGroup = someBoardGroupForStock();
            newPost(stock, stockBoardGroup);
            expectedUnreadBoardGroupPostStatusMap.put(stockBoardGroup, Boolean.TRUE);

            userReadPosts(stock, otherUser, stockBoardGroup, null, PostsViewType.BOARD_GROUP);
        }

        @Test
        void shouldReturnUnreadStockBoardGroupPostStatusResponse() throws Exception {
            UnreadStockBoardGroupPostStatusDataResponse dataResponse =
                itUtil.getResult(callApi(status().isOk()), UnreadStockBoardGroupPostStatusDataResponse.class);

            assertUnreadStockBoardGroupPostStatusResponse(dataResponse.getUnreadStockBoardGroupPostStatus());
        }
    }


    private void assertUnreadStockBoardGroupPostStatusResponse(UnreadStockBoardGroupPostStatusResponse unreadStockBoardGroupPostStatus) {
        assertThat(unreadStockBoardGroupPostStatus.getUnreadAction(), is(expectedUnreadBoardGroupPostStatusMap.get(BoardGroup.ACTION)));
        assertThat(unreadStockBoardGroupPostStatus.getUnreadAnalysis(), is(expectedUnreadBoardGroupPostStatusMap.get(BoardGroup.ANALYSIS)));
        assertThat(unreadStockBoardGroupPostStatus.getUnreadDebate(), is(expectedUnreadBoardGroupPostStatusMap.get(BoardGroup.DEBATE)));
    }

    private void newPost(Stock stock, BoardGroup boardGroup) {
        itUtil.createOrUpdateLatestPostTimestamp(stock, boardGroup);
    }

    private void userReadPosts(
        Stock stock, User user, BoardGroup boardGroup, BoardCategory boardCategory, PostsViewType postsViewType
    ) {
        itUtil.createOrUpdateLatestUserPostsView(stock, user, boardGroup, boardCategory, postsViewType);
    }

    private MvcResult callApi(ResultMatcher matcher) throws Exception {
        return mockMvc
            .perform(
                get(TARGET_API, stock.getCode())
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(matcher)
            .andReturn();
    }
}
