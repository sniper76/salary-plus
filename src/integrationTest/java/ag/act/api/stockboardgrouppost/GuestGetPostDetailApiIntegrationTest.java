package ag.act.api.stockboardgrouppost;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.core.configuration.GlobalBoardManager;
import ag.act.dto.AppVersion;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.PostUserView;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.AppPreferenceType;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.model.PostDetailsDataResponse;
import ag.act.model.PostDetailsResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;
import java.util.Optional;

import static ag.act.TestUtil.someBoardCategory;
import static ag.act.TestUtil.someBoardCategoryExcluding;
import static ag.act.TestUtil.someBoardGroupForGlobal;
import static ag.act.TestUtil.someBoardGroupForStock;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomThings.someThing;

@SuppressWarnings("checkstyle:LineLength")
class GuestGetPostDetailApiIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}";
    private static final String TOO_HIGH_APP_VERSION = "100.100.100";
    private static final List<BoardCategory> PUBLIC_READABLE_STOCK_BOARD_CATEGORIES = List.of(
        BoardCategory.DEBATE,
        BoardCategory.SOLIDARITY_LEADER_LETTERS
    );
    private static final BoardCategory[] PUBLIC_READABLE_STOCK_BOARD_CATEGORY_ARRAY = PUBLIC_READABLE_STOCK_BOARD_CATEGORIES.toArray(BoardCategory[]::new);

    private final String defaultAppVersion = AppPreferenceType.MIN_APP_VERSION.getDefaultValue();


    @Autowired
    private GlobalBoardManager globalBoardManager;

    private String globalStockCode = null;
    private Stock stock;
    private Board board;
    private Post post;

    @BeforeEach
    void setUp() {
        itUtil.init();
        globalStockCode = globalBoardManager.getStockCode();
        dbCleaner.clean();

        minAppVersionIsTooHigh();
    }

    @AfterEach
    void tearDown() {
        dbCleaner.clean();
    }

    @Nested
    class GetGlobalBoardGroupPostDetails {
        @BeforeEach
        void setUp() {
            final User writer = itUtil.createUser();
            final BoardGroup boardGroup = someBoardGroupForGlobal();
            final BoardCategory boardCategory = someBoardCategory(boardGroup);
            stock = itUtil.findStock(globalStockCode);
            board = getBoard(boardGroup, boardCategory);

            createPost(writer);
        }

        @Test
        void shouldReturnSuccess() throws Exception {
            final PostDetailsDataResponse result = callApiAndGetResponse(board.getGroup().name(), X_APP_VERSION_WEB, status().isOk());

            assertResponse(result);
        }
    }

    @Nested
    class GetStockBoardGroupPostDetails {

        @Nested
        class WhenCategoryIsReadableToGuest {
            @BeforeEach
            void setUp() {
                final User writer = itUtil.createUser();
                final BoardCategory boardCategory = someThing(PUBLIC_READABLE_STOCK_BOARD_CATEGORY_ARRAY);
                stock = itUtil.createStock();
                board = itUtil.createBoard(stock, boardCategory.getBoardGroup(), boardCategory);

                createPost(writer);
            }

            @Test
            void shouldReturnSuccess() throws Exception {
                final PostDetailsDataResponse result = callApiAndGetResponse(board.getGroup().name(), X_APP_VERSION_WEB, status().isOk());

                assertResponse(result);
            }
        }

        @Nested
        class WhenCategoryIsUnreadableToGuest {
            @BeforeEach
            void setUp() {
                final User writer = itUtil.createUser();
                final BoardCategory boardCategory = someBoardCategory();
                stock = itUtil.createStock();
                board = itUtil.createBoard(stock, boardCategory.getBoardGroup(), boardCategory);

                createPost(writer);
            }

            private static BoardCategory someBoardCategory() {
                for (int i = 0; i < 10; i++) {
                    try {
                        return someBoardCategoryExcluding(someBoardGroupForStock(), PUBLIC_READABLE_STOCK_BOARD_CATEGORY_ARRAY);
                    } catch (Exception ignored) {
                        // ignore
                    }
                }
                throw new RuntimeException("Failed to find a board category excluding public readable categories");
            }

            @Test
            void shouldReturnForbidden() throws Exception {
                MvcResult mvcResult = callApi(board.getGroup().name(), X_APP_VERSION_WEB, status().isUnauthorized());

                itUtil.assertErrorResponse(mvcResult, 401, "인가되지 않은 접근입니다.");
            }
        }
    }

    @Nested
    class GetPostDetailsWithNonZeroViewCount {
        @BeforeEach
        void setUp() {
            final User writer = itUtil.createUser();
            stock = itUtil.findStock(globalStockCode);
            board = getBoard(BoardGroup.GLOBALCOMMUNITY, BoardCategory.FREE_DEBATE);

            createPostWithSomeViewCount(writer);
        }

        @Test
        void shouldReturnSuccess() throws Exception {
            final PostDetailsDataResponse result = callApiAndGetResponse(board.getGroup().name(), X_APP_VERSION_WEB, status().isOk());

            assertResponse(result);
        }

        private void createPostWithSomeViewCount(User writer) {
            post = itUtil.createPost(board, writer.getId());
            PostUserView postUserView = itUtil.createPostUserView(writer, post);
            post.setViewCount(postUserView.getCount());
        }
    }

    @Nested
    class WhenAppVersionIsNotWeb {
        @BeforeEach
        void setUp() {
            final User writer = itUtil.createUser();
            stock = itUtil.findStock(globalStockCode);
            board = getBoard(BoardGroup.GLOBALCOMMUNITY, BoardCategory.FREE_DEBATE);
            createPost(writer);
        }

        @Test
        void shouldReturnUpgradeRequired() throws Exception {
            MvcResult mvcResult = callApi(
                board.getGroup().name(),
                defaultAppVersion,
                status().isUpgradeRequired()
            );

            itUtil.assertErrorResponse(
                mvcResult,
                426,
                4261,
                "최신앱으로 업데이트해야 서비스를 이용할 수 있습니다"
            );
        }
    }

    private void minAppVersionIsTooHigh() {
        given(appPreferenceCache.getValue(AppPreferenceType.MIN_APP_VERSION)).willReturn(AppVersion.of(TOO_HIGH_APP_VERSION));
    }

    private Board getBoard(BoardGroup boardGroup, BoardCategory boardCategory) {
        final Optional<Board> boardOptional = itUtil.findBoard(stock.getCode(), boardCategory);
        return boardOptional.orElseGet(() -> itUtil.createBoard(stock, boardGroup, boardCategory));
    }

    private void createPost(User writer) {
        post = itUtil.createPost(board, writer.getId());
        post.setViewCount(0L);
        post.setViewUserCount(0L);
    }

    private void assertResponse(PostDetailsDataResponse result) {
        PostDetailsResponse actual = result.getData();

        assertThat(actual.getId(), is(post.getId()));
        assertThat(actual.getStock().getCode(), is(stock.getCode()));
        assertThat(actual.getBoardId(), is(board.getId()));
        assertThat(actual.getTitle(), is(post.getTitle()));
        assertThat(actual.getStatus(), is(post.getStatus()));
        assertThat(actual.getReported(), is(false));
        assertThat(actual.getDeleted(), is(false));
        assertThat(actual.getIsAuthorAdmin(), is(false));
        assertThat(actual.getIsExclusiveToHolders(), is(post.getIsExclusiveToHolders()));
        assertThat(actual.getViewCount(), is(post.getViewCount()));
        assertThat(actual.getDigitalDocument(), nullValue());
        assertThat(actual.getHolderListReadAndCopyDigitalDocument(), nullValue());
    }

    private PostDetailsDataResponse callApiAndGetResponse(
        String boardGroupName,
        String appVersion,
        ResultMatcher resultMatcher
    ) throws Exception {
        MvcResult result = callApi(boardGroupName, appVersion, resultMatcher);

        return itUtil.getResult(result, PostDetailsDataResponse.class);
    }

    private MvcResult callApi(String boardGroupName, String appVersion, ResultMatcher resultMatcher) throws Exception {
        return mockMvc.perform(
                get(TARGET_API, stock.getCode(), boardGroupName, post.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header(X_APP_VERSION, appVersion)
            )
            .andExpect(resultMatcher)
            .andReturn();
    }
}