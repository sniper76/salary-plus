package ag.act.api.stockboardgrouppost.preview;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.TestUtil;
import ag.act.core.configuration.GlobalBoardManager;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.model.GetBoardGroupPostResponse;
import ag.act.model.Paging;
import ag.act.model.PostResponse;
import ag.act.model.Status;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static ag.act.TestUtil.someBoardGroupCategoryForGlobal;
import static ag.act.TestUtil.someBoardGroupCategoryForStock;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static ag.act.itutil.authentication.AuthenticationTestUtil.userAgent;
import static ag.act.itutil.authentication.AuthenticationTestUtil.xAppVersion;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetPostPreviewsApiIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts/previews";
    private static final int PREVIEW_PAGE_SIZE = 5;
    private static final long BEST_POST_LIKE_COUNT_CRITERIA = 10L;
    private static final int EXCLUSIVE_TO_HOLDERS_POST_COUNT = 1;
    private static final int EXCLUSIVE_TO_HOLDERS_POST_INDEX = 0;

    private User currentUser;
    private String jwt;
    private Stock stock;
    private User writer;
    private Post[] posts;
    private String globalStockCode;

    @Autowired
    private GlobalBoardManager globalBoardManager;

    @BeforeEach
    void setUp() {
        itUtil.init();
        globalStockCode = globalBoardManager.getStockCode();
        dbCleaner.clean();

        currentUser = itUtil.createUser();
        jwt = itUtil.createJwt(currentUser.getId());
        writer = itUtil.createUser();
    }

    @AfterEach
    void tearDown() {
        dbCleaner.clean();
    }

    @Nested
    class GetGlobalBoardGroupPostPreviews {
        private Board board;

        @BeforeEach
        void setUp() {
            createAndSetGlobalStock();
            createAndSetGlobalBoard();

            posts = new Post[] {
                createPost(board),
                createPost(board),
                createPost(board),
                createPost(board)
            };

            // 잉여 데이터
            createDeletedPost(board);
        }

        @Nested
        @DisplayName("유저가")
        class WhenUser {
            @Test
            @DisplayName("글로벌 게시판 미리보기 목록을 조회한다.")
            void shouldReturnSuccess() throws Exception {
                final GetBoardGroupPostResponse result = userCallApiAndGetResponse(
                    board.getGroup().name()
                );

                assertPaging(result.getPaging());
                assertPosts(result.getData());
            }
        }

        @Nested
        @DisplayName("게스트가")
        class WhenGuest {

            @Test
            @DisplayName("글로벌 게시판 미리보기 목록을 조회한다.")
            void shouldReturnSuccess() throws Exception {
                final GetBoardGroupPostResponse result = guestCallApiAndGetResponse(
                    status().isOk(),
                    board.getGroup().name()
                );

                assertPaging(result.getPaging());
                assertPosts(result.getData());
            }
        }

        private void createAndSetGlobalBoard() {
            TestUtil.BoardGroupCategory globalBoardGroupCategory = someBoardGroupCategoryForGlobal();
            board = getBoard(globalBoardGroupCategory.boardGroup(), globalBoardGroupCategory.boardCategory());
        }
    }

    @Nested
    class GetStockBoardGroupPostPreviews {
        private Board board;

        @BeforeEach
        void setUp() {
            createAndSetNotGlobalStock();
            createAndSetNotGlobalBoard();

            posts = new Post[] {
                createPost(board),
                createPost(board),
                createPost(board),
                createExclusiveToHoldersPost(board)
            };

            // 잉여 데이터
            createDeletedPost(board);
        }

        @Nested
        @DisplayName("유저가 게시글의 종목을 보유할 때")
        class WhenUserIsHoldingStockOfPost {

            @BeforeEach
            void setUp() {
                itUtil.createUserHoldingStock(stock.getCode(), currentUser);
            }

            @Test
            @DisplayName("종목 게시판 미리보기 목록을 조회한다.")
            void shouldReturnSuccess() throws Exception {
                final GetBoardGroupPostResponse result = userCallApiAndGetResponse(
                    board.getGroup().name()
                );

                assertPaging(result.getPaging());
                assertPosts(result.getData());
            }

            private void assertPosts(List<PostResponse> postResponses) {
                final int expectedPostSize = posts.length;
                assertThat(postResponses.size(), is(expectedPostSize));

                for (int i = EXCLUSIVE_TO_HOLDERS_POST_COUNT; i < expectedPostSize; i++) {
                    assertPostResponse(postResponses.get(i), posts[expectedPostSize - EXCLUSIVE_TO_HOLDERS_POST_COUNT - i]);
                }
                assertExclusiveToHoldersPostResponse(postResponses.get(EXCLUSIVE_TO_HOLDERS_POST_INDEX), posts[expectedPostSize - 1]);
            }

            private void assertExclusiveToHoldersPostResponse(PostResponse actual, Post expected) {
                final Board expectedBoard = expected.getBoard();

                assertThat(actual.getId(), is(expected.getId()));
                assertThat(actual.getStock().getCode(), is(expectedBoard.getStock().getCode()));
                assertThat(actual.getBoardId(), is(expectedBoard.getId()));
                assertThat(actual.getTitle(), is(expected.getTitle()));
                assertThat(actual.getStatus(), is(expected.getStatus()));
                assertThat(actual.getLikeCount(), is(expected.getLikeCount()));
                assertThat(actual.getIsExclusiveToHolders(), is(true));
                assertThat(actual.getStatus(), is(Status.ACTIVE));
                assertThat(actual.getDeleted(), is(false));
                assertThat(actual.getDeletedAt(), is(nullValue()));
            }
        }

        @Nested
        @DisplayName("유저가 게시글의 종목을 보유하지 않을 때")
        class WhenUserIsNotHoldingStockOfPost {

            @Test
            @DisplayName("종목 게시판 미리보기 목록을 조회한다.")
            void shouldReturnSuccess() throws Exception {
                final GetBoardGroupPostResponse result = userCallApiAndGetResponse(
                    board.getGroup().name());

                assertPaging(result.getPaging());
                assertPosts(result.getData());
            }

            private void assertPosts(List<PostResponse> postResponses) {
                final int expectedPostSize = posts.length;
                assertThat(postResponses.size(), is(expectedPostSize));

                for (int i = EXCLUSIVE_TO_HOLDERS_POST_COUNT; i < expectedPostSize; i++) {
                    assertPostResponse(postResponses.get(i), posts[expectedPostSize - EXCLUSIVE_TO_HOLDERS_POST_COUNT - i]);
                }
                assertNotUserHoldingStockPostResponse(postResponses.get(EXCLUSIVE_TO_HOLDERS_POST_INDEX), posts[expectedPostSize - 1]);
            }
        }

        @Nested
        @DisplayName("게스트가")
        class WhenGuest {
            @Test
            @DisplayName("인가되지 않은 접근 에러를 반환한다.")
            void shouldReturnUnauthorized() throws Exception {
                MvcResult mvcResult = guestCallApi(status().isUnauthorized(), board.getGroup().name());

                itUtil.assertErrorResponse(mvcResult, UNAUTHORIZED_STATUS, "인가되지 않은 접근입니다.");
            }
        }

        private void createAndSetNotGlobalBoard() {
            TestUtil.BoardGroupCategory stockBoardGroupCategory = someBoardGroupCategoryForStock();
            board = getBoard(stockBoardGroupCategory.boardGroup(), stockBoardGroupCategory.boardCategory());
        }

        private void assertNotUserHoldingStockPostResponse(PostResponse actual, Post expected) {
            final Board expectedBoard = expected.getBoard();

            assertThat(actual.getId(), is(expected.getId()));
            assertThat(actual.getStock().getCode(), is(expectedBoard.getStock().getCode()));
            assertThat(actual.getBoardId(), is(expectedBoard.getId()));
            assertThat(actual.getTitle(), is("주주에게만 공개된 제한된 글입니다."));
            assertThat(actual.getStatus(), is(expected.getStatus()));
            assertThat(actual.getLikeCount(), is(expected.getLikeCount()));
            assertThat(actual.getIsExclusiveToHolders(), is(Boolean.TRUE));
            assertThat(actual.getStatus(), is(Status.ACTIVE));
        }
    }

    private Post createPost(Board board) {
        Post post = itUtil.createPostWithLikeCount(board, writer.getId(), BEST_POST_LIKE_COUNT_CRITERIA);
        return itUtil.updatePost(post);
    }

    private void createDeletedPost(Board board) {
        Post post = createPost(board);
        post.setDeletedAt(LocalDateTime.now());
        post.setStatus(Status.DELETED);
        itUtil.updatePost(post);
    }

    private Post createExclusiveToHoldersPost(Board board) {
        Post post = createPost(board);
        post.setIsExclusiveToHolders(Boolean.TRUE);
        return itUtil.updatePost(post);
    }

    private void createAndSetNotGlobalStock() {
        stock = itUtil.createStock();
        itUtil.createUserHoldingStock(stock.getCode(), writer);
    }

    private void createAndSetGlobalStock() {
        stock = itUtil.findStock(globalStockCode);
    }

    private Board getBoard(BoardGroup boardGroup, BoardCategory boardCategory) {
        final Optional<Board> boardOptional = itUtil.findBoard(stock.getCode(), boardCategory);
        return boardOptional.orElseGet(() -> itUtil.createBoard(stock, boardGroup, boardCategory));
    }

    private void assertPaging(Paging paging) {
        assertThat(paging.getSize(), is(PREVIEW_PAGE_SIZE));
        assertThat(paging.getTotalElements(), is((long) posts.length));
    }

    private void assertPosts(List<PostResponse> postResponses) {
        final int expectedPostSize = posts.length;
        assertThat(postResponses.size(), is(expectedPostSize));

        for (int i = 0; i < expectedPostSize; i++) {
            assertPostResponse(postResponses.get(i), posts[expectedPostSize - 1 - i]);
        }
    }

    private void assertPostResponse(PostResponse actual, Post expected) {
        final Board expectedBoard = expected.getBoard();

        assertThat(actual.getId(), is(expected.getId()));
        assertThat(actual.getStock().getCode(), is(expectedBoard.getStock().getCode()));
        assertThat(actual.getBoardId(), is(expectedBoard.getId()));
        assertThat(actual.getTitle(), is(expected.getTitle()));
        assertThat(actual.getStatus(), is(expected.getStatus()));
        assertThat(actual.getLikeCount(), is(expected.getLikeCount()));
        assertThat(actual.getIsExclusiveToHolders(), is(false));
        assertThat(actual.getStatus(), is(Status.ACTIVE));
        assertThat(actual.getDeleted(), is(false));
        assertThat(actual.getDeletedAt(), is(nullValue()));
    }

    private GetBoardGroupPostResponse userCallApiAndGetResponse(
        String boardGroupName
    ) throws Exception {
        return itUtil.getResult(userCallApi(boardGroupName), GetBoardGroupPostResponse.class);
    }

    private GetBoardGroupPostResponse guestCallApiAndGetResponse(
        ResultMatcher resultMatcher,
        String boardGroupName
    ) throws Exception {
        return itUtil.getResult(guestCallApi(resultMatcher, boardGroupName), GetBoardGroupPostResponse.class);
    }

    private MvcResult userCallApi(String boardGroupName) throws Exception {
        return mockMvc.perform(
                get(TARGET_API, stock.getCode(), boardGroupName)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt), xAppVersion(X_APP_VERSION_WEB), userAgent(USER_AGENT_WEB)))
            )
            .andExpect(status().isOk())
            .andReturn();
    }

    private MvcResult guestCallApi(ResultMatcher resultMatcher, String boardGroupName) throws Exception {
        return mockMvc.perform(
                get(TARGET_API, stock.getCode(), boardGroupName)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(xAppVersion(X_APP_VERSION_WEB), userAgent(USER_AGENT_WEB)))

            )
            .andExpect(resultMatcher)
            .andReturn();
    }
}