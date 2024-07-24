package ag.act.api.stockboardgrouppost;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.TestUtil;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.AppPreferenceType;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.virtualboard.VirtualBoardCategory;
import ag.act.enums.virtualboard.VirtualBoardGroup;
import ag.act.model.GetBoardGroupPostResponse;
import ag.act.model.Paging;
import ag.act.model.PostResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static ag.act.TestUtil.someBoardGroupCategoryForGlobal;
import static ag.act.TestUtil.someBoardGroupCategoryForStock;
import static ag.act.TestUtil.someVirtualBoardCategory;
import static ag.act.TestUtil.toMultiValueMap;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomThings.someThing;

@SuppressWarnings("checkstyle:LineLength")
class GetPostsSortingApiIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts";
    private static final String VIEW_COUNT_DESC = "viewUserCount:DESC";
    private static final String LIKE_COUNT_DESC = "likeCount:DESC";
    private static final String IMPOSSIBLE_COLUMN_ASC = "impossibleColumn:ASC";
    private static final int MAX_PAGE_SIZE = 100;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private final String defaultAppVersion = AppPreferenceType.MIN_APP_VERSION.getDefaultValue();

    private User currentUser;
    private String jwt;
    private Stock stock;
    private User writer;
    private Post[] posts;
    private int pageSize;

    @BeforeEach
    void setUp() {
        itUtil.init();
        dbCleaner.clean();

        currentUser = itUtil.createUser();
        jwt = itUtil.createJwt(currentUser.getId());
        writer = itUtil.createUser();

        pageSize = someIntegerBetween(DEFAULT_PAGE_SIZE, MAX_PAGE_SIZE);
    }

    @Nested
    @DisplayName("액트 베스트가 아닌 게시판을 조회할 때")
    class GetNotVirtualBoardGroupPosts {

        @DirtiesContext
        @Nested
        @DisplayName("글로벌 게시판의 게시글 목록을 조회할 때")
        class GetGlobalBoardGroupPosts {
            private Board board;

            @BeforeEach
            void setUp() {
                createAndSetGlobalStock();
                createAndSetGlobalBoard();
            }

            @Nested
            @DisplayName("최신순으로 정렬하는 경우")
            class AndSortedByCreatedAt {
                @BeforeEach
                void setUp() {
                    posts = sortPosts(new Post[] {
                        createPost(board),
                        createPost(board),
                        createPost(board),
                        createPost(board)
                    });
                }

                @Test
                @DisplayName("최신순으로 정렬된 게시글 목록을 조회한다.")
                void shouldReturnSuccess() throws Exception {
                    final GetBoardGroupPostResponse result = callApiAndGetResponse(
                        board.getGroup().name(),
                        board.getCategory().name(),
                        CREATED_AT_DESC,
                        pageSize
                    );

                    assertPaging(result.getPaging());
                    assertPosts(result.getData());
                }
            }

            @Nested
            @DisplayName("조회수 내림차순으로 정렬하는 경우")
            class AndSortedByViewCount {
                @BeforeEach
                void setUp() {
                    posts = sortPosts(new Post[] {
                        createPostWithViewCount(board, 400L),
                        createPostWithViewCount(board, 300L),
                        createPostWithViewCount(board, 200L),
                        createPostWithViewCount(board, 200L)
                    });
                }

                @Test
                @DisplayName("조회수 내림차순으로 정렬된 게시글 목록을 조회한다.")
                void shouldReturnSuccess() throws Exception {
                    final GetBoardGroupPostResponse result = callApiAndGetResponse(
                        board.getGroup().name(),
                        board.getCategory().name(),
                        VIEW_COUNT_DESC,
                        pageSize
                    );

                    assertPaging(result.getPaging());
                    assertPosts(result.getData());
                }
            }

            @Nested
            @DisplayName("좋아요 내림차순으로 정렬하는 경우")
            class AndSortedByLikeCount {
                @BeforeEach
                void setUp() {
                    posts = sortPosts(new Post[] {
                        createPostWithLikeCount(board, 400L),
                        createPostWithLikeCount(board, 300L),
                        createPostWithLikeCount(board, 200L),
                        createPostWithLikeCount(board, 200L)
                    });
                }

                @Test
                @DisplayName("좋아요 내림차순으로 정렬된 게시글 목록을 조회한다.")
                void shouldReturnSuccess() throws Exception {
                    final GetBoardGroupPostResponse result = callApiAndGetResponse(
                        board.getGroup().name(),
                        board.getCategory().name(),
                        LIKE_COUNT_DESC,
                        pageSize
                    );

                    assertPaging(result.getPaging());
                    assertPosts(result.getData());
                }
            }

            @Nested
            @DisplayName("정렬할 수 없는 컬럼으로 정렬하는 경우")
            class AndSortedByImPossibleSortColumn {
                @BeforeEach
                void setUp() {
                    posts = sortPosts(new Post[] {
                        createPost(board),
                        createPost(board),
                        createPost(board),
                        createPost(board)
                    });
                }

                @Test
                @DisplayName("최신순으로 정렬된 게시글 목록을 조회한다.")
                void shouldReturnSuccess() throws Exception {
                    final GetBoardGroupPostResponse result = callApiAndGetResponse(
                        board.getGroup().name(),
                        board.getCategory().name(),
                        IMPOSSIBLE_COLUMN_ASC,
                        pageSize
                    );

                    assertPaging(result.getPaging());
                    assertPosts(result.getData());
                }
            }

            @Nested
            @DisplayName("100개 초과하여 조회하는경우")
            class AndExceeding100Posts {
                @BeforeEach
                void setUp() {
                    posts = sortPosts(new Post[] {
                        createPost(board),
                        createPost(board),
                        createPost(board),
                        createPost(board)
                    });
                    pageSize = someIntegerBetween(MAX_PAGE_SIZE + 1, 500);
                }

                @Test
                @DisplayName("한 페이지에 게시글 10개를 조회한다.")
                void shouldReturnSuccess() throws Exception {
                    final GetBoardGroupPostResponse result = callApiAndGetResponse(
                        board.getGroup().name(),
                        board.getCategory().name(),
                        CREATED_AT_DESC,
                        pageSize
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
        @DisplayName("종목 게시판의 게시글 목록을 조회할 때")
        class GetStockBoardGroupPosts {
            private Board board;

            @BeforeEach
            void setUp() {
                createAndSetNotGlobalStock();
                createAndSetNotGlobalBoard();
            }

            @Nested
            @DisplayName("최신순으로 정렬하는 경우")
            class AndSortedByCreatedAt {
                @BeforeEach
                void setUp() {
                    posts = sortPosts(new Post[] {
                        createPost(board),
                        createPost(board),
                        createPost(board),
                        createPost(board)
                    });
                }

                @Test
                @DisplayName("최신순으로 정렬된 게시글 목록을 조회한다.")
                void shouldReturnSuccess() throws Exception {
                    final GetBoardGroupPostResponse result = callApiAndGetResponse(
                        board.getGroup().name(),
                        board.getCategory().name(),
                        CREATED_AT_DESC,
                        pageSize
                    );

                    assertPaging(result.getPaging());
                    assertPosts(result.getData());
                }
            }

            @Nested
            @DisplayName("조회수 내림차순으로 정렬하는 경우")
            class AndSortedByViewCount {
                @BeforeEach
                void setUp() {
                    posts = sortPosts(new Post[] {
                        createPostWithViewCount(board, 400L),
                        createPostWithViewCount(board, 300L),
                        createPostWithViewCount(board, 200L),
                        createPostWithViewCount(board, 200L)
                    });
                }

                @Test
                @DisplayName("조회수 내림차순으로 정렬된 게시글 목록을 조회한다.")
                void shouldReturnSuccess() throws Exception {
                    final GetBoardGroupPostResponse result = callApiAndGetResponse(
                        board.getGroup().name(),
                        board.getCategory().name(),
                        VIEW_COUNT_DESC,
                        pageSize
                    );

                    assertPaging(result.getPaging());
                    assertPosts(result.getData());
                }
            }

            @Nested
            @DisplayName("좋아요 내림차순으로 정렬하는 경우")
            class AndSortedByLikeCount {
                @BeforeEach
                void setUp() {
                    posts = sortPosts(new Post[] {
                        createPostWithLikeCount(board, 400L),
                        createPostWithLikeCount(board, 300L),
                        createPostWithLikeCount(board, 200L),
                        createPostWithLikeCount(board, 200L)
                    });
                }

                @Test
                @DisplayName("좋아요 내림차순으로 정렬된 게시글 목록을 조회한다.")
                void shouldReturnSuccess() throws Exception {
                    final GetBoardGroupPostResponse result = callApiAndGetResponse(
                        board.getGroup().name(),
                        board.getCategory().name(),
                        LIKE_COUNT_DESC,
                        pageSize
                    );

                    assertPaging(result.getPaging());
                    assertPosts(result.getData());
                }
            }

            @Nested
            @DisplayName("정렬할 수 없는 컬럼으로 정렬하는 경우")
            class AndSortedByImPossibleSortColumn {
                @BeforeEach
                void setUp() {
                    posts = sortPosts(new Post[] {
                        createPost(board),
                        createPost(board),
                        createPost(board),
                        createPost(board)
                    });
                }

                @Test
                @DisplayName("최신순으로 정렬된 게시글 목록을 조회한다.")
                void shouldReturnSuccess() throws Exception {
                    final GetBoardGroupPostResponse result = callApiAndGetResponse(
                        board.getGroup().name(),
                        board.getCategory().name(),
                        IMPOSSIBLE_COLUMN_ASC,
                        pageSize
                    );

                    assertPaging(result.getPaging());
                    assertPosts(result.getData());
                }
            }

            @Nested
            @DisplayName("100개 초과하여 조회하는경우")
            class AndExceeding100Posts {
                @BeforeEach
                void setUp() {
                    posts = sortPosts(new Post[] {
                        createPost(board),
                        createPost(board),
                        createPost(board),
                        createPost(board)
                    });
                    pageSize = someIntegerBetween(MAX_PAGE_SIZE + 1, 500);
                }

                @Test
                @DisplayName("한 페이지에 게시글 10개를 조회한다.")
                void shouldReturnSuccess() throws Exception {
                    final GetBoardGroupPostResponse result = callApiAndGetResponse(
                        board.getGroup().name(),
                        board.getCategory().name(),
                        CREATED_AT_DESC,
                        pageSize
                    );

                    assertPaging(result.getPaging());
                    assertPosts(result.getData());
                }
            }

            private void createAndSetNotGlobalBoard() {
                TestUtil.BoardGroupCategory stockBoardGroupCategory = someBoardGroupCategoryForStock();
                board = getBoard(stockBoardGroupCategory.boardGroup(), stockBoardGroupCategory.boardCategory());
            }
        }

        private Post createPost(Board board) {
            Post post = itUtil.createPost(board, writer.getId());
            return updatePost(post, 0L, 0L);
        }

        private Post createPostWithViewCount(Board board, long viewCount) {
            Post post = itUtil.createPost(board, writer.getId());
            post.setViewCount(viewCount);
            return updatePost(post, viewCount, 0L);
        }

        private Post createPostWithLikeCount(Board board, long likeCount) {
            Post post = itUtil.createPostWithLikeCount(board, writer.getId(), likeCount);
            return updatePost(post, 0L, likeCount);
        }
    }

    @Nested
    @DisplayName("액트 베스트 게시판의 게시글 목록을 조회할 때")
    class GetVirtualBoardGroupActBestPosts {
        private final long bestPostLikeCountCriteria = 10L;
        private Board board1;
        private Board board2;
        private VirtualBoardCategory virtualBoardCategory;
        private int beforeTotalElements;


        @BeforeEach
        void setUp() {
            virtualBoardCategory = someVirtualBoardCategory(VirtualBoardGroup.ACT_BEST);
            BoardCategory boardCategory1 = someThing(virtualBoardCategory.getSubCategories().toArray(BoardCategory[]::new));
            BoardCategory boardCategory2 = someThing(virtualBoardCategory.getSubCategories().toArray(BoardCategory[]::new));
            BoardGroup boardGroup = boardCategory1.getBoardGroup();

            createAndSetStock(boardGroup);

            board1 = getBoard(boardCategory1.getBoardGroup(), boardCategory1);
            board2 = getBoard(boardCategory2.getBoardGroup(), boardCategory2);
            beforeTotalElements = itUtil.countBestPosts(virtualBoardCategory.getSubCategories());
        }

        private void createAndSetStock(BoardGroup boardGroup) {
            if (boardGroup.isGlobal()) {
                createAndSetGlobalStock();
                return;
            }
            createAndSetNotGlobalStock();
        }

        @Nested
        @DisplayName("최신순으로 정렬하는 경우")
        class AndSortedByCreatedAt {
            @BeforeEach
            void setUp() {
                posts = sortPosts(new Post[] {
                    createPost(board1),
                    createPost(board1),
                    createPost(board2),
                    createPost(board2)
                });
            }

            @Test
            @DisplayName("최신순으로 정렬된 게시글 목록을 조회한다.")
            void shouldReturnSuccess() throws Exception {
                final GetBoardGroupPostResponse result = callApiAndGetResponse(
                    VirtualBoardGroup.ACT_BEST.name(),
                    virtualBoardCategory.name(),
                    CREATED_AT_DESC,
                    pageSize
                );

                assertPaging(result.getPaging());
                assertPosts(result.getData());
            }
        }

        @Nested
        @DisplayName("조회수 내림차순으로 정렬하는 경우")
        class AndSortedByViewCount {
            @BeforeEach
            void setUp() {
                posts = sortPosts(new Post[] {
                    createPostWithViewCount(board1, 400L),
                    createPostWithViewCount(board1, 300L),
                    createPostWithViewCount(board2, 200L),
                    createPostWithViewCount(board2, 200L)
                });
            }

            @Test
            @DisplayName("조회수 내림차순으로 정렬된 게시글 목록을 조회한다.")
            void shouldReturnSuccess() throws Exception {
                final GetBoardGroupPostResponse result = callApiAndGetResponse(
                    VirtualBoardGroup.ACT_BEST.name(),
                    virtualBoardCategory.name(),
                    VIEW_COUNT_DESC,
                    pageSize
                );

                assertPaging(result.getPaging());
                assertPosts(result.getData());
            }
        }

        @Nested
        @DisplayName("좋아요 내림차순으로 정렬하는 경우")
        class AndSortedByLikeCount {
            @BeforeEach
            void setUp() {
                posts = sortPosts(new Post[] {
                    createPostWithLikeCount(board1, bestPostLikeCountCriteria + 400L),
                    createPostWithLikeCount(board1, bestPostLikeCountCriteria + 300L),
                    createPostWithLikeCount(board2, bestPostLikeCountCriteria + 200L),
                    createPostWithLikeCount(board2, bestPostLikeCountCriteria + 200L)
                });
            }

            @Test
            @DisplayName("좋아요 내림차순으로 정렬된 게시글 목록을 조회한다.")
            void shouldReturnSuccess() throws Exception {
                final GetBoardGroupPostResponse result = callApiAndGetResponse(
                    VirtualBoardGroup.ACT_BEST.name(),
                    virtualBoardCategory.name(),
                    LIKE_COUNT_DESC,
                    pageSize
                );

                assertPaging(result.getPaging());
                assertPosts(result.getData());
            }
        }

        @Nested
        @DisplayName("정렬할 수 없는 컬럼으로 정렬하는 경우")
        class AndSortedByImPossibleSortColumn {
            @BeforeEach
            void setUp() {
                posts = sortPosts(new Post[] {
                    createPost(board1),
                    createPost(board1),
                    createPost(board2),
                    createPost(board2)
                });
            }

            @Test
            @DisplayName("최신순으로 정렬된 게시글 목록을 조회한다.")
            void shouldReturnSuccess() throws Exception {
                final GetBoardGroupPostResponse result = callApiAndGetResponse(
                    VirtualBoardGroup.ACT_BEST.name(),
                    virtualBoardCategory.name(),
                    IMPOSSIBLE_COLUMN_ASC,
                    pageSize
                );

                assertPaging(result.getPaging());
                assertPosts(result.getData());
            }
        }

        @Nested
        @DisplayName("100개 초과하여 조회하는경우")
        class AndExceeding100Posts {
            @BeforeEach
            void setUp() {
                posts = sortPosts(new Post[] {
                    createPost(board1),
                    createPost(board1),
                    createPost(board2),
                    createPost(board2)
                });
                pageSize = someIntegerBetween(MAX_PAGE_SIZE + 1, 500);
            }

            @Test
            @DisplayName("한 페이지에 게시글 10개를 조회한다.")
            void shouldReturnSuccess() throws Exception {
                final GetBoardGroupPostResponse result = callApiAndGetResponse(
                    VirtualBoardGroup.ACT_BEST.name(),
                    virtualBoardCategory.name(),
                    CREATED_AT_DESC,
                    pageSize
                );

                assertPaging(result.getPaging());
                assertPosts(result.getData());
            }
        }

        private Post createPost(Board board) {
            Post post = itUtil.createPost(board, writer.getId());
            return updatePost(post, 0L, bestPostLikeCountCriteria);
        }

        private Post createPostWithViewCount(Board board, long viewCount) {
            Post post = itUtil.createPost(board, writer.getId());
            post.setViewCount(viewCount);
            return updatePost(post, viewCount, bestPostLikeCountCriteria);
        }

        private Post createPostWithLikeCount(Board board, long likeCount) {
            Post post = itUtil.createPostWithLikeCount(board, writer.getId(), likeCount);
            return updatePost(post, 0L, likeCount);
        }

        private void assertPaging(Paging paging) {
            assertThat(paging.getSize(), is(getExpectedPageSize(pageSize)));
            assertThat(paging.getTotalElements(), is(beforeTotalElements + (long) posts.length));
        }

        private void assertPosts(List<PostResponse> postResponses) {
            for (int i = 0; i < posts.length; i++) {
                assertPostResponse(postResponses.get(i), posts[i]);
            }
        }
    }

    private void createAndSetNotGlobalStock() {
        stock = itUtil.createStock();
        itUtil.createUserHoldingStock(stock.getCode(), currentUser);
        itUtil.createUserHoldingStock(stock.getCode(), writer);
    }

    private void createAndSetGlobalStock() {
        stock = itUtil.createStock();
    }

    private Board getBoard(BoardGroup boardGroup, BoardCategory boardCategory) {
        final Optional<Board> boardOptional = itUtil.findBoard(stock.getCode(), boardCategory);
        return boardOptional.orElseGet(() -> itUtil.createBoard(stock, boardGroup, boardCategory));
    }

    private Post updatePost(Post post, long viewCount, long likeCount) {
        post.setLikeCount(likeCount);
        post.setViewCount(viewCount);
        return itUtil.updatePost(post);
    }

    private Post[] sortPosts(Post[] posts) {
        Arrays.sort(
            posts,
            Comparator.comparingLong(Post::getLikeCount)
                .thenComparingLong(Post::getViewCount)
                .thenComparing(Post::getCreatedAt)
                .reversed() // 내림차순으로 변경
        );
        return posts;
    }

    private void assertPaging(Paging paging) {
        assertThat(paging.getSize(), is(getExpectedPageSize(pageSize)));
        assertThat(paging.getTotalElements(), is((long) posts.length));
    }

    private void assertPosts(List<PostResponse> postResponses) {
        assertThat(postResponses.size(), is(posts.length));

        for (int i = 0; i < posts.length; i++) {
            assertPostResponse(postResponses.get(i), posts[i]);
        }
    }

    private int getExpectedPageSize(int size) {
        return size > MAX_PAGE_SIZE ? DEFAULT_PAGE_SIZE : size;
    }

    private void assertPostResponse(PostResponse actual, Post expected) {
        final Board expectedBoard = expected.getBoard();

        assertThat(actual.getId(), is(expected.getId()));
        assertThat(actual.getStock().getCode(), is(expectedBoard.getStock().getCode()));
        assertThat(actual.getBoardId(), is(expectedBoard.getId()));
        assertThat(actual.getTitle(), is(expected.getTitle()));
        assertThat(actual.getStatus(), is(expected.getStatus()));
        assertThat(actual.getLikeCount(), is(expected.getLikeCount()));
        assertThat(actual.getViewCount(), is(expected.getViewCount()));
        assertThat(actual.getIsExclusiveToHolders(), is(expected.getIsExclusiveToHolders()));
    }

    private GetBoardGroupPostResponse callApiAndGetResponse(
        String boardGroupName,
        String boardCategoryName,
        String sort,
        int size
    ) throws Exception {
        Map<String, Object> params = Map.of(
            "sorts", List.of(sort, CREATED_AT_DESC),
            "page", PAGE_1,
            "size", getExpectedPageSize(size),
            "boardCategory", boardCategoryName,
            "isExclusiveToHolders", Boolean.FALSE);

        return itUtil.getResult(callApi(boardGroupName, params), GetBoardGroupPostResponse.class);
    }

    private MvcResult callApi(String boardGroupName, Map<String, Object> params) throws Exception {
        return mockMvc.perform(
                get(TARGET_API, stock.getCode(), boardGroupName)
                    .params(toMultiValueMap(params))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header(X_APP_VERSION, defaultAppVersion)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(status().isOk())
            .andReturn();
    }
}