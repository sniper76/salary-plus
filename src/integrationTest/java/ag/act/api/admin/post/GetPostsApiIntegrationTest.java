package ag.act.api.admin.post;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentDownload;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.DigitalDocumentAnswerStatus;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.admin.PostSearchType;
import ag.act.itutil.holder.PostsByBoardGroupTestHolder;
import ag.act.model.DigitalDocumentDownloadResponse;
import ag.act.model.GetBoardGroupPostResponse;
import ag.act.model.Paging;
import ag.act.model.PostResponse;
import ag.act.model.Status;
import ag.act.model.UserDigitalDocumentResponse;
import ag.act.util.ZoneIdUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static ag.act.TestUtil.assertTime;
import static ag.act.TestUtil.someLocalDateTimeInTheFuture;
import static ag.act.TestUtil.somePostStatus;
import static ag.act.TestUtil.toMultiValueMap;
import static ag.act.enums.admin.PostSearchType.CONTENT;
import static ag.act.enums.admin.PostSearchType.TITLE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class GetPostsApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/board-groups/{boardGroup}/posts";

    private String jwt;
    private BoardGroup boardGroup;
    private Map<String, Object> params;
    private Stock stock;
    private Board board1;
    private Board board2;
    private Integer pageNumber;
    private Post post1;
    private Post post2;
    private Post post3;
    private Post post4ExclusiveToHolders;
    private Post post5;
    private String searchKeyword;
    private static final PostsByBoardGroupTestHolder postsByBoardGroupTestHolder = new PostsByBoardGroupTestHolder();
    private Status status;
    private BoardCategory boardCategory;
    private String zipFileKey;

    @BeforeEach
    void setUp() {
        itUtil.init();
        postsByBoardGroupTestHolder.initialize(
            itUtil.findAllPosts()
        );
        final User adminUser = itUtil.createAdminUser();
        jwt = itUtil.createJwt(adminUser.getId());
    }

    @Nested
    class WhenRandomBoardGroup {

        @BeforeEach
        void setUp() {
            searchKeyword = someAlphanumericString(10);

            final User user = itUtil.createUser();
            boardGroup = someEnum(BoardGroup.class);
            stock = itUtil.createStock();
            board1 = itUtil.createBoard(stock, boardGroup, boardGroup.getCategories().get(0));
            itUtil.createSolidarity(stock.getCode());
            itUtil.createUserHoldingStock(stock.getCode(), user);

            post1 = createPost(user, searchKeyword, board1);
            post2 = createPost(user, searchKeyword, board1);
            post3 = createPost(user, someString(10), board1);
            post4ExclusiveToHolders = setIsExclusiveToHoldersTrue(createPost(user, someString(10), board1));
        }

        @Nested
        class WhenSearchWithoutParams {

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_1;
                params = Map.of(
                    "page", pageNumber.toString(),
                    "size", SIZE.toString()
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnPosts() throws Exception {
                final long totalElements = postsByBoardGroupTestHolder.getPosts(boardGroup).size();
                assertResponse(callApiAndGetResult(boardGroup.name()), totalElements);
            }

            private void assertResponse(ag.act.model.GetBoardGroupPostResponse result, long totalElements) {
                final ag.act.model.Paging paging = result.getPaging();
                final List<ag.act.model.PostResponse> postResponses = result.getData();

                assertPaging(paging, totalElements);
                assertThat(postResponses.size(), is(SIZE));
                assertPostResponse(post4ExclusiveToHolders, postResponses.get(0), stock.getCode(), board1.getId());
                assertPostResponse(post3, postResponses.get(1), stock.getCode(), board1.getId());
            }
        }

        @Nested
        class WhenSearchWithPostTitleWithoutSearchType {

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_1;
                params = Map.of(
                    "searchKeyword", searchKeyword,
                    "page", pageNumber.toString(),
                    "size", SIZE.toString()
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnPosts() throws Exception {
                assertResponse(callApiAndGetResult(boardGroup.name()), 2L);
            }
        }
    }

    @Nested
    class WhenActionBoardGroup {

        @BeforeEach
        void setUp() {
            searchKeyword = someAlphanumericString(10);

            final User user = itUtil.createUser();
            boardGroup = someEnum(BoardGroup.class);
            stock = itUtil.createStock();
            board1 = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.SURVEYS);
            board2 = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.ETC);
            itUtil.createSolidarity(stock.getCode());
            itUtil.createUserHoldingStock(stock.getCode(), user);

            String zipFilePath = someAlphanumericString(10);
            zipFileKey = someAlphanumericString(10);

            post1 = createPost(user, searchKeyword, board1);
            post2 = createPost(user, searchKeyword, board1);
            post3 = createPost(user, someString(10), board1);
            post4ExclusiveToHolders = createPost(user, someString(10), board1);
            post5 = createPostWithDigitalDocument(user, someString(10), zipFileKey, zipFilePath, board2, stock);
        }

        @Nested
        class WhenPostsDigitalDocumentDownloadPath {

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_1;
                params = Map.of(
                    "page", pageNumber.toString(),
                    "size", SIZE.toString()
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnPosts() throws Exception {
                final long totalElements = postsByBoardGroupTestHolder.getPosts(BoardGroup.ACTION).size();
                assertResponse(callApiAndGetResult(BoardGroup.ACTION.name()), totalElements);
            }

            private void assertResponse(ag.act.model.GetBoardGroupPostResponse result, long totalElements) {
                final ag.act.model.Paging paging = result.getPaging();
                final List<ag.act.model.PostResponse> postResponses = result.getData();

                assertPaging(paging, totalElements);
                assertThat(postResponses.size(), is(SIZE));
                assertPostWithDigitalDocumentResponse(post5, postResponses.get(0));
                assertPostResponse(post4ExclusiveToHolders, postResponses.get(1), stock.getCode(), board1.getId());
            }
        }
    }

    @Nested
    class WhenMultipleBoardCategories {

        @BeforeEach
        void setUp() {
            searchKeyword = someAlphanumericString(10);

            final User user = itUtil.createUser();
            boardGroup = randomBoardGroupThatHasMultipleCategories();
            stock = itUtil.createStock();
            board1 = itUtil.createBoard(stock, boardGroup, boardGroup.getCategories().get(0));

            post1 = createPost(user, searchKeyword, board1);
            post2 = createPost(user, searchKeyword, board1);
            post3 = createPost(user, someString(10), board1);
            post4ExclusiveToHolders = createPost(user, someString(10), board1);
        }

        @Nested
        class WhenSearchWithoutParams {

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_1;
                params = Map.of(
                    "page", pageNumber.toString(),
                    "size", SIZE.toString()
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnPosts() throws Exception {
                final long totalElements = postsByBoardGroupTestHolder.getPosts(boardGroup).size();
                assertResponse(callApiAndGetResult(boardGroup.name()), totalElements);
            }

            private void assertResponse(ag.act.model.GetBoardGroupPostResponse result, long totalElements) {
                final ag.act.model.Paging paging = result.getPaging();
                final List<ag.act.model.PostResponse> postResponses = result.getData();

                assertPaging(paging, totalElements);
                assertThat(postResponses.size(), is(SIZE));
                assertPostResponse(post4ExclusiveToHolders, postResponses.get(0), stock.getCode(), board1.getId());
                assertPostResponse(post3, postResponses.get(1), stock.getCode(), board1.getId());
            }
        }

        @Nested
        class WhenSearchWithPostTitle {

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_1;
                params = Map.of(
                    "searchType", TITLE.name(),
                    "searchKeyword", searchKeyword,
                    "page", pageNumber.toString(),
                    "size", SIZE.toString()
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnPosts() throws Exception {
                assertResponse(callApiAndGetResult(boardGroup.name()), 2L);
            }
        }

        @Nested
        class WhenSearchWithCategory {

            @BeforeEach
            void setUp() {
                final User user = itUtil.createUser();
                boardCategory = boardGroup.getCategories().get(1);
                board1 = createOrGetBoard(stock, boardGroup, boardCategory);

                post1 = createPost(user, someString(10), board1);
                post2 = createPost(user, someString(10), board1);

                pageNumber = PAGE_1;
                params = Map.of(
                    "boardCategory", boardCategory.name(),
                    "page", pageNumber.toString(),
                    "size", SIZE.toString()
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnPosts() throws Exception {
                final long totalElements = postsByBoardGroupTestHolder.getPosts(boardGroup)
                    .stream()
                    .filter(post -> post.getBoard().getCategory() == boardCategory)
                    .count();

                assertResponse(callApiAndGetResult(boardGroup.name()), totalElements);
            }
        }

        @Nested
        class WhenSearchWithInvalidCategory {
            private static Stream<Arguments> valueProvider() {
                return Stream.of(
                    Arguments.of("All"),
                    Arguments.of("all"),
                    Arguments.of("ALL"),
                    Arguments.of(someAlphanumericString(5)),
                    Arguments.of(someAlphanumericString(15))
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @ParameterizedTest(name = "{index} => boardCategoryName=''{0}''")
            @MethodSource("valueProvider")
            void shouldReturnAllPostsWithoutCategory(String boardCategoryName) throws Exception {
                // Given
                pageNumber = PAGE_1;
                params = Map.of(
                    "boardCategory", boardCategoryName,
                    "page", pageNumber.toString(),
                    "size", SIZE.toString()
                );

                // When
                final long totalElements = postsByBoardGroupTestHolder.getPosts(boardGroup).size();

                // Then
                assertResponse(callApiAndGetResult(boardGroup.name()), totalElements);
            }

            private void assertResponse(ag.act.model.GetBoardGroupPostResponse result, long totalElements) {
                final ag.act.model.Paging paging = result.getPaging();
                final List<ag.act.model.PostResponse> postResponses = result.getData();

                assertPaging(paging, totalElements);
                assertThat(postResponses.size(), is(SIZE));
                assertPostResponse(post4ExclusiveToHolders, postResponses.get(0), stock.getCode(), board1.getId());
                assertPostResponse(post3, postResponses.get(1), stock.getCode(), board1.getId());
            }
        }

        @Nested
        class WhenSearchWithStatus {

            @BeforeEach
            void setUp() {
                final User user = itUtil.createUser();
                boardCategory = boardGroup.getCategories().get(1);
                board1 = createOrGetBoard(stock, boardGroup, boardCategory);

                status = somePostStatus();
                post1 = createPost(user, someString(10), board1, status);
                post2 = createPost(user, someString(10), board1, status);

                pageNumber = PAGE_1;
                params = Map.of(
                    "status", status.name(),
                    "page", pageNumber.toString(),
                    "size", SIZE.toString()
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @RepeatedTest(3)
            void shouldReturnPosts() throws Exception {
                final long totalElements = postsByBoardGroupTestHolder.getPosts(boardGroup)
                    .stream()
                    .filter(post -> post.getStatus() == status)
                    .count();

                assertResponse(callApiAndGetResult(boardGroup.name()), totalElements);
            }
        }

        @Nested
        class WhenSearchWithStatusAndWithCategory {

            @BeforeEach
            void setUp() {
                final User user = itUtil.createUser();
                boardCategory = boardGroup.getCategories().get(1);
                board1 = createOrGetBoard(stock, boardGroup, boardCategory);
                status = somePostStatus();

                post1 = createPost(user, someString(10), board1, status);
                post2 = createPost(user, someString(10), board1, status);

                pageNumber = PAGE_1;
                params = Map.of(
                    "searchType", "",
                    "searchKeyword", "",
                    "boardCategory", boardCategory.name(),
                    "status", status.name(),
                    "page", pageNumber.toString(),
                    "size", SIZE.toString()
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnPosts() throws Exception {
                final long totalElements = postsByBoardGroupTestHolder.getPosts(boardGroup)
                    .stream()
                    .filter(post -> post.getBoard().getCategory() == boardCategory)
                    .filter(post -> post.getStatus() == status)
                    .count();

                assertResponse(callApiAndGetResult(boardGroup.name()), totalElements);
            }
        }

        @Nested
        class WhenSearchWithStatusAndWithCategoryAndWithTitle {

            @BeforeEach
            void setUp() {
                final User user = itUtil.createUser();
                boardCategory = boardGroup.getCategories().get(1);
                board1 = createOrGetBoard(stock, boardGroup, boardCategory);
                status = somePostStatus();
                searchKeyword = someAlphanumericString(10);

                post1 = createPost(user, searchKeyword, board1, status);
                post2 = createPost(user, searchKeyword, board1, status);

                pageNumber = PAGE_1;
                params = Map.of(
                    "searchType", TITLE.name(),
                    "searchKeyword", searchKeyword,
                    "boardCategory", boardCategory.name(),
                    "status", status.name(),
                    "page", pageNumber.toString(),
                    "size", SIZE.toString()
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnPosts() throws Exception {
                assertResponse(callApiAndGetResult(boardGroup.name()), 2L);
            }
        }

        @Nested
        class WhenSearchWithStatusAndWithCategoryAndWithStockCodeInFirstPage {

            @BeforeEach
            void setUp() {
                final User user = itUtil.createUser();
                boardCategory = boardGroup.getCategories().get(1);
                board1 = createOrGetBoard(stock, boardGroup, boardCategory);
                status = somePostStatus();
                searchKeyword = stock.getCode();// stockCode for search

                post1 = createPost(user, someString(10), board1, status);
                post2 = createPost(user, someString(10), board1, status);

                pageNumber = PAGE_1;
                params = Map.of(
                    "searchType", PostSearchType.STOCK_CODE.name(),
                    "searchKeyword", searchKeyword,
                    "boardCategory", boardCategory.name(),
                    "status", status.name(),
                    "page", pageNumber.toString(),
                    "size", SIZE.toString()
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnPosts() throws Exception {
                assertResponse(callApiAndGetResult(boardGroup.name()), 2L);
            }
        }

        @Nested
        class WhenSearchWithStatusAndWithCategoryAndWithStockCodeInSecondPage {

            @BeforeEach
            void setUp() {
                final User user = itUtil.createUser();
                boardCategory = boardGroup.getCategories().get(1);
                board1 = createOrGetBoard(stock, boardGroup, boardCategory);
                status = somePostStatus();
                searchKeyword = stock.getCode();// stockCode for search

                post1 = createPost(user, someString(10), board1, status);
                post2 = createPost(user, someString(10), board1, status);
                post3 = createPost(user, someString(10), board1, status);
                post4ExclusiveToHolders = createPost(user, someString(10), board1, status);

                pageNumber = PAGE_2;
                params = Map.of(
                    "searchType", PostSearchType.STOCK_CODE.name(),
                    "searchKeyword", searchKeyword,
                    "boardCategory", boardCategory.name(),
                    "status", status.name(),
                    "page", pageNumber.toString(),
                    "size", SIZE.toString()
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnPosts() throws Exception {
                assertResponse(callApiAndGetResult(boardGroup.name()), 4L);
            }
        }
    }

    @Nested
    class WhenNoticeBoardCategory {

        @BeforeEach
        void setUp() {
            searchKeyword = someAlphanumericString(10);

            final User user = itUtil.createUser();
            boardGroup = BoardGroup.GLOBALEVENT;
            boardCategory = BoardCategory.NOTICE;

            stock = itUtil.createStock();
            board1 = itUtil.createBoard(stock, boardGroup, boardCategory);
            board2 = itUtil.createBoard(stock, boardGroup, BoardCategory.EVENT);
            itUtil.createSolidarity(stock.getCode());
            itUtil.createUserHoldingStock(stock.getCode(), user);

            post1 = createPost(user, searchKeyword, board1);
            post2 = createPost(user, someString(10), searchKeyword, board1);
            post3 = createPost(user, someString(10), searchKeyword, board1);
            post5 = createPost(user, searchKeyword, board1);
            post4ExclusiveToHolders = createPost(user, searchKeyword, board2);
        }

        @Nested
        class WhenWithoutParams {

            @Nested
            class WhenFirstPage {

                @BeforeEach
                void setUp() {
                    pageNumber = PAGE_1;
                    params = Map.of(
                        "size", SIZE,
                        "page", pageNumber,
                        "boardCategory", boardCategory.name()
                    );
                }

                @Test
                void shouldReturnPosts() throws Exception {
                    GetBoardGroupPostResponse response = callApiAndGetResult(boardGroup.name());
                    List<PostResponse> postResponses = response.getData();

                    final long totalElements = postsByBoardGroupTestHolder.getPosts(boardGroup)
                        .stream()
                        .filter(post -> post.getBoard().getCategory() == boardCategory)
                        .count();

                    assertPaging(response.getPaging(), totalElements);
                    assertPostResponse(post5, postResponses.get(0), stock.getCode(), board1.getId());
                    assertPostResponse(post3, postResponses.get(1), stock.getCode(), board1.getId());
                }
            }

            @Nested
            class WhenSecondPage {

                @BeforeEach
                void setUp() {
                    pageNumber = PAGE_2;
                    params = Map.of(
                        "size", SIZE,
                        "page", pageNumber,
                        "boardCategory", boardCategory.name()
                    );
                }

                @Test
                void shouldReturnPosts() throws Exception {
                    GetBoardGroupPostResponse response = callApiAndGetResult(boardGroup.name());
                    List<PostResponse> postResponses = response.getData();

                    final long totalElements = postsByBoardGroupTestHolder.getPosts(boardGroup)
                        .stream()
                        .filter(post -> post.getBoard().getCategory() == boardCategory)
                        .count();

                    assertPaging(response.getPaging(), totalElements);
                    assertPostResponse(post2, postResponses.get(0), stock.getCode(), board1.getId());
                    assertPostResponse(post1, postResponses.get(1), stock.getCode(), board1.getId());
                }
            }
        }

        @Nested
        class WhenSearchByTitle {

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_1;
                params = Map.of(
                    "size", SIZE,
                    "page", pageNumber,
                    "searchKeyword", searchKeyword,
                    "searchType", TITLE.name(),
                    "boardCategory", boardCategory.name()
                );
            }

            @Test
            void shouldReturnPosts() throws Exception {
                GetBoardGroupPostResponse response = callApiAndGetResult(boardGroup.name());
                List<PostResponse> postResponses = response.getData();

                assertPaging(response.getPaging(), 2L);
                assertPostResponse(post5, postResponses.get(0), stock.getCode(), board1.getId());
                assertPostResponse(post1, postResponses.get(1), stock.getCode(), board1.getId());
            }
        }

        @Nested
        class WhenSearchByContent {

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_1;
                params = Map.of(
                    "size", SIZE,
                    "page", pageNumber,
                    "searchKeyword", searchKeyword,
                    "searchType", CONTENT.name(),
                    "boardCategory", boardCategory.name()
                );
            }

            @Test
            void shouldReturnPosts() throws Exception {
                GetBoardGroupPostResponse response = callApiAndGetResult(boardGroup.name());
                List<PostResponse> postResponses = response.getData();

                assertPaging(response.getPaging(), 2L);
                assertPostResponse(post3, postResponses.get(0), stock.getCode(), board1.getId());
                assertPostResponse(post2, postResponses.get(1), stock.getCode(), board1.getId());
            }
        }

        @Nested
        class WhenSearchByTitleOrContent {

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_1;
                params = Map.of(
                    "size", SIZE,
                    "page", pageNumber,
                    "searchType", PostSearchType.TITLE_AND_CONTENT.name(),
                    "searchKeyword", searchKeyword,
                    "boardCategory", boardCategory.name()
                );
            }

            @Test
            void shouldReturnPosts() throws Exception {
                GetBoardGroupPostResponse response = callApiAndGetResult(boardGroup.name());
                List<PostResponse> postResponses = response.getData();

                assertPaging(response.getPaging(), 4L);
                assertPostResponse(post5, postResponses.get(0), stock.getCode(), board1.getId());
                assertPostResponse(post3, postResponses.get(1), stock.getCode(), board1.getId());
            }
        }

        @Nested
        class WhenSearchByPeriod {

            private LocalDateTime startLocalDateTime;
            private LocalDateTime endLocalDateTime;
            private Instant endSearchInstant;
            private Instant startSearchInstant;
            private Post post6;
            private Post post7;

            @BeforeEach
            void setUp() {
                endSearchInstant = Instant.now();
                startSearchInstant = endSearchInstant.minus(someIntegerBetween(1, 20), ChronoUnit.DAYS);

                endLocalDateTime = toLocalDateTime(endSearchInstant);
                startLocalDateTime = toLocalDateTime(startSearchInstant);

                final LocalDateTime beforeStartDateTime = startLocalDateTime.minusDays(someIntegerBetween(30, 100));

                cleanPosts(beforeStartDateTime);

                searchKeyword = someAlphanumericString(10);

                final User user = itUtil.createUser();
                boardGroup = BoardGroup.GLOBALEVENT;
                boardCategory = BoardCategory.NOTICE;

                stock = itUtil.createStock();
                board1 = itUtil.createBoard(stock, boardGroup, boardCategory);
                board2 = itUtil.createBoard(stock, boardGroup, BoardCategory.EVENT);
                itUtil.createSolidarity(stock.getCode());
                itUtil.createUserHoldingStock(stock.getCode(), user);

                // 기간 검색 요청 시 기간을 입력 안 할 경우, 아래 post 들은 post4ExclusiveToHolders 을 제외하고 모두 검색된다.

                post1 = createPost(user, searchKeyword, board1, someActiveStartDate()); // search O

                post2 = createPost(user, someString(10), board1, beforeStartDateTime); // search X
                post3 = createPost(user, searchKeyword, board1, beforeStartDateTime); // search X

                final LocalDateTime beforeSearchEndDateTime = endLocalDateTime.minusDays(1);
                post5 = createPost(user, someString(10), board1, someActiveStartDate()); // search O

                final LocalDateTime someActiveEndDate = endLocalDateTime.plusDays(someIntegerBetween(1, 30));
                post6 = createPost(user, someString(10), board1, beforeStartDateTime); // search O

                post4ExclusiveToHolders = createPost(user, searchKeyword, board2, someActiveStartDate()); // search X

                final LocalDateTime activeStartDateInFuture = someLocalDateTimeInTheFuture();
                post7 = createPost(user, searchKeyword, board1, activeStartDateInFuture); // search X
            }

            private LocalDateTime toLocalDateTime(Instant instant) {
                return instant.atZone(ZoneIdUtil.getSeoulZoneId()).toLocalDateTime();
            }

            private void cleanPosts(LocalDateTime beforeStartDateTime) {
                postsByBoardGroupTestHolder.getPosts(boardGroup)
                    .stream()
                    .peek(post -> post.setActiveStartDate(beforeStartDateTime))
                    .peek(post -> post.setActiveEndDate(beforeStartDateTime))
                    .forEach(post -> itUtil.updatePost(post));
            }

            @Nested
            class WhenWithoutParams {

                @BeforeEach
                void setUp() {
                    pageNumber = PAGE_1;
                    params = Map.of(
                        "size", SIZE,
                        "page", pageNumber,
                        "boardCategory", boardCategory.name()
                    );
                }

                @Test
                void shouldReturnPosts() throws Exception {
                    GetBoardGroupPostResponse response = callApiAndGetResult(boardGroup.name());
                    List<PostResponse> postResponses = response.getData();

                    final long totalElements = postsByBoardGroupTestHolder.getPosts(boardGroup)
                        .stream()
                        .filter(post -> post.getBoard().getCategory() == boardCategory)
                        .count();

                    List<Post> posts = Arrays.asList(post1, post2, post3, post5, post6, post7);
                    posts.sort(Comparator.comparing(Post::getCreatedAt).reversed());

                    assertPaging(response.getPaging(), totalElements);
                    assertPostResponse(posts.get(0), postResponses.get(0), stock.getCode(), board1.getId());
                    assertPostResponse(posts.get(1), postResponses.get(1), stock.getCode(), board1.getId());
                }
            }

            @Nested
            class WhenWithParameter {

                @BeforeEach
                void setUp() {
                    pageNumber = PAGE_1;
                    params = Map.of(
                        "size", SIZE,
                        "page", pageNumber,
                        "boardCategory", boardCategory.name(),
                        "searchStartDate", startSearchInstant,
                        "searchEndDate", endSearchInstant
                    );
                }

                @Test
                void shouldReturnPosts() throws Exception {
                    GetBoardGroupPostResponse response = callApiAndGetResult(boardGroup.name());
                    List<PostResponse> postResponses = response.getData();

                    List<Post> posts = postsByBoardGroupTestHolder.getPosts(boardGroup)
                        .stream()
                        .filter(post -> post.getBoard().getCategory() == boardCategory)
                        .filter(post -> isActive(post.getActiveStartDate()))
                        .filter(post -> isActiveNotEnded(post.getActiveEndDate()))
                        .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                        .toList();

                    assertPaging(response.getPaging(), posts.size());
                    assertPostResponse(posts.get(0), postResponses.get(0), stock.getCode(), board1.getId());
                    assertPostResponse(posts.get(1), postResponses.get(1), stock.getCode(), board1.getId());
                }

                private boolean isActive(LocalDateTime dateTime) {
                    return dateTime.isBefore(endLocalDateTime) || dateTime.isEqual(endLocalDateTime);
                }

                private boolean isActiveNotEnded(LocalDateTime dateTime) {
                    if (dateTime == null) {
                        return true;
                    }

                    return dateTime.isAfter(startLocalDateTime) || dateTime.isEqual(startLocalDateTime);
                }
            }

            private LocalDateTime someActiveStartDate() {
                return startLocalDateTime.minusDays(someIntegerBetween(0, 10));
            }
        }
    }

    @Nested
    class WhenActiveEndDateIsNotNull {

        @BeforeEach
        void setUp() {
            final User user = itUtil.createUser();
            boardGroup = BoardGroup.GLOBALEVENT;
            boardCategory = BoardCategory.NOTICE;

            stock = itUtil.createStock();
            board1 = itUtil.createBoard(stock, boardGroup, boardCategory);
            board2 = itUtil.createBoard(stock, boardGroup, BoardCategory.EVENT);
            itUtil.createSolidarity(stock.getCode());
            itUtil.createUserHoldingStock(stock.getCode(), user);

            post1 = createPost(user, board1, someLocalDateTimeInTheFuture());
            post2 = createPost(user, board1, someLocalDateTimeInTheFuture());
        }

        @Nested
        class WhenWithoutParams {

            @Nested
            class WhenFirstPage {

                @BeforeEach
                void setUp() {
                    pageNumber = PAGE_1;
                    params = Map.of(
                        "size", SIZE,
                        "page", pageNumber,
                        "boardCategory", boardCategory.name()
                    );
                }

                @Test
                void shouldReturnPosts() throws Exception {
                    GetBoardGroupPostResponse response = callApiAndGetResult(boardGroup.name());
                    List<PostResponse> postResponses = response.getData();

                    final long totalElements = postsByBoardGroupTestHolder.getPosts(boardGroup)
                        .stream()
                        .filter(post -> post.getBoard().getCategory() == boardCategory)
                        .count();

                    assertPaging(response.getPaging(), totalElements);
                    assertPostResponse(post2, postResponses.get(0), stock.getCode(), board1.getId());
                    assertPostResponse(post1, postResponses.get(1), stock.getCode(), board1.getId());
                }

                private void assertPostResponse(Post post, PostResponse postResponse, String stockCode, Long boardId) {
                    assertThat(postResponse.getId(), is(post.getId()));
                    assertThat(postResponse.getStock().getCode(), is(stockCode));
                    assertThat(postResponse.getBoardId(), is(boardId));
                    assertThat(postResponse.getStatus(), is(post.getStatus()));
                    assertThat(postResponse.getPoll(), is(nullValue()));
                    assertThat(postResponse.getIsAuthorAdmin(), is(false));
                    assertThat(postResponse.getReported(), is(false));
                    assertTime(postResponse.getActiveStartDate(), post.getActiveStartDate());
                    assertTime(postResponse.getActiveEndDate(), post.getActiveEndDate());

                    itUtil.assertPostTitleAndContentForAdmin(post, postResponse);
                }
            }
        }
    }

    private ag.act.model.GetBoardGroupPostResponse callApiAndGetResult(String boardGroup) throws Exception {
        MvcResult response = mockMvc
            .perform(
                get(TARGET_API, boardGroup)
                    .params(toMultiValueMap(params))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, "Bearer " + jwt)
                    .header(X_APP_VERSION, X_APP_VERSION_CMS)
            )
            .andExpect(status().isOk())
            .andReturn();

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            ag.act.model.GetBoardGroupPostResponse.class
        );
    }

    private BoardGroup randomBoardGroupThatHasMultipleCategories() {
        EnumSet<BoardGroup> boardGroups = EnumSet.allOf(BoardGroup.class);
        return boardGroups.stream().filter(boardGroup1 -> boardGroup1.getCategories().size() > 1).findFirst().orElseThrow();
    }

    private void assertPostResponse(Post post, PostResponse postResponse, String stockCode, Long boardId) {
        assertThat(postResponse.getId(), is(post.getId()));
        assertThat(postResponse.getStock().getCode(), is(stockCode));
        assertThat(postResponse.getBoardId(), is(boardId));
        assertThat(postResponse.getStatus(), is(post.getStatus()));
        assertThat(postResponse.getPoll(), is(nullValue()));
        assertThat(postResponse.getIsAuthorAdmin(), is(false));
        assertThat(postResponse.getReported(), is(false));
        assertThat(postResponse.getActiveStartDate(), notNullValue());

        itUtil.assertPostTitleAndContentForAdmin(post, postResponse);
    }

    private void assertPostWithDigitalDocumentResponse(Post post, PostResponse postResponse) {
        assertPostResponse(post, postResponse, stock.getCode(), board2.getId());

        UserDigitalDocumentResponse userDigitalDocument = postResponse.getDigitalDocument();
        assertThat(userDigitalDocument, is(notNullValue()));

        DigitalDocumentDownloadResponse digitalDocumentDownloadResponse = userDigitalDocument.getDigitalDocumentDownload();
        assertThat(digitalDocumentDownloadResponse, is(notNullValue()));
        assertThat(digitalDocumentDownloadResponse.getZipFileKey(), is(zipFileKey));
    }

    private void assertResponse(ag.act.model.GetBoardGroupPostResponse result, long totalElements) {
        final ag.act.model.Paging paging = result.getPaging();
        final List<ag.act.model.PostResponse> postResponses = result.getData();

        assertPaging(paging, totalElements);
        assertThat(postResponses.size(), is(SIZE));
        assertPostResponse(post2, postResponses.get(0), stock.getCode(), board1.getId());
        assertPostResponse(post1, postResponses.get(1), stock.getCode(), board1.getId());
    }

    private void assertPaging(Paging paging, long totalElements) {
        assertThat(paging.getPage(), is(pageNumber));
        assertThat(paging.getTotalElements(), is(totalElements));
        assertThat(paging.getTotalPages(), is((int) Math.ceil(totalElements / (SIZE * 1.0))));
        assertThat(paging.getSize(), is(SIZE));
        assertThat(paging.getSorts().size(), is(1));
        assertThat(paging.getSorts().get(0), is(CREATED_AT_DESC));
    }

    private Board createOrGetBoard(Stock stock, BoardGroup boardGroup, BoardCategory boardCategory) {
        try {
            return itUtil.createBoard(stock, boardGroup, boardCategory);
        } catch (Exception e) {
            return board1;
        }
    }

    private Post createPost(User user, Board board, LocalDateTime activeEndDate) {
        Post post = itUtil.createPost(board, user.getId());
        post.setActiveEndDate(activeEndDate);
        return postsByBoardGroupTestHolder.addOrSet(board.getGroup(), itUtil.updatePost(post));
    }

    private Post createPost(User user, String title, Board board) {
        Post post = itUtil.createPost(board, user.getId());
        post.setTitle(someString(10) + title + someString(10));
        return postsByBoardGroupTestHolder.addOrSet(board.getGroup(), itUtil.updatePost(post));
    }

    private Post createPost(User user, String title, Board board, LocalDateTime activeStartDate) {
        Post post = createPost(user, title, board);
        post.setActiveStartDate(activeStartDate);
        return postsByBoardGroupTestHolder.addOrSet(board.getGroup(), itUtil.updatePost(post));
    }

    private Post createPost(User user, String title, String content, Board board) {
        Post post = itUtil.createPost(board, user.getId(), title, false);
        post.setContent(someString(10) + content + someString(10));
        return postsByBoardGroupTestHolder.addOrSet(board.getGroup(), itUtil.updatePost(post));
    }

    private Post createPost(User user, String someString, Board board, Status status) {
        final Post post = createPost(user, someString, board);
        post.setStatus(status);
        return postsByBoardGroupTestHolder.addOrSet(board.getGroup(), itUtil.updatePost(post));
    }

    private Post createPostWithDigitalDocument(User user, String title, String zipFileKey, String zipFilePath, Board board, Stock stock) {
        Post post = itUtil.createPost(board, user.getId());
        post.setTitle(someString(10) + title + someString(10));

        User acceptUser = itUtil.createUser();

        DigitalDocument digitalDocument = itUtil.createDigitalDocument(post, stock, acceptUser, DigitalDocumentType.ETC_DOCUMENT);
        itUtil.createDigitalDocumentUser(digitalDocument, user, stock, someAlphanumericString(10), DigitalDocumentAnswerStatus.COMPLETE);
        DigitalDocumentDownload digitalDocumentDownload = itUtil.createDigitalDocumentDownload(digitalDocument.getId(), user.getId(), true);
        digitalDocumentDownload.setZipFilePath(zipFilePath);
        digitalDocumentDownload.setZipFileKey(zipFileKey);
        itUtil.updateDigitalDocumentDownload(digitalDocumentDownload);

        itUtil.createMyDataSummary(user, stock.getCode(), digitalDocument.getStockReferenceDate());

        return postsByBoardGroupTestHolder.addOrSet(board.getGroup(), itUtil.updatePost(post));
    }

    private Post setIsExclusiveToHoldersTrue(Post post) {
        post.setIsExclusiveToHolders(true);
        post = itUtil.updatePost(post);
        return post;
    }
}
