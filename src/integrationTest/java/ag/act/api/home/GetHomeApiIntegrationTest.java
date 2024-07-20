package ag.act.api.home;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.api.home.util.HomeApiIntegrationTestHelper;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.notification.Notification;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.virtualboard.VirtualBoardGroup;
import ag.act.model.Status;
import ag.act.util.DateTimeUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;

public class GetHomeApiIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/home";
    private HomeApiIntegrationTestHelper homeTestHelper;
    private String jwt;
    private User user;

    @BeforeEach
    void setUp() {
        itUtil.init();

        dbCleaner.clean();

        user = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());
        homeTestHelper = itUtil.getHomeTestHelper();
    }

    private ag.act.model.HomeResponse getResponse(MvcResult response)
        throws JsonProcessingException, UnsupportedEncodingException {

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            ag.act.model.HomeResponse.class
        );
    }

    private MvcResult callApi() throws Exception {
        return mockMvc
            .perform(
                get(TARGET_API)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(status().isOk())
            .andReturn();
    }

    @Nested
    class TestSolidarities {
        @Nested
        class WhenAllMySolidaritiesActive {
            @BeforeEach
            void setUp() {
                for (int i = 0; i < 21; i++) {
                    homeTestHelper.mockUserHoldingStock(user, i, Status.ACTIVE);
                }
            }

            @DisplayName("Should return 200 response for my home, with maximum 20 my solidarities " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                final ag.act.model.HomeResponse result = getResponse(callApi());

                assertThat(result.getMySolidarity().size(), is(20));
                for (int i = 0; i < 20; i++) {
                    assertThat(result.getMySolidarity().get(i).getDisplayOrder(), is(i));
                }
            }
        }

        @Nested
        class WhenInactiveSolidarityUpdatedToActive {
            @BeforeEach
            void setUp() {
                List<Status> statuses = new ArrayList<>();
                for (int i = 0; i < 20; i++) {
                    statuses.add(Status.INACTIVE_BY_ADMIN);
                }
                statuses.add(Status.ACTIVE);

                Collections.shuffle(statuses);

                for (Status status : statuses) {
                    homeTestHelper.mockUserHoldingStock(user, 100_000, status);
                }
            }

            @DisplayName("Should return 200 response for my home, with maximum 20 my solidarities " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                final ag.act.model.HomeResponse result = getResponse(callApi());

                assertThat(result.getMySolidarity().size(), is(20));
                assertThat(result.getMySolidarity().get(0).getStatus(), is(Status.ACTIVE));
                assertThat(result.getMySolidarity().get(0).getMinThresholdMemberCount(), is(50));
            }
        }

        @Nested
        class TestSolidarityStakeRank {

            @BeforeEach
            void setUp() {
                homeTestHelper.mockUserHoldingStockWithStockRanking(user, 1, Status.ACTIVE, 1);
                homeTestHelper.mockUserHoldingStock(user, 2, Status.ACTIVE);
            }

            @DisplayName("Should return 200 response for my home, with maximum 2 my solidarities " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                final ag.act.model.HomeResponse result = getResponse(callApi());

                assertThat(result.getMySolidarity().size(), is(2));

                assertThat(result.getMySolidarity().get(0).getDisplayOrder(), is(1));
                assertThat(result.getMySolidarity().get(0).getStakeRank(), is(String.valueOf(1)));
                assertThat(result.getMySolidarity().get(1).getDisplayOrder(), is(2));
                assertThat(result.getMySolidarity().get(1).getStakeRank(), is("-"));
            }
        }
    }

    @Nested
    class TestUnreadNotificationCount {
        private static final long READ_NOTIFICATIONS_COUNT = 3L;
        private static final long UNREAD_NOTIFICATIONS_COUNT = 4L;
        private static final long PAST_NOTIFICATIONS_COUNT = 5L;
        private static final long EXPIRED_NOTIFICATIONS_COUNT = 2L;
        private Post post;
        private User otherUser;
        private List<Notification> readNotifications;
        private List<Notification> unreadNotifications;
        private List<Notification> pastNotifications;

        @BeforeEach
        void setUp() {
            User writer = itUtil.createUser();
            Stock stock = itUtil.createStock();
            Board board = itUtil.createBoard(stock);
            post = itUtil.createPostWithLikeCount(board, writer.getId(), 0L);
            itUtil.createUserHoldingStock(stock.getCode(), user);

            createReadNotifications();
            createUnreadNotifications();
            createPastNotifications();
            createExpiredNotifications();

            createExcludedNotificationsForUnreadCount(someIntegerBetween(1, 10));

            otherUser = itUtil.createUser();
            stubOtherUsersNotificationUserView();
        }

        @DisplayName("Should return 200 response for my home, with 4 unread notifications " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final ag.act.model.HomeResponse result = getResponse(callApi());

            assertThat(result.getUnreadNotificationsCount(), is(UNREAD_NOTIFICATIONS_COUNT));
        }

        private void createReadNotifications() {
            readNotifications = createNotifications(READ_NOTIFICATIONS_COUNT);

            readNotifications.forEach((notification) ->
                itUtil.createNotificationUserView(user.getId(), notification.getId()));
        }

        private void createUnreadNotifications() {
            unreadNotifications = createNotifications(UNREAD_NOTIFICATIONS_COUNT);
        }

        private void createPastNotifications() {

            pastNotifications = createNotifications(PAST_NOTIFICATIONS_COUNT);

            pastNotifications.forEach((notification) -> {
                final LocalDateTime oneMonthBeforeLocalDateTime = DateTimeUtil.getPastMonthFromCurrentLocalDateTime(1).minusSeconds(1);

                notification.setCreatedAt(oneMonthBeforeLocalDateTime);
                itUtil.updateNotification(notification);
            });
        }

        private void createExpiredNotifications() {

            pastNotifications = createNotifications(EXPIRED_NOTIFICATIONS_COUNT);

            pastNotifications.forEach((notification) -> {
                notification.setActiveStartDate(LocalDateTime.now().minusDays(2));
                notification.setActiveEndDate(LocalDateTime.now().minusDays(1));

                itUtil.updateNotification(notification);
            });
        }

        private List<Notification> createNotifications(long size) {
            return LongStream.range(0L, size).mapToObj(i -> itUtil.createNotification(post.getId())).toList();
        }

        private void createExcludedNotificationsForUnreadCount(long size) {
            LongStream.range(0L, size)
                .mapToObj(i -> itUtil.createNotification(post.getId()))
                .peek(notification -> notification.setStatus(Status.DELETED))
                .forEach(itUtil::updateNotification);
        }

        private void stubOtherUsersNotificationUserView() {
            // Test to ensure that only notifications unread by the current user are counted.
            // This verifies that notifications read by others but unread by the current user are correctly included in the count.
            Stream.concat(
                readNotifications.stream(),
                Stream.concat(
                    unreadNotifications.stream(),
                    pastNotifications.stream()
                )
            ).forEach(notification ->
                itUtil.createNotificationUserView(otherUser.getId(), notification.getId()));
        }
    }

    @Nested
    class TestUnreadDigitalDelegationCount {

        @BeforeEach
        void setUp() {
            final User orderUser = itUtil.createUser();
            final Stock stock = itUtil.createStock();
            mockUserHoldingStockOnReferenceDate(orderUser, stock.getCode(), someRandomeLocalDate(someIntegerBetween(1, 15)));
            mockUserHoldingStockOnReferenceDate(orderUser, stock.getCode(), someRandomeLocalDate(someIntegerBetween(16, 30)));
        }

        private void mockUserHoldingStockOnReferenceDate(User user, String stockCode, LocalDate stockReferenceDate) {
            itUtil.createUserHoldingStockOnReferenceDate(
                stockCode,
                user.getId(),
                stockReferenceDate
            );
        }

        private DigitalDocument mockDigitalProxyDocument(
            LocalDateTime targetStartDate, LocalDateTime targetEndDate, LocalDate referenceDate
        ) {
            final Stock stock = itUtil.createStock(someStockCode());
            final Board board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.DIGITAL_DELEGATION);
            final User adminUser = itUtil.createAdminUser();
            final User acceptUser = itUtil.createAcceptorUser();
            final Post post = itUtil.createPost(board, adminUser.getId());

            DigitalDocument digitalDocument = itUtil.createDigitalDocument(
                post, stock, acceptUser, DigitalDocumentType.DIGITAL_PROXY, targetStartDate, targetEndDate, referenceDate
            );
            itUtil.createOrUpdateLatestPostTimestamp(stock, board.getGroup(), board.getCategory());

            return itUtil.updateDigitalDocument(digitalDocument);
        }

        private LocalDate someRandomeLocalDate(Integer daysToSubtract) {
            return LocalDate.now().minusDays(daysToSubtract);
        }

        @Nested
        class WhenUserHasUnreadDigitalDelegation {
            private Long postId;

            @BeforeEach
            void setUp() {
                final LocalDateTime now = LocalDateTime.now();
                final LocalDateTime targetStartDate = now.minusDays(5);
                final LocalDateTime targetEndDate = now.plusDays(5);
                final LocalDate referenceDate = now.toLocalDate();
                final DigitalDocument digitalDocument = mockDigitalProxyDocument(targetStartDate, targetEndDate, referenceDate);
                postId = digitalDocument.getPostId();
                mockUserHoldingStockOnReferenceDate(user, digitalDocument.getStockCode(), referenceDate);
            }

            @Nested
            class WhenExistPostUserView {

                @BeforeEach
                void setUp() {
                    itUtil.createPostUserView(user, itUtil.findPostNoneNull(postId));
                }

                @DisplayName("Should return 200 response for my home, with unread digitalDelegations " + TARGET_API)
                @Test
                void shouldReturnUnreadDigitalDelegationFalse() throws Exception {
                    final ag.act.model.HomeResponse result = getResponse(callApi());

                    assertThat(result.getUnreadPostStatus().getUnreadDigitalDelegation(), is(Boolean.FALSE));
                }
            }

            @Nested
            class WhenNotExistPostUserView {

                @DisplayName("Should return 200 response for my home, with unread digitalDelegations " + TARGET_API)
                @Test
                void shouldReturnSuccess() throws Exception {
                    final ag.act.model.HomeResponse result = getResponse(callApi());

                    assertThat(result.getUnreadPostStatus().getUnreadDigitalDelegation(), is(Boolean.TRUE));
                }
            }
        }

        @Nested
        class WhenUserDoesNotHaveUnreadDigitalDelegation {

            @DisplayName("Should return 200 response for my home, without unread digitalDelegations " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                final ag.act.model.HomeResponse result = getResponse(callApi());

                assertThat(result.getUnreadPostStatus().getUnreadDigitalDelegation(), is(Boolean.FALSE));
            }
        }
    }

    @Nested
    class TestBestPostPreviews {
        private Stock stock;
        private Board board;
        private Post post1;
        private Post post2;
        private Post post3;
        private Post notBestPost;
        private static final String ACT_BEST_URL = "/act-best";
        private static final int ACT_BEST_PREVIEW_SIZE = 2;

        @BeforeEach
        void setUp() {
            stock = itUtil.createStock();
            board = itUtil.createBoardForVirtualBoardGroup(VirtualBoardGroup.ACT_BEST, stock);

            post1 = itUtil.createPostWithLikeCount(board, user.getId(), 15L);
            post2 = itUtil.createPostWithLikeCount(board, user.getId(), 14L);
            post3 = itUtil.createPostWithLikeCount(board, user.getId(), 13L);
            post3 = setIsExclusiveToHoldersTrue(post3);
            notBestPost = itUtil.createPostWithLikeCount(board, user.getId(), 0L);
        }

        private Post setIsExclusiveToHoldersTrue(Post post) {
            post.setIsExclusiveToHolders(true);
            post = itUtil.updatePost(post);
            return post;
        }

        @DisplayName("Should return 200 response for my home, with best posts preview " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final ag.act.model.HomeResponse result = getResponse(callApi());

            String link = result.getActBestPosts().getLink();
            List<ag.act.model.PostResponse> postResponses = result.getActBestPosts().getPosts();

            assertThat(link, is(ACT_BEST_URL));
            assertThat(postResponses.size(), is(ACT_BEST_PREVIEW_SIZE));
            assertExclusiveToHoldersPostResponse(postResponses.get(0), post3);
            assertPostResponse(postResponses.get(1), post2);
            assertNotBestPostExcluded(postResponses);
        }

        private void assertPostResponse(ag.act.model.PostResponse actual, Post expected) {
            assertThat(actual.getId(), is(expected.getId()));
            assertThat(actual.getStock().getCode(), is(stock.getCode()));
            assertThat(actual.getBoardId(), is(board.getId()));
            assertThat(actual.getTitle(), is(expected.getTitle()));
            assertThat(actual.getIsExclusiveToHolders(), is(false));
            assertThat(actual.getStatus(), is(expected.getStatus()));
            assertThat(actual.getReported(), is(false));
            assertThat(actual.getDeleted(), is(false));
            assertThat(actual.getIsAuthorAdmin(), is(false));
        }

        private void assertExclusiveToHoldersPostResponse(ag.act.model.PostResponse actual, Post expected) {
            assertThat(actual.getId(), is(expected.getId()));
            assertThat(actual.getStock().getCode(), is(stock.getCode()));
            assertThat(actual.getBoardId(), is(board.getId()));
            assertThat(actual.getTitle(), is("주주에게만 공개된 제한된 글입니다."));
            assertThat(actual.getIsExclusiveToHolders(), is(true));
            assertThat(actual.getStatus(), is(expected.getStatus()));
            assertThat(actual.getReported(), is(false));
            assertThat(actual.getDeleted(), is(false));
            assertThat(actual.getIsAuthorAdmin(), is(false));
        }

        private void assertNotBestPostExcluded(List<ag.act.model.PostResponse> postResponses) {
            Set<Long> excludedPostIds = Set.of(
                post1.getId(),
                notBestPost.getId()
            );

            boolean noExcludedPosts = postResponses.stream()
                .noneMatch(postResponse -> excludedPostIds.contains(postResponse.getId()));

            assertThat(noExcludedPosts, is(true));
        }
    }
}
