package ag.act.api.stockhome;

import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.BoardGroup;
import ag.act.enums.PostsViewType;
import ag.act.model.StockHomeResponse;
import ag.act.model.UnreadStockBoardGroupPostStatusResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static ag.act.TestUtil.someBoardGroupForStock;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetStockHomeUnreadStockBoardGroupPostStatusResponseApiIntegrationTest extends GetStockHomeApiIntegrationTest {

    private final Map<BoardGroup, Boolean> expectedUnreadBoardGroupPostStatusMap = new HashMap<>(Map.ofEntries(
        Map.entry(BoardGroup.ACTION, Boolean.FALSE),
        Map.entry(BoardGroup.ANALYSIS, Boolean.FALSE),
        Map.entry(BoardGroup.DEBATE, Boolean.FALSE))
    );

    @Nested
    class WhenNoPostAndNoRead {

        @Test
        void shouldReturnUnreadStockBoardGroupPostStatusResponse() throws Exception {
            StockHomeResponse response = getResponse(callApi(status().isOk()));

            assertUnreadStockBoardGroupPostStatusResponse(response.getUnreadStockBoardGroupPostStatus());
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
            StockHomeResponse response = getResponse(callApi(status().isOk()));

            assertUnreadStockBoardGroupPostStatusResponse(response.getUnreadStockBoardGroupPostStatus());
        }
    }

    @Nested
    class WhenNewPostAndRead {
        @BeforeEach
        void setUp() {
            final BoardGroup stockBoardGroup = someBoardGroupForStock();
            newPost(stock, stockBoardGroup);

            userReadPosts(stock, currentUser, stockBoardGroup, PostsViewType.BOARD_GROUP);
        }

        @Test
        void shouldReturnUnreadStockBoardGroupPostStatusResponse() throws Exception {
            StockHomeResponse response = getResponse(callApi(status().isOk()));

            assertUnreadStockBoardGroupPostStatusResponse(response.getUnreadStockBoardGroupPostStatus());
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

            userReadPosts(stock, otherUser, stockBoardGroup, PostsViewType.BOARD_GROUP);
        }

        @Test
        void shouldReturnUnreadStockBoardGroupPostStatusResponse() throws Exception {
            StockHomeResponse response = getResponse(callApi(status().isOk()));

            assertUnreadStockBoardGroupPostStatusResponse(response.getUnreadStockBoardGroupPostStatus());
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
        Stock stock, User user, BoardGroup boardGroup, PostsViewType postsViewType
    ) {
        itUtil.createOrUpdateLatestUserPostsView(stock, user, boardGroup, null, postsViewType);
    }
}
