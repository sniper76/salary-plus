package ag.act.api.stockboardgrouppost;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.core.configuration.GlobalBoardManager;
import ag.act.dto.AppVersion;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.AppPreferenceType;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.virtualboard.VirtualBoardGroup;
import ag.act.model.GetBoardGroupPostResponse;
import ag.act.model.PostResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static ag.act.TestUtil.someBoardCategory;
import static ag.act.TestUtil.someBoardGroupForStock;
import static ag.act.TestUtil.toMultiValueMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("checkstyle:LineLength")
class GuestGetPostsApiIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts";
    private static final String EVENT_CAMPAIGN = "EVENT,CAMPAIGN";
    private static final String TOO_HIGH_APP_VERSION = "100.100.100";
    private final String defaultAppVersion = AppPreferenceType.MIN_APP_VERSION.getDefaultValue();

    @Autowired
    private GlobalBoardManager globalBoardManager;

    private String globalStockCode = null;
    private Stock globalStock;
    private Board board;
    private Post post1;
    private Post post2;

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
    class GetActGlobalBoardGroupPosts {
        @BeforeEach
        void setUp() {
            globalStock = itUtil.findStock(globalStockCode);
        }

        @Nested
        class WhenGlobalBoard {

            @Nested
            class AndAllCategory {
                @BeforeEach
                void setUp() {
                    final User writer = itUtil.createUser();

                    final Board board1 = getBoard(BoardGroup.GLOBALBOARD, BoardCategory.STOCKHOLDER_ACTION);
                    post1 = itUtil.createPost(board1, writer.getId());

                    final Board board2 = getBoard(BoardGroup.GLOBALBOARD, BoardCategory.STOCK_ANALYSIS_DATA);
                    post2 = itUtil.createPost(board2, writer.getId());
                }

                @Test
                void shouldReturnSuccess() throws Exception {
                    final GetBoardGroupPostResponse result = callApiAndGetResponse(BoardGroup.GLOBALBOARD.name(), X_APP_VERSION_WEB, status().isOk());

                    assertResponse(result);
                }
            }

            @Nested
            class AndStockholderActionCategory {
                @BeforeEach
                void setUp() {
                    board = getBoard(BoardGroup.GLOBALBOARD, BoardCategory.STOCKHOLDER_ACTION);

                    final User writer = itUtil.createUser();
                    post1 = itUtil.createPost(board, writer.getId());
                    post2 = itUtil.createPost(board, writer.getId());
                }

                @Test
                void shouldReturnSuccess() throws Exception {
                    final GetBoardGroupPostResponse result = callApiAndGetResponse(
                        board.getGroup().name(),
                        board.getCategory().name(),
                        X_APP_VERSION_WEB,
                        status().isOk()
                    );

                    assertResponse(result);
                }
            }

            @Nested
            class AndStockAnalysisDataCategory {
                @BeforeEach
                void setUp() {
                    board = getBoard(BoardGroup.GLOBALBOARD, BoardCategory.STOCK_ANALYSIS_DATA);

                    final User writer = itUtil.createUser();
                    post1 = itUtil.createPost(board, writer.getId());
                    post2 = itUtil.createPost(board, writer.getId());
                }

                @Test
                void shouldReturnSuccess() throws Exception {
                    final GetBoardGroupPostResponse result = callApiAndGetResponse(
                        board.getGroup().name(),
                        board.getCategory().name(),
                        X_APP_VERSION_WEB,
                        status().isOk()
                    );
                    assertResponse(result);
                }
            }
        }

        @Nested
        class WhenGlobalEvent {

            @Nested
            class AndEventAndCampaignCategory {
                @BeforeEach
                void setUp() {
                    final User writer = itUtil.createUser();

                    final Board board1 = getBoard(BoardGroup.GLOBALEVENT, BoardCategory.EVENT);
                    post1 = itUtil.createPost(board1, writer.getId());
                    final Board board2 = getBoard(BoardGroup.GLOBALEVENT, BoardCategory.CAMPAIGN);
                    post2 = itUtil.createPost(board2, writer.getId());
                }

                @Test
                void shouldReturnSuccess() throws Exception {
                    final GetBoardGroupPostResponse result = callApiAndGetResponse(BoardGroup.GLOBALEVENT.name(), X_APP_VERSION_WEB, status().isOk());

                    assertResponse(result);
                }
            }

            @Nested
            class AndNoticeCategory {
                @BeforeEach
                void setUp() {
                    board = getBoard(BoardGroup.GLOBALEVENT, BoardCategory.NOTICE);

                    final User writer = itUtil.createUser();
                    post1 = itUtil.createPost(board, writer.getId());
                    post2 = itUtil.createPost(board, writer.getId());
                }

                @Test
                void shouldReturnSuccess() throws Exception {
                    final GetBoardGroupPostResponse result = callApiAndGetResponse(
                        board.getGroup().name(),
                        board.getCategory().name(),
                        X_APP_VERSION_WEB,
                        status().isOk()
                    );
                    assertResponse(result);
                }
            }
        }

        @Nested
        class WhenGlobalCommunity {
            @BeforeEach
            void setUp() {
                board = getBoard(BoardGroup.GLOBALCOMMUNITY, BoardCategory.FREE_DEBATE);

                final User writer = itUtil.createUser();
                post1 = itUtil.createPost(board, writer.getId());
                post2 = itUtil.createPost(board, writer.getId());
            }

            @Test
            void shouldReturnSuccess() throws Exception {
                final GetBoardGroupPostResponse result = callApiAndGetResponse(board.getGroup().name(), X_APP_VERSION_WEB, status().isOk());

                assertResponse(result);
            }
        }
    }

    @Nested
    class GetActBestPosts {

        private final int size = 6;
        private Post post3;
        private Post post4;
        private Post post5;
        private Post post6;

        @BeforeEach
        void setUp() {
            final Stock stock = itUtil.createStock();
            globalStock = itUtil.findStock(globalStockCode);

            final User writer = itUtil.createUser();
            final Board board1 = getBoard(BoardGroup.GLOBALBOARD, BoardCategory.STOCKHOLDER_ACTION);
            post1 = itUtil.createPostWithLikeCount(board1, writer.getId(), 200L);

            final Board board2 = getBoard(BoardGroup.GLOBALBOARD, BoardCategory.STOCK_ANALYSIS_DATA);
            post2 = itUtil.createPostWithLikeCount(board2, writer.getId(), 200L);

            final Board board3 = getBoard(BoardGroup.GLOBALCOMMUNITY, BoardCategory.FREE_DEBATE);
            post3 = itUtil.createPostWithLikeCount(board3, writer.getId(), 200L);

            final Board board4 = itUtil.createBoard(stock, BoardGroup.ANALYSIS, BoardCategory.DAILY_ACT);
            post4 = itUtil.createPostWithLikeCount(board4, writer.getId(), 200L);

            final Board board5 = itUtil.createBoard(stock, BoardGroup.ANALYSIS, BoardCategory.SOLIDARITY_LEADER_LETTERS);
            post5 = itUtil.createPostWithLikeCount(board5, writer.getId(), 200L);

            final Board board6 = itUtil.createBoard(stock, BoardGroup.DEBATE, BoardCategory.DEBATE);
            post6 = itUtil.createPostWithLikeCount(board6, writer.getId(), 200L);

            final Board exceptBoard1 = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.SURVEYS);
            itUtil.createPostWithLikeCount(exceptBoard1, writer.getId(), 200L);
        }

        @Test
        void shouldReturnSuccess() throws Exception {
            final GetBoardGroupPostResponse result = callApiAndGetResponse(VirtualBoardGroup.ACT_BEST.name(), X_APP_VERSION_WEB, status().isOk());

            List<PostResponse> postResponses = result.getData();
            assertThat(postResponses.size(), is(size));
            assertPostResponse(postResponses.get(0), post6);
            assertPostResponse(postResponses.get(1), post5);
            assertPostResponse(postResponses.get(2), post4);
            assertPostResponse(postResponses.get(3), post3);
            assertPostResponse(postResponses.get(4), post2);
            assertPostResponse(postResponses.get(5), post1);
        }
    }

    @Nested
    class GetActStockBoardGroupPosts {

        private Stock stock;

        @BeforeEach
        void setUp() {
            stock = itUtil.createStock();

            final BoardGroup boardGroup = someBoardGroupForStock();
            final BoardCategory boardCategory = someBoardCategory(boardGroup);
            board = itUtil.createBoard(stock, boardGroup, boardCategory);
        }

        @Test
        void shouldReturnForbidden() throws Exception {
            MvcResult mvcResult = callApi(status().isUnauthorized());

            itUtil.assertErrorResponse(mvcResult, 401, "인가되지 않은 접근입니다.");
        }

        private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {

            Map<String, Object> params = Map.of(
                "sorts", CREATED_AT_DESC,
                "boardCategory", board.getCategory(),
                "isExclusiveToHolders", Boolean.FALSE);

            return mockMvc
                .perform(
                    get(TARGET_API, stock, board.getGroup().name())
                        .params(toMultiValueMap(params))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(resultMatcher)
                .andReturn();
        }
    }

    @Nested
    class WhenAppVersionIsNotWeb {
        @BeforeEach
        void setUp() {
            final User writer = itUtil.createUser();

            globalStock = itUtil.findStock(globalStockCode);
            board = getBoard(BoardGroup.GLOBALCOMMUNITY, BoardCategory.FREE_DEBATE);
            itUtil.createPost(board, writer.getId());
        }

        @Test
        void shouldReturnUpgradeRequired() throws Exception {
            MvcResult mvcResult = callApi(
                board.getGroup().name(),
                board.getCategory().name(),
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
        final Optional<Board> boardOptional = itUtil.findBoard(globalStock.getCode(), boardCategory);
        return boardOptional.orElseGet(() -> itUtil.createBoard(globalStock, boardGroup, boardCategory));
    }

    private void assertResponse(GetBoardGroupPostResponse result) {
        List<PostResponse> postResponses = result.getData();
        assertThat(postResponses.size(), is(SIZE));
        assertPostResponse(postResponses.get(0), post2);
        assertPostResponse(postResponses.get(1), post1);
    }

    private void assertPostResponse(PostResponse actual, Post expected) {
        final Board expectedBoard = expected.getBoard();

        assertThat(actual.getId(), is(expected.getId()));
        assertThat(actual.getStock().getCode(), is(expectedBoard.getStock().getCode()));
        assertThat(actual.getBoardId(), is(expectedBoard.getId()));
        assertThat(actual.getTitle(), is(expected.getTitle()));
        assertThat(actual.getStatus(), is(expected.getStatus()));
        assertThat(actual.getReported(), is(false));
        assertThat(actual.getDeleted(), is(false));
        assertThat(actual.getIsAuthorAdmin(), is(false));
        assertThat(actual.getIsExclusiveToHolders(), is(expected.getIsExclusiveToHolders()));
    }

    private GetBoardGroupPostResponse callApiAndGetResponse(String boardGroupName, String appVersion, ResultMatcher resultMatcher) throws Exception {
        return callApiAndGetResponse(boardGroupName, null, appVersion, resultMatcher);
    }

    private GetBoardGroupPostResponse callApiAndGetResponse(
        String boardGroupName,
        String boardCategoryName,
        String appVersion,
        ResultMatcher resultMatcher
    ) throws Exception {
        MvcResult result = callApi(boardGroupName, boardCategoryName, appVersion, resultMatcher);

        return itUtil.getResult(result, GetBoardGroupPostResponse.class);
    }

    private MvcResult callApi(String boardGroupName, String boardCategoryName, String appVersion, ResultMatcher resultMatcher) throws Exception {
        final String boardCategory = Optional.ofNullable(boardCategoryName).orElse("");

        String boardCategories = "";
        if (boardCategory.equals(EVENT_CAMPAIGN)) {
            boardCategories = boardCategory;
        }

        Map<String, Object> params = Map.of(
            "sorts", CREATED_AT_DESC,
            "boardCategory", boardCategory,
            "boardCategories", boardCategories,
            "isExclusiveToHolders", Boolean.FALSE);

        return mockMvc.perform(
                get(TARGET_API, globalStockCode, boardGroupName)
                    .params(toMultiValueMap(params))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header(X_APP_VERSION, appVersion)
            )
            .andExpect(resultMatcher)
            .andReturn();
    }
}
