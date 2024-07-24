package ag.act.api.home;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.core.configuration.GlobalBoardManager;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.PostsViewType;
import ag.act.model.UnreadPostStatus;
import ag.act.util.KoreanDateTimeUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ag.act.TestUtil.someBoardGroupForStock;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("SameParameterValue")
public class GetHomeApiUnreadPostsIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/home";

    @Autowired
    private GlobalBoardManager globalBoardManager;

    private String jwt;
    private User user;
    private User otherUser;
    private Stock globalStock;
    private Stock stock1;
    private Stock stock2;
    private Stock stock3;
    private UnreadPostStatus actualUnreadPostStatus;

    private void userReadPosts(
        Stock stock, User user, BoardGroup boardGroup, BoardCategory boardCategory, PostsViewType postsViewType
    ) {
        itUtil.createOrUpdateLatestUserPostsView(stock, user, boardGroup, boardCategory, postsViewType);
    }

    private void newPost(Stock stock) {
        BoardGroup boardGroup = someBoardGroupForStock();
        itUtil.createOrUpdateLatestPostTimestamp(stock, boardGroup);
    }

    private void newPost(Stock stock, BoardGroup boardGroup) {
        itUtil.createOrUpdateLatestPostTimestamp(stock, boardGroup);
    }

    private Post createNewDigitalDocumentAndPost(Board board, Stock stock, LocalDate referenceDate) {
        Post post = itUtil.createPost(board, user.getId());
        User acceptorUser = itUtil.createAcceptorUser();
        itUtil.createDigitalDocument(post, stock, acceptorUser, DigitalDocumentType.DIGITAL_PROXY, referenceDate);
        return post;
    }

    private void newPostAtGlobalBoard() {
        newPost(globalStock, BoardGroup.GLOBALBOARD);
    }

    private void newPostAtGlobalEvent() {
        newPost(globalStock, BoardGroup.GLOBALEVENT);
    }

    private void newPostAtGlobalCommunity() {
        newPost(globalStock, BoardGroup.GLOBALCOMMUNITY);
    }

    private void userReadGlobalBoard() {
        userReadPosts(globalStock, user, BoardGroup.GLOBALBOARD, null, PostsViewType.BOARD_GROUP);
    }

    private void userReadGlobalCommunity() {
        userReadPosts(globalStock, user, BoardGroup.GLOBALCOMMUNITY, null, PostsViewType.BOARD_GROUP);
    }

    private void userReadStockHome(Stock stock) {
        userReadPosts(stock, user, null, null, PostsViewType.STOCK_HOME);
    }

    private void userReadGlobalEvent() {
        userReadPosts(globalStock, user, BoardGroup.GLOBALEVENT, null, PostsViewType.BOARD_GROUP);
    }

    private void userReadDigitalDelegationPost(Post post) {
        itUtil.createPostUserView(user, post);
    }

    private void callApiAndSetUnreadPostStatus() {
        try {
            MvcResult response = mockMvc
                .perform(
                    get(TARGET_API)
                        .headers(headers(jwt(jwt)))
                )
                .andExpect(status().isOk())
                .andReturn();

            actualUnreadPostStatus = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                ag.act.model.HomeResponse.class
            ).getUnreadPostStatus();
        } catch (Exception e) {
            throw new RuntimeException("[GET] /api/home api를 호출하고 응답을 파싱하는데 실패했습니다.");
        }
    }

    private void cleanUpDb() {
        itUtil.deleteAllLatestPostTimestamps();
        itUtil.deleteAllLatestUserPostsViews();
    }

    private void assertUnreadPostStatus(
        Boolean expectedUnreadGlobalBoard,
        Boolean expectedUnreadGlobalCommunity,
        Boolean expectedUnreadGlobalEvent
    ) {
        assertThat(actualUnreadPostStatus.getUnreadGlobalBoard(), is(expectedUnreadGlobalBoard));
        assertThat(actualUnreadPostStatus.getUnreadGlobalCommunity(), is(expectedUnreadGlobalCommunity));
        assertThat(actualUnreadPostStatus.getUnreadGlobalEvent(), is(expectedUnreadGlobalEvent));
    }

    private void assertUnreadPostStatus(
        Boolean expectedUnreadGlobalBoard,
        Boolean expectedUnreadGlobalCommunity,
        List<Stock> expectedUnreadStocks
    ) {
        Set<String> expectedStockCodes = expectedUnreadStocks.stream()
            .map(Stock::getCode)
            .collect(Collectors.toSet());

        List<ag.act.model.StockResponse> stockResponses = actualUnreadPostStatus.getUnreadStocks();

        assertThat(actualUnreadPostStatus.getUnreadGlobalBoard(), is(expectedUnreadGlobalBoard));
        assertThat(actualUnreadPostStatus.getUnreadGlobalCommunity(), is(expectedUnreadGlobalCommunity));
        assertThat(stockResponses.size(), is(expectedUnreadStocks.size()));
        stockResponses.forEach(stockResponse ->
            assertThat(expectedStockCodes, hasItem(stockResponse.getCode())));
    }

    @BeforeEach
    void setUp() {
        user = itUtil.createUser();
        otherUser = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());

        globalStock = itUtil.findStock(globalBoardManager.getStockCode());
        stock1 = itUtil.createStock();
        stock2 = itUtil.createStock();
        stock3 = itUtil.createStock();

        itUtil.createUserHoldingStock(stock1.getCode(), user);
        itUtil.createUserHoldingStock(stock2.getCode(), user);
        itUtil.createUserHoldingStock(stock1.getCode(), otherUser);
        itUtil.createUserHoldingStock(stock2.getCode(), otherUser);
        itUtil.createUserHoldingStock(stock3.getCode(), otherUser);
    }

    @Nested
    class WhenTestUnreadGlobalBoard {
        @AfterEach
        void tearDown() {
            cleanUpDb();
        }

        @Test
        void noReadAndNoPost() {
            // Given

            // When
            callApiAndSetUnreadPostStatus();

            // Then
            assertUnreadPostStatus(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
        }

        @Test
        void noReadAndNewPost() {
            // Given
            newPostAtGlobalBoard();

            // When
            callApiAndSetUnreadPostStatus();

            // Then
            assertUnreadPostStatus(Boolean.TRUE, Boolean.FALSE, Boolean.FALSE);
        }

        @Test
        void newPostAndReadAndNewPost() {
            // Given
            newPostAtGlobalBoard();
            userReadGlobalBoard();
            newPostAtGlobalBoard();

            // When
            callApiAndSetUnreadPostStatus();

            // Then
            assertUnreadPostStatus(Boolean.TRUE, Boolean.FALSE, Boolean.FALSE);
        }

        @Test
        void readAndNewPost() {
            // Given
            userReadGlobalBoard();
            newPostAtGlobalBoard();

            // When
            callApiAndSetUnreadPostStatus();

            // Then
            assertUnreadPostStatus(Boolean.TRUE, Boolean.FALSE, Boolean.FALSE);
        }

        @Test
        void newPostAndRead() {
            // Given
            newPostAtGlobalBoard();
            userReadGlobalBoard();

            // When
            callApiAndSetUnreadPostStatus();

            // Then
            assertUnreadPostStatus(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
        }

        @Test
        void readAndNoPost() {
            // Given
            userReadGlobalBoard();

            // When
            callApiAndSetUnreadPostStatus();

            // Then
            assertUnreadPostStatus(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
        }

        @Test
        void whenOtherUserRead() {
            // Given
            newPostAtGlobalBoard();
            // When other user reads the post
            userReadPosts(
                globalStock, otherUser, BoardGroup.GLOBALBOARD, null, PostsViewType.BOARD_GROUP
            );

            // When
            callApiAndSetUnreadPostStatus();

            // Then
            assertUnreadPostStatus(Boolean.TRUE, Boolean.FALSE, Boolean.FALSE);
        }
    }

    @Nested
    class WhenTestUnreadGlobalCommunity {
        @AfterEach
        void tearDown() {
            cleanUpDb();
        }

        @Test
        void noReadAndNoPost() {
            // Given

            // When
            callApiAndSetUnreadPostStatus();

            // Then
            assertUnreadPostStatus(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
        }

        @Test
        void noReadAndNewPost() {
            // Given
            newPostAtGlobalCommunity();

            // When
            callApiAndSetUnreadPostStatus();

            // Then
            assertUnreadPostStatus(Boolean.FALSE, Boolean.TRUE, Boolean.FALSE);
        }

        @Test
        void newPostAndReadAndNewPost() {
            // Given
            newPostAtGlobalCommunity();
            userReadGlobalCommunity();
            newPostAtGlobalCommunity();

            // When
            callApiAndSetUnreadPostStatus();

            // Then
            assertUnreadPostStatus(Boolean.FALSE, Boolean.TRUE, Boolean.FALSE);
        }

        @Test
        void readAndNewPost() {
            // Given
            userReadGlobalCommunity();
            newPostAtGlobalCommunity();

            // When
            callApiAndSetUnreadPostStatus();

            // Then
            assertUnreadPostStatus(Boolean.FALSE, Boolean.TRUE, Boolean.FALSE);
        }

        @Test
        void newPostAndRead() {
            // Given
            newPostAtGlobalCommunity();
            userReadGlobalCommunity();

            // When
            callApiAndSetUnreadPostStatus();

            // Then
            assertUnreadPostStatus(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
        }

        @Test
        void readAndNoPost() {
            // Given
            userReadGlobalCommunity();

            // When
            callApiAndSetUnreadPostStatus();

            // Then
            assertUnreadPostStatus(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
        }

        @Test
        void whenOtherUserRead() {
            // Given
            newPostAtGlobalCommunity();
            // When other user reads the post
            userReadPosts(
                globalStock, otherUser, BoardGroup.GLOBALCOMMUNITY, null, PostsViewType.BOARD_GROUP
            );

            // When
            callApiAndSetUnreadPostStatus();

            // Then
            assertUnreadPostStatus(Boolean.FALSE, Boolean.TRUE, Boolean.FALSE);
        }
    }

    @Nested
    class WhenTestBothUnreadGlobal {
        @AfterEach
        void tearDown() {
            cleanUpDb();
        }

        @Test
        void bothNoNewPost() {
            // Given

            // When
            callApiAndSetUnreadPostStatus();

            // Then
            assertUnreadPostStatus(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
        }

        @Test
        void readBothNoNewPost() {
            // Given
            userReadGlobalBoard();
            userReadGlobalCommunity();

            // When
            callApiAndSetUnreadPostStatus();

            // Then
            assertUnreadPostStatus(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
        }

        @Test
        void readBothAndBothNewPost() {
            // Given
            userReadGlobalBoard();
            userReadGlobalCommunity();
            newPostAtGlobalBoard();
            newPostAtGlobalCommunity();

            // When
            callApiAndSetUnreadPostStatus();

            // Then
            assertUnreadPostStatus(Boolean.TRUE, Boolean.TRUE, Boolean.FALSE);
        }

        @Test
        void readBothAndGlobalBoardNewPost() {
            // Given
            userReadGlobalBoard();
            userReadGlobalCommunity();
            newPostAtGlobalBoard();

            // When
            callApiAndSetUnreadPostStatus();

            // Then
            assertUnreadPostStatus(Boolean.TRUE, Boolean.FALSE, Boolean.FALSE);
        }

        @Test
        void readBothAndGlobalCommunityNewPost() {
            // Given
            userReadGlobalBoard();
            userReadGlobalCommunity();
            newPostAtGlobalCommunity();

            // When
            callApiAndSetUnreadPostStatus();

            // Then
            assertUnreadPostStatus(Boolean.FALSE, Boolean.TRUE, Boolean.FALSE);
        }

        @Test
        void globalBoardNewPost() {
            // Given
            newPostAtGlobalBoard();

            // When
            callApiAndSetUnreadPostStatus();

            // Then
            assertUnreadPostStatus(Boolean.TRUE, Boolean.FALSE, Boolean.FALSE);
        }

        @Test
        void globalCommunityNewPost() {
            // Given
            newPostAtGlobalCommunity();

            // When
            callApiAndSetUnreadPostStatus();

            // Then
            assertUnreadPostStatus(Boolean.FALSE, Boolean.TRUE, Boolean.FALSE);
        }

        @Test
        void bothNewPost() {
            // Given
            newPostAtGlobalBoard();
            newPostAtGlobalCommunity();

            // When
            callApiAndSetUnreadPostStatus();

            // Then
            assertUnreadPostStatus(Boolean.TRUE, Boolean.TRUE, Boolean.FALSE);

        }

        @Test
        void bothNewPostAndUserReadGlobalBoard() {
            // Given
            newPostAtGlobalBoard();
            newPostAtGlobalCommunity();
            userReadGlobalBoard();

            // When
            callApiAndSetUnreadPostStatus();

            // Then
            assertUnreadPostStatus(Boolean.FALSE, Boolean.TRUE, Boolean.FALSE);
        }

        @Test
        void bothNewPostAndUserReadGlobalCommunity() {
            // Given
            newPostAtGlobalBoard();
            newPostAtGlobalCommunity();
            userReadGlobalCommunity();

            // When
            callApiAndSetUnreadPostStatus();

            // Then
            assertUnreadPostStatus(Boolean.TRUE, Boolean.FALSE, Boolean.FALSE);
        }
    }

    @Nested
    class WhenTestUnreadGlobalEvent {
        @AfterEach
        void tearDown() {
            cleanUpDb();
        }

        @Test
        void noReadAndNoPost() {
            // Given

            // When
            callApiAndSetUnreadPostStatus();

            // Then
            assertUnreadPostStatus(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
        }

        @Test
        void noReadAndNewPost() {
            // Given
            newPostAtGlobalEvent();

            // When
            callApiAndSetUnreadPostStatus();

            // Then
            assertUnreadPostStatus(Boolean.FALSE, Boolean.FALSE, Boolean.TRUE);
        }

        @Test
        void newPostAndReadAndNewPost() {
            // Given
            newPostAtGlobalEvent();
            userReadGlobalEvent();
            newPostAtGlobalEvent();

            // When
            callApiAndSetUnreadPostStatus();

            // Then
            assertUnreadPostStatus(Boolean.FALSE, Boolean.FALSE, Boolean.TRUE);
        }

        @Test
        void readAndNewPost() {
            // Given
            userReadGlobalEvent();
            newPostAtGlobalEvent();

            // When
            callApiAndSetUnreadPostStatus();

            // Then
            assertUnreadPostStatus(Boolean.FALSE, Boolean.FALSE, Boolean.TRUE);
        }

        @Test
        void newPostAndRead() {
            // Given
            newPostAtGlobalEvent();
            userReadGlobalEvent();

            // When
            callApiAndSetUnreadPostStatus();

            // Then
            assertUnreadPostStatus(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
        }

        @Test
        void readAndNoPost() {
            // Given
            userReadGlobalEvent();

            // When
            callApiAndSetUnreadPostStatus();

            // Then
            assertUnreadPostStatus(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
        }

        @Test
        void whenOtherUserRead() {
            // Given
            newPostAtGlobalEvent();
            // When other user reads the post
            userReadPosts(
                globalStock, otherUser, BoardGroup.GLOBALEVENT, null, PostsViewType.BOARD_GROUP
            );

            // When
            callApiAndSetUnreadPostStatus();

            // Then
            assertUnreadPostStatus(Boolean.FALSE, Boolean.FALSE, Boolean.TRUE);
        }
    }

    @Nested
    class WhenTestUnreadStocks {
        @AfterEach
        void tearDown() {
            cleanUpDb();
        }

        @Test
        void oneStockNewPost() {
            // Given
            newPost(stock1);

            // When
            callApiAndSetUnreadPostStatus();

            // Then
            assertUnreadPostStatus(Boolean.FALSE, Boolean.FALSE, List.of(stock1));
        }

        @Test
        void readAndNewPostAndRead() {
            // Given
            userReadStockHome(stock1);
            newPost(stock1);
            userReadStockHome(stock1);

            // When
            callApiAndSetUnreadPostStatus();

            // Then
            assertUnreadPostStatus(Boolean.FALSE, Boolean.FALSE, List.of());
        }

        @Test
        void bothStockNewPost() {
            // Given
            newPost(stock1);
            newPost(stock2);

            // When
            callApiAndSetUnreadPostStatus();

            // Then
            assertUnreadPostStatus(Boolean.FALSE, Boolean.FALSE, List.of(stock1, stock2));
        }

        @Test
        void bothStockNewPostAndReadOne() {
            // Given
            newPost(stock1);
            newPost(stock2);
            userReadStockHome(stock1);

            // When
            callApiAndSetUnreadPostStatus();

            // Then
            assertUnreadPostStatus(Boolean.FALSE, Boolean.FALSE, List.of(stock2));
        }

        @Test
        void bothStockNewPostAndReadBoth() {
            // Given
            newPost(stock1);
            newPost(stock2);
            userReadStockHome(stock1);
            userReadStockHome(stock2);

            // When
            callApiAndSetUnreadPostStatus();

            // Then
            assertUnreadPostStatus(Boolean.FALSE, Boolean.FALSE, List.of());
        }

        @Test
        void notHoldingStockNewPost() {
            // Given
            newPost(stock1);
            newPost(stock2);
            // User not holding stock3. Should be exclueded in the response.
            newPost(stock3);

            // When
            callApiAndSetUnreadPostStatus();

            // Then
            assertUnreadPostStatus(Boolean.FALSE, Boolean.FALSE, List.of(stock1, stock2));
        }

        @Test
        void whenOtherRead() {
            // Given
            newPost(stock1);
            newPost(stock2);
            // When other user reads the post
            userReadPosts(stock1, otherUser, null, null, PostsViewType.STOCK_HOME);

            // When
            callApiAndSetUnreadPostStatus();

            // Then
            assertUnreadPostStatus(Boolean.FALSE, Boolean.FALSE, List.of(stock1, stock2));
        }
    }

    @Nested
    class WhenTestUnreadDigitalDelegation {

        private LocalDate referenceDate;
        private Board board;

        @AfterEach
        void tearDown() {
            cleanUpDb();
        }

        @BeforeEach
        void setUp() {
            referenceDate = KoreanDateTimeUtil.getCurrentDateTime().toLocalDate();
            itUtil.createUserHoldingStockOnReferenceDate(stock1.getCode(), user.getId(), referenceDate);
            board = itUtil.createBoard(stock1, BoardGroup.ACTION, BoardCategory.DIGITAL_DELEGATION);
        }

        @Test
        void newPostAndNoRead() {
            createNewDigitalDocumentAndPost(board, stock1, referenceDate);

            // When
            callApiAndSetUnreadPostStatus();

            // Then
            assertThat(actualUnreadPostStatus.getUnreadDigitalDelegation(), is(Boolean.TRUE));
        }

        @Test
        void twoNewPostAndReadOne() {
            // Given
            Post post1 = createNewDigitalDocumentAndPost(board, stock1, referenceDate);
            createNewDigitalDocumentAndPost(board, stock1, referenceDate);
            userReadDigitalDelegationPost(post1);

            // When
            callApiAndSetUnreadPostStatus();

            // Then
            assertThat(actualUnreadPostStatus.getUnreadDigitalDelegation(), is(Boolean.TRUE));
        }

        @Test
        void twoNewPostAndReadAll() {
            // Given
            Post post1 = createNewDigitalDocumentAndPost(board, stock1, referenceDate);
            Post post2 = createNewDigitalDocumentAndPost(board, stock1, referenceDate);
            userReadDigitalDelegationPost(post1);
            userReadDigitalDelegationPost(post2);

            // When
            callApiAndSetUnreadPostStatus();

            // Then
            assertThat(actualUnreadPostStatus.getUnreadDigitalDelegation(), is(Boolean.FALSE));
        }
    }
}