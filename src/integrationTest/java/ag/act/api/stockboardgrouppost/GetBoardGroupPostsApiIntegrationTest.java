package ag.act.api.stockboardgrouppost;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.core.configuration.GlobalBoardManager;
import ag.act.entity.Board;
import ag.act.entity.Poll;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.ReportType;
import ag.act.enums.RoleType;
import ag.act.enums.virtualboard.VirtualBoardCategory;
import ag.act.enums.virtualboard.VirtualBoardGroup;
import ag.act.model.GetBoardGroupPostResponse;
import ag.act.model.Paging;
import ag.act.model.PollResponse;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static ag.act.TestUtil.assertTime;
import static ag.act.TestUtil.someBoardCategoryFromVirtualBoardCategory;
import static ag.act.TestUtil.someBoardCategoryFromVirtualBoardCategoryExcluding;
import static ag.act.TestUtil.someFilename;
import static ag.act.TestUtil.someVirtualBoardCategory;
import static ag.act.TestUtil.toMultiValueMap;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomThings.someThing;

class GetBoardGroupPostsApiIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts";

    @Autowired
    private GlobalBoardManager globalBoardManager;
    private String sort;
    private String jwt;
    private User user;
    private Post postWriteBlockedUser;
    private boolean isExclusiveToHolders;
    private boolean isExclusiveToPublic = false;
    private boolean isNotDeleted = false;

    @BeforeEach
    void setUp() {
        isExclusiveToHolders = false;
        sort = CREATED_AT_DESC;
        dbCleaner.clean();
    }

    @AfterEach
    void tearDown() {
        dbCleaner.clean();
    }

    @Nested
    class WhenBoardGroupNotVirtual {
        private Stock stock;
        private Board board;
        private Poll poll;
        private Post post2WithPoll;
        private Post post1;

        @BeforeEach
        void setUp() {
            itUtil.init();
            user = itUtil.createUser();
            jwt = itUtil.createJwt(user.getId());
            stock = itUtil.createStock();
            board = itUtil.createBoard(stock);
            itUtil.createSolidarity(stock.getCode());
            itUtil.createUserHoldingStock(stock.getCode(), user);

            post1 = itUtil.createPost(board, user.getId());
            post2WithPoll = itUtil.createPost(board, user.getId());
            post2WithPoll = itUtil.createPoll(post2WithPoll);
            itUtil.createPoll(post2WithPoll);
            itUtil.createPoll(post2WithPoll);
            poll = post2WithPoll.getFirstPoll();
        }

        @AfterEach
        void cleanUp() {
            itUtil.deletePost(post1);
            itUtil.deletePost(post2WithPoll);
        }

        @Nested
        class WhenNotIncludeDeletedPosts {

            private String thumbnailImageUrl;

            private void assertResponse(GetBoardGroupPostResponse result) {
                final Paging paging = result.getPaging();
                final List<PostResponse> postResponses = result.getData();

                assertPaging(paging, 2L);
                assertPosts(postResponses);
            }

            private void assertPosts(List<PostResponse> postResponses) {
                assertThat(postResponses.size(), is(2));

                final PostResponse postResponse1 = postResponses.get(0);

                assertThat(postResponse1.getId(), is(post2WithPoll.getId()));
                assertThat(postResponse1.getStock().getCode(), is(stock.getCode()));
                assertThat(postResponse1.getBoardId(), is(board.getId()));
                assertThat(postResponse1.getTitle(), is(post2WithPoll.getTitle()));
                assertThat(postResponse1.getStatus(), is(post2WithPoll.getStatus()));
                assertThat(postResponse1.getReported(), is(false));
                assertThat(postResponse1.getDeleted(), is(false));
                assertThat(postResponse1.getIsAuthorAdmin(), is(false));
                assertThat(postResponse1.getActiveStartDate(), notNullValue());
                assertUserProfileForList(postResponse1);

                final List<PollResponse> pollResponseList = postResponse1.getPolls();
                final PollResponse pollResponse1 = pollResponseList.get(0);
                assertThat(pollResponse1.getId(), is(poll.getId()));
                assertThat(pollResponse1.getTitle(), is(poll.getTitle()));
                assertThat(pollResponse1.getVoteType(), is(poll.getVoteType().name()));
                assertThat(pollResponse1.getSelectionOption(), is(poll.getSelectionOption().name()));
                assertTime(pollResponse1.getTargetStartDate(), poll.getTargetStartDate());
                assertTime(pollResponse1.getTargetEndDate(), poll.getTargetEndDate());

                final PostResponse postResponse2 = postResponses.get(1);
                assertThat(postResponse2.getId(), is(post1.getId()));
                assertThat(postResponse2.getStock().getCode(), is(stock.getCode()));
                assertThat(postResponse2.getBoardId(), is(board.getId()));
                assertThat(postResponse2.getTitle(), is(post1.getTitle()));
                assertThat(postResponse2.getStatus(), is(post1.getStatus()));
                assertThat(postResponse2.getPoll(), is(nullValue()));
                assertThat(postResponse2.getReported(), is(false));
                assertThat(postResponse2.getDeleted(), is(false));
                assertThat(postResponse2.getIsAuthorAdmin(), is(false));
                assertThat(postResponse2.getThumbnailImageUrl(), is(thumbnailImageUrl));
                assertUserProfileForList(postResponse2);
            }

            private void assertUserProfileForList(PostResponse postResponse1) {
                assertThat(postResponse1.getUserProfile(), notNullValue());
                assertThat(postResponse1.getUserProfile().getIsSolidarityLeader(), nullValue());
                assertThat(postResponse1.getUserProfile().getLeadingStocks(), nullValue());
            }

            @Nested
            class WhenGetBoardGroupPosts {

                @BeforeEach
                void setUp() {
                    thumbnailImageUrl = someFilename();
                    post1.setThumbnailImageUrl(thumbnailImageUrl);
                    itUtil.updatePost(post1);
                }

                @Nested
                class AndBoardCategoryIsNotProvided {

                    @Test
                    void shouldReturnSuccess() throws Exception {
                        final GetBoardGroupPostResponse result = callApiAndGetResponse(
                            stock.getCode(),
                            board.getGroup().name(),
                            ""
                        );

                        assertResponse(result);
                    }
                }

                @Nested
                class AndBoardCategoryIsAll {

                    @Test
                    void shouldReturnSuccess() throws Exception {
                        final GetBoardGroupPostResponse result = callApiAndGetResponse(
                            stock.getCode(),
                            board.getGroup().name(),
                            someThing("all", "ALL", "aLL", "All"),
                            ""
                        );

                        assertResponse(result);
                    }
                }
            }

            @Nested
            class WhenPostsHaveSomeReportedByMe {

                @BeforeEach
                void setUp() {
                    itUtil.createReport(post1.getId(), user, ReportType.POST);
                }

                @Test
                void shouldReturnPostWithSomeReportedByMe() throws Exception {
                    final GetBoardGroupPostResponse result = callApiAndGetResponse(
                        stock.getCode(),
                        board.getGroup().name(),
                        ""
                    );

                    final PostResponse postResponse2 = result.getData().get(1);
                    assertThat(postResponse2.getId(), is(post1.getId()));
                    assertThat(postResponse2.getStock().getCode(), is(stock.getCode()));
                    assertThat(postResponse2.getBoardId(), is(board.getId()));
                    assertThat(postResponse2.getTitle(), is("신고된 게시글입니다."));
                    assertThat(postResponse2.getStatus(), is(post1.getStatus()));
                    assertThat(postResponse2.getPoll(), is(nullValue()));
                    assertThat(postResponse2.getReported(), is(true));
                    assertThat(postResponse2.getDeleted(), is(false));
                    assertThat(postResponse2.getIsAuthorAdmin(), is(false));
                    assertThat(postResponse2.getUserProfile(), notNullValue());
                }
            }

            @Nested
            class WhenPostsIsNew {
                private Post postIsNew;

                @BeforeEach
                void setUp() {
                    final LocalDateTime now = LocalDateTime.now();
                    post1.setCreatedAt(now.minusHours(49));
                    post1 = itUtil.updatePost(post1);

                    post2WithPoll.setCreatedAt(now.minusHours(48));
                    post2WithPoll = itUtil.updatePost(post2WithPoll);

                    postIsNew = itUtil.createPost(board, user.getId());
                    postIsNew.setCreatedAt(now.minusHours(47));
                    postIsNew = itUtil.updatePost(postIsNew);
                }

                @Test
                void shouldReturn() throws Exception {
                    final GetBoardGroupPostResponse result = callApiAndGetResponse(
                        stock.getCode(),
                        board.getGroup().name(),
                        ""
                    );

                    final List<PostResponse> postResponseList = result.getData();
                    for (PostResponse postResponse : postResponseList) {
                        assertThat(postResponse.getIsNew(), is(Objects.equals(postResponse.getId(), postIsNew.getId())));
                    }
                }
            }

            @Nested
            class WhenExistBlockedUser {

                @BeforeEach
                void setUp() {
                    thumbnailImageUrl = someFilename();
                    post1.setThumbnailImageUrl(thumbnailImageUrl);
                    itUtil.updatePost(post1);
                    postWriteBlockedUser = createPostOfBlockedUser(board);
                }

                @Test
                void shouldReturnSuccess() throws Exception {
                    final GetBoardGroupPostResponse result = callApiAndGetResponse(
                        stock.getCode(),
                        board.getGroup().name(),
                        ""
                    );

                    assertResponse(result);
                    assertThat(
                        result.getData().stream().anyMatch(it -> Objects.equals(it.getId(), postWriteBlockedUser.getId())),
                        is(false)
                    );
                }
            }
        }

        @Nested
        class WhenSomeDeletedPostsExist {

            private Post deletedByUserPost;

            @BeforeEach
            void setUp() {
                deletedByUserPost = stubDeletedPost(Status.DELETED_BY_USER);
                stubDeletedPost(Status.DELETED_BY_ADMIN);
            }

            private Post stubDeletedPost(Status status) {
                Post deletedPost = itUtil.createPost(board, user.getId());
                deletedPost.setStatus(status);
                deletedPost.setDeletedAt(LocalDateTime.now());
                deletedPost.setTitle("DELETED_POST_FOR_TEST_" + status.name());

                return itUtil.updatePost(deletedPost);
            }

            @Test
            void shouldReturnPostsIncludingDeletedAsWell() throws Exception {
                final GetBoardGroupPostResponse result = callApiAndGetResponse(
                    stock.getCode(),
                    board.getGroup().name(),
                    ""
                );

                assertResponse(result);
            }

            private void assertResponse(GetBoardGroupPostResponse result) {
                final Paging paging = result.getPaging();
                final List<PostResponse> postResponses = result.getData();

                assertPaging(paging, 3L);
                assertPosts(postResponses);
            }

            private void assertPosts(List<PostResponse> postResponses) {
                assertThat(postResponses.size(), is(3));

                final PostResponse postResponse1 = postResponses.get(0);
                assertThat(postResponse1.getId(), is(deletedByUserPost.getId()));
                assertThat(postResponse1.getStock().getCode(), is(stock.getCode()));
                assertThat(postResponse1.getBoardId(), is(board.getId()));
                assertThat(postResponse1.getTitle(), is("삭제된 게시글입니다."));
                assertThat(postResponse1.getStatus(), is(deletedByUserPost.getStatus()));
                assertThat(postResponse1.getReported(), is(false));
                assertThat(postResponse1.getDeleted(), is(true));
                assertThat(postResponse1.getIsAuthorAdmin(), is(false));
                assertThat(postResponse1.getUserProfile(), nullValue());

                final PostResponse postResponse2 = postResponses.get(1);
                assertThat(postResponse2.getId(), is(post2WithPoll.getId()));
                assertThat(postResponse2.getStock().getCode(), is(stock.getCode()));
                assertThat(postResponse2.getBoardId(), is(board.getId()));
                assertThat(postResponse2.getTitle(), is(post2WithPoll.getTitle()));
                assertThat(postResponse2.getStatus(), is(post2WithPoll.getStatus()));
                assertThat(postResponse2.getReported(), is(false));
                assertThat(postResponse2.getDeleted(), is(false));
                assertThat(postResponse2.getIsAuthorAdmin(), is(false));
                assertThat(postResponse2.getUserProfile(), notNullValue());

                final List<PollResponse> pollResponseList = postResponse2.getPolls();
                final PollResponse pollResponse = pollResponseList.get(0);
                assertThat(pollResponse.getId(), is(poll.getId()));
                assertTime(pollResponse.getTargetStartDate(), poll.getTargetStartDate());
                assertTime(pollResponse.getTargetEndDate(), poll.getTargetEndDate());

                final PostResponse postResponse4 = postResponses.get(2);
                assertThat(postResponse4.getId(), is(post1.getId()));
                assertThat(postResponse4.getStock().getCode(), is(stock.getCode()));
                assertThat(postResponse4.getBoardId(), is(board.getId()));
                assertThat(postResponse4.getTitle(), is(post1.getTitle()));
                assertThat(postResponse4.getStatus(), is(post1.getStatus()));
                assertThat(postResponse4.getPoll(), is(nullValue()));
                assertThat(postResponse4.getReported(), is(false));
                assertThat(postResponse4.getDeleted(), is(false));
                assertThat(postResponse4.getIsAuthorAdmin(), is(false));
                assertThat(postResponse4.getUserProfile(), notNullValue());
            }
        }

        @Nested
        class WhenPostAuthorIsAdmin {
            @BeforeEach
            void setUp() {
                itUtil.createUserRole(user, someThing(RoleType.ADMIN, RoleType.SUPER_ADMIN));
            }

            @Test
            void shouldReturnPostWithIsAuthorAdminTrue() throws Exception {
                final GetBoardGroupPostResponse result = callApiAndGetResponse(
                    stock.getCode(),
                    board.getGroup().name(),
                    ""
                );

                final PostResponse postResponse2 = result.getData().get(1);
                assertThat(postResponse2.getId(), is(post1.getId()));
                assertThat(postResponse2.getStock().getCode(), is(stock.getCode()));
                assertThat(postResponse2.getBoardId(), is(board.getId()));
                assertThat(postResponse2.getTitle(), is(post1.getTitle()));
                assertThat(postResponse2.getStatus(), is(post1.getStatus()));
                assertThat(postResponse2.getPoll(), is(nullValue()));
                assertThat(postResponse2.getReported(), is(false));
                assertThat(postResponse2.getDeleted(), is(false));
                assertThat(postResponse2.getIsAuthorAdmin(), is(true));
                assertThat(postResponse2.getUserProfile(), notNullValue());
            }
        }
    }

    private Post createPostOfBlockedUser(Board board) {
        final User blockedUser = itUtil.createUser();
        itUtil.createBlockedUser(user.getId(), blockedUser.getId());
        return itUtil.createPost(board, blockedUser.getId());
    }

    @Nested
    class GetActBestPosts {
        private final String globalStockCode = globalBoardManager.getStockCode();
        private final VirtualBoardGroup virtualBoardGroup = VirtualBoardGroup.ACT_BEST;
        private Stock stock;
        private Board board;
        private Post post1;
        private Post post2;
        private Post post3;
        private Post notBestPost1;
        private Post notBestPost2;

        @BeforeEach
        void setUp() {
            itUtil.init();
            user = itUtil.createUser();
            jwt = itUtil.createJwt(user.getId());

            stock = itUtil.createStock();
            board = itUtil.createBoardForVirtualBoardGroup(VirtualBoardGroup.ACT_BEST, stock);

            post1 = itUtil.createPostWithLikeCount(board, user.getId(), 15L);
            post2 = itUtil.createPostWithLikeCount(board, user.getId(), 14L);
            post3 = itUtil.createPostWithLikeCount(board, user.getId(), 13L);
            notBestPost1 = itUtil.createPostWithLikeCount(board, user.getId(), 0L);
            notBestPost2 = itUtil.createPostWithLikeCount(board, user.getId(), 0L);
        }

        @AfterEach
        void cleanUp() {
            itUtil.deletePost(post1);
            itUtil.deletePost(post2);
            itUtil.deletePost(post3);
        }

        @Nested
        class WhenSortByCreatedAt {
            @Test
            void shouldReturnSuccess() throws Exception {
                final GetBoardGroupPostResponse result = callApiAndGetResponse(
                    globalStockCode,
                    virtualBoardGroup.name(),
                    sort
                );

                assertResponse(result);
            }

            private void assertResponse(GetBoardGroupPostResponse result) {
                final ag.act.model.Paging paging = result.getPaging();
                final List<ag.act.model.PostResponse> postResponses = result.getData();

                assertPaging(paging, 3L);
                assertPosts(postResponses);
            }

            private void assertPosts(List<ag.act.model.PostResponse> postResponses) {
                assertThat(postResponses.size(), is(3));

                assertPostResponse(postResponses.get(0), post3);
                assertPostResponse(postResponses.get(1), post2);
                assertPostResponse(postResponses.get(2), post1);

                Set<Long> excludedPostIds = Set.of(
                    notBestPost1.getId(),
                    notBestPost2.getId()
                );
                assertNotBestPostExcluded(postResponses, excludedPostIds);
            }
        }

        @Nested
        class WhenSortByLikeCount {

            @BeforeEach
            void setUp() {
                sort = "likeCount:DESC";
            }

            @Test
            void shouldReturnSuccess() throws Exception {
                final GetBoardGroupPostResponse result = callApiAndGetResponse(
                    globalStockCode,
                    virtualBoardGroup.name(),
                    sort
                );

                assertResponse(result);
            }

            private void assertResponse(GetBoardGroupPostResponse result) {
                final ag.act.model.Paging paging = result.getPaging();
                final List<ag.act.model.PostResponse> postResponses = result.getData();

                assertPaging(paging, 3L);
                assertPosts(postResponses);
            }

            private void assertPosts(List<ag.act.model.PostResponse> postResponses) {
                assertThat(postResponses.size(), is(3));

                assertPostResponse(postResponses.get(0), post1);
                assertPostResponse(postResponses.get(1), post2);
                assertPostResponse(postResponses.get(2), post3);

                Set<Long> excludedPostIds = Set.of(
                    notBestPost1.getId(),
                    notBestPost2.getId()
                );
                assertNotBestPostExcluded(postResponses, excludedPostIds);
            }
        }

        @Nested
        @DisplayName("액트 베스트 중에서")
        class WhenActBest {

            @BeforeEach
            void setUp() {
                isExclusiveToPublic = true;
                isNotDeleted = true;
            }

            @Nested
            class GetPublicOnly {

                private Post exclusiveToHoldersPost;

                @BeforeEach
                void setUp() {
                    exclusiveToHoldersPost = itUtil.createPost(board, user.getId());
                    exclusiveToHoldersPost.setIsExclusiveToHolders(Boolean.TRUE);
                    itUtil.updatePost(exclusiveToHoldersPost);
                }

                @Test
                @DisplayName("전체공개 게시글만 조회할 수 있다.")
                void getPublicPosts() throws Exception {
                    sort = "likeCount:DESC";
                    GetBoardGroupPostResponse response = callApiAndGetResponse(stock.getCode(), virtualBoardGroup.name(), sort);

                    assertPaging(response.getPaging(), 3L);
                    List<PostResponse> postResponses = response.getData();
                    assertPostResponse(postResponses.get(0), post1);
                    assertPostResponse(postResponses.get(1), post2);
                    assertPostResponse(postResponses.get(2), post3);

                    assertNotBestPostExcluded(postResponses, Set.of(exclusiveToHoldersPost.getId()));
                }

            }

            @Nested
            class NotGetDeletedPost {

                private Post deletedPost;

                @BeforeEach
                void setUp() {
                    deletedPost = itUtil.createPost(board, user.getId());
                    deletedPost.setStatus(Status.DELETED_BY_USER);
                    deletedPost.setDeletedAt(LocalDateTime.now());
                    itUtil.updatePost(deletedPost);
                }

                @Test
                @DisplayName("삭제된 게시글은 조회하지 않을 수 있다.")
                void notGetDeletePost() throws Exception {
                    sort = "likeCount:DESC";
                    GetBoardGroupPostResponse response = callApiAndGetResponse(stock.getCode(), virtualBoardGroup.name(), sort);

                    assertPaging(response.getPaging(), 3L);
                    List<PostResponse> postResponses = response.getData();
                    assertPostResponse(postResponses.get(0), post1);
                    assertPostResponse(postResponses.get(1), post2);
                    assertPostResponse(postResponses.get(2), post3);

                    assertNotBestPostExcluded(postResponses, Set.of(deletedPost.getId()));
                }

            }
        }

        @Nested
        @DisplayName("액트 베스트 중에서 주주에게만 공개한 게시글을")
        class GetOnlyExclusiveToHoldersPosts {

            private static final VirtualBoardCategory virtualBoardCategory = VirtualBoardCategory.ACT_BEST_STOCK;

            private Post notHoldingStockPost;
            private Post postByBlockedUser;

            @BeforeEach
            void setUp() {
                stock = itUtil.createStock();
                board = itUtil.createBoard(stock, BoardGroup.DEBATE, BoardCategory.DEBATE);

                itUtil.createUserHoldingStock(stock.getCode(), user);
                post1 = changeBoard(post1);
                post2 = changeBoard(post2);
                post3 = changeBoard(post3);

                post1.setIsExclusiveToHolders(true);
                post1 = itUtil.updatePost(post1);
                isExclusiveToHolders = true;

                // prepare user not holding stock, board and post
                final Stock notHoldingStock = itUtil.createStock();
                final Board notHoldingStockBoard = itUtil.createBoard(notHoldingStock, BoardGroup.DEBATE, BoardCategory.DEBATE);

                final User anotherUser = itUtil.createUser();
                notHoldingStockPost = itUtil.createPostWithLikeCount(notHoldingStockBoard, anotherUser.getId(), 15L);
                notHoldingStockPost.setIsExclusiveToHolders(true);
                notHoldingStockPost = itUtil.updatePost(notHoldingStockPost);

                // prepare blocked user and post
                final User blockedUser = itUtil.createUser();
                itUtil.createBlockedUser(user.getId(), blockedUser.getId());
                postByBlockedUser = itUtil.createPostWithLikeCount(board, blockedUser.getId(), 15L);
                postByBlockedUser.setIsExclusiveToHolders(true);
                postByBlockedUser = itUtil.updatePost(postByBlockedUser);
            }

            private Post changeBoard(Post post) {
                post.setBoardId(board.getId());
                post.setBoard(board);
                return itUtil.updatePost(post);
            }

            @Test
            @DisplayName("조회할 수 있다.")
            void shouldReturnSuccess() throws Exception {
                final GetBoardGroupPostResponse result = callApiAndGetResponse(
                    globalStockCode,
                    virtualBoardGroup.name(),
                    virtualBoardCategory.name(),
                    ""
                );

                assertResponse(result);
            }

            private void assertResponse(GetBoardGroupPostResponse result) {
                final ag.act.model.Paging paging = result.getPaging();
                final List<ag.act.model.PostResponse> postResponses = result.getData();

                assertPaging(paging, 1L);
                assertPosts(postResponses);
            }

            private void assertPosts(List<ag.act.model.PostResponse> postResponses) {
                assertThat(postResponses.size(), is(1));

                assertPostResponse(postResponses.get(0), post1);
                assertThat(postResponses.get(0).getIsExclusiveToHolders(), is(true));

                Set<Long> excludedPostIds = Set.of(
                    post2.getId(),
                    post3.getId(),
                    notHoldingStockPost.getId(),
                    postByBlockedUser.getId()
                );
                assertNotBestPostExcluded(postResponses, excludedPostIds);
            }
        }

        @Nested
        class WhenCategoryNameExist {

            private VirtualBoardCategory virtualBoardCategory;

            @BeforeEach
            void setUp() {
                virtualBoardCategory = someVirtualBoardCategory(virtualBoardGroup);
                BoardCategory category = someBoardCategoryFromVirtualBoardCategory(virtualBoardCategory);
                board.setCategory(category);
                board.setGroup(category.getBoardGroup());
                board = itUtil.updateBoard(board);

                BoardCategory anotherCategory = someBoardCategoryFromVirtualBoardCategoryExcluding(virtualBoardCategory);
                Board anotherBoard = itUtil.createBoard(stock, anotherCategory.getBoardGroup(), anotherCategory);
                notBestPost1 = itUtil.createPost(anotherBoard, user.getId());
                notBestPost2 = itUtil.createPost(anotherBoard, user.getId());
            }

            @Test
            void shouldReturnSuccess() throws Exception {
                final GetBoardGroupPostResponse result = callApiAndGetResponse(
                    globalStockCode,
                    virtualBoardGroup.name(),
                    virtualBoardCategory.name(),
                    sort
                );

                assertResponse(result);
            }

            private void assertResponse(GetBoardGroupPostResponse result) {
                final ag.act.model.Paging paging = result.getPaging();
                final List<ag.act.model.PostResponse> postResponses = result.getData();

                assertPaging(paging, 3L);
                assertPosts(postResponses);
            }

            private void assertPosts(List<ag.act.model.PostResponse> postResponses) {
                assertThat(postResponses.size(), is(3));

                assertPostResponse(postResponses.get(0), post3);
                assertPostResponse(postResponses.get(1), post2);
                assertPostResponse(postResponses.get(2), post1);

                Set<Long> excludedPostIds = Set.of(
                    notBestPost1.getId(),
                    notBestPost2.getId()
                );
                assertNotBestPostExcluded(postResponses, excludedPostIds);
            }
        }

        @Nested
        class WhenExistBlockedUser {

            @BeforeEach
            void setUp() {
                postWriteBlockedUser = createPostOfBlockedUser(board);
            }

            @Test
            void shouldReturnSuccess() throws Exception {
                final GetBoardGroupPostResponse result = callApiAndGetResponse(
                    globalStockCode,
                    virtualBoardGroup.name(),
                    sort
                );

                assertResponse(result);
                assertThat(
                    result.getData().stream().anyMatch(it -> Objects.equals(it.getId(), postWriteBlockedUser.getId())),
                    is(false)
                );
            }

            private void assertResponse(GetBoardGroupPostResponse result) {
                final ag.act.model.Paging paging = result.getPaging();
                final List<ag.act.model.PostResponse> postResponses = result.getData();

                assertPaging(paging, 3L);
                assertPosts(postResponses);
            }

            private void assertPosts(List<ag.act.model.PostResponse> postResponses) {
                assertThat(postResponses.size(), is(3));

                assertPostResponse(postResponses.get(0), post3);
                assertPostResponse(postResponses.get(1), post2);
                assertPostResponse(postResponses.get(2), post1);

                Set<Long> excludedPostIds = Set.of(
                    notBestPost1.getId(),
                    notBestPost2.getId()
                );
                assertNotBestPostExcluded(postResponses, excludedPostIds);
            }
        }

        private void assertPostResponse(ag.act.model.PostResponse actual, Post expected) {
            assertThat(actual.getId(), is(expected.getId()));
            assertThat(actual.getStock().getCode(), is(stock.getCode()));
            assertThat(actual.getBoardId(), is(board.getId()));
            assertThat(actual.getTitle(), is(expected.getTitle()));
            assertThat(actual.getStatus(), is(expected.getStatus()));
            assertThat(actual.getReported(), is(false));
            assertThat(actual.getDeleted(), is(false));
            assertThat(actual.getIsAuthorAdmin(), is(false));
            assertThat(actual.getIsExclusiveToHolders(), is(expected.getIsExclusiveToHolders()));
        }
    }

    private GetBoardGroupPostResponse callApiAndGetResponse(
        String stockCode,
        String boardGroupName,
        String sorts
    ) throws Exception {
        return callApiAndGetResponse(stockCode, boardGroupName, "", sorts);
    }

    private GetBoardGroupPostResponse callApiAndGetResponse(
        String stockCode,
        String boardGroupName,
        String boardCategoryName,
        String sorts
    ) throws Exception {

        Map<String, Object> params = Map.of(
            "sorts", Optional.ofNullable(sorts).orElse(""),
            "boardCategory", Optional.ofNullable(boardCategoryName).orElse(""),
            "isExclusiveToHolders", isExclusiveToHolders,
            "isExclusiveToPublic", isExclusiveToPublic,
            "isNotDeleted", isNotDeleted
        );

        MvcResult response = mockMvc
            .perform(
                get(TARGET_API, stockCode, boardGroupName)
                    .params(toMultiValueMap(params))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(status().isOk())
            .andReturn();

        return itUtil.getResult(response, GetBoardGroupPostResponse.class);
    }

    private void assertNotBestPostExcluded(List<ag.act.model.PostResponse> postResponses, Set<Long> excludedPostIds) {
        boolean noExcludedPosts = postResponses.stream()
            .noneMatch(postResponse -> excludedPostIds.contains(postResponse.getId()));

        assertThat(noExcludedPosts, is(true));
    }

    private void assertPaging(ag.act.model.Paging paging, Long totalElements) {
        assertThat(paging.getPage(), is(1));
        assertThat(paging.getTotalPages(), is(1));
        assertThat(paging.getTotalElements(), is(totalElements));
        assertThat(paging.getSorts().size(), is(1));
        itUtil.assertSort(paging.getSorts().get(0), sort);
    }
}
