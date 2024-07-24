package ag.act.api.notification;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.core.configuration.GlobalBoardManager;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.notification.Notification;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.itutil.holder.ActEntityTestHolder;
import ag.act.model.Paging;
import ag.act.model.Status;
import ag.act.model.UserNotificationResponse;
import ag.act.util.AppLinkUrlGenerator;
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

import static ag.act.TestUtil.assertTime;
import static ag.act.TestUtil.someBoardCategory;
import static ag.act.TestUtil.toMultiValueMap;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;

class GetUserNotificationsApiIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/notifications";
    private static final String SIZE_OF_ALL_NOTIFICATIONS = "1000000";

    private ActEntityTestHolder<Notification, Long> notificationsTestHolder;
    @Autowired
    private AppLinkUrlGenerator appLinkUrlGenerator;
    @Autowired
    private GlobalBoardManager globalBoardManager;
    private String jwt;
    private Map<String, Object> params;
    private Stock stock;
    private Stock globalStock;
    private Integer pageNumber;
    private User loginUser;
    private Post post2;
    private Post post3;
    private Post post4;
    private Post post5;
    private Notification notification1;
    private Notification notification2;
    private Notification notification3;
    private Notification notification4;
    private Notification notification5;
    private String linkUrl2;
    private String linkUrl3;
    private String linkUrl4;
    private String linkUrl5;
    private String globalStockCode;
    private User author;
    private Board board;

    @BeforeEach
    void setUp() {
        itUtil.init();
        updateNotificationsToDeleted();
        notificationsTestHolder = new ActEntityTestHolder<>();

        loginUser = itUtil.createUser();
        author = itUtil.createUser();
        jwt = itUtil.createJwt(loginUser.getId());

        stock = itUtil.createStock();
        final BoardGroup boardGroup = someEnum(BoardGroup.class);
        board = itUtil.createBoard(stock, boardGroup, someBoardCategory(boardGroup));

        itUtil.createUserHoldingStock(stock.getCode(), loginUser);

        globalStockCode = globalBoardManager.getStockCode();
        globalStock = itUtil.findStock(globalStockCode);
        final Board globalBoard = itUtil.findBoard(globalStockCode, BoardCategory.STOCK_ANALYSIS_DATA)
            .orElseThrow();

        Post post1 = createPost(author, board);
        post2 = createPost(author, board);
        post3 = createPost(author, board);
        post4 = createPost(author, board);
        post5 = createPost(author, globalBoard);

        notification1 = createNotification(post1);
        notification2 = createNotification(post2);
        notification3 = createNotification(post3);
        notification4 = createNotification(post4);
        notification5 = createNotification(post5);

        createDeletedNotification(createPost(author, board));
        createDeletedNotification(createPost(author, board));
        createDeletedNotification(createPost(author, board));
        createDeletedNotification(createPost(author, board));

        appLinkUrlGenerator.generateBoardGroupPostLinkUrl(post1);
        linkUrl2 = appLinkUrlGenerator.generateBoardGroupPostLinkUrl(post2);
        linkUrl3 = appLinkUrlGenerator.generateBoardGroupPostLinkUrl(post3);
        linkUrl4 = appLinkUrlGenerator.generateBoardGroupPostLinkUrl(post4);
        linkUrl5 = appLinkUrlGenerator.generateBoardGroupPostLinkUrl(post5);

        createAnotherUserAndHoldingStock();
        createAnotherUserAndHoldingStock();
        createAnotherUserAndHoldingStock();
    }

    private void updateNotificationsToDeleted() {
        itUtil.findAllRecentNotifications()
            .forEach(it -> {
                it.setStatus(Status.DELETED);
                itUtil.updateNotification(it);
            });
    }

    private void createAnotherUserAndHoldingStock() {
        itUtil.createUserHoldingStock(stock.getCode(), itUtil.createUser());
    }

    private ag.act.model.GetUserNotificationDataResponse callApiAndGetResult() throws Exception {
        MvcResult response = mockMvc
            .perform(
                get(TARGET_API)
                    .params(toMultiValueMap(params))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(status().isOk())
            .andReturn();

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            ag.act.model.GetUserNotificationDataResponse.class
        );
    }

    private void assertNotificationDetailsResponse(
        Notification notification,
        Post post,
        String linkUrl,
        UserNotificationResponse userNotificationResponse
    ) {
        assertNotificationDetailsResponse(notification, post, linkUrl, userNotificationResponse, false);
    }

    private void assertNotificationDetailsResponse(
        Notification notification,
        Post post,
        String linkUrl,
        UserNotificationResponse userNotificationResponse,
        boolean isRead
    ) {
        assertThat(userNotificationResponse.getId(), is(notification.getId()));
        assertThat(userNotificationResponse.getPostId(), is(post.getId()));
        assertThat(userNotificationResponse.getCategory(), is(notification.getCategory().name()));
        assertThat(userNotificationResponse.getType(), is(notification.getType().name()));
        assertTime(userNotificationResponse.getCreatedAt(), notification.getCreatedAt());
        assertThat(userNotificationResponse.getIsRead(), is(isRead));
        assertThat(userNotificationResponse.getLinkUrl(), is(linkUrl));

        assertThat(userNotificationResponse.getPost().getId(), is(post.getId()));
        assertThat(userNotificationResponse.getPost().getTitle(), is(post.getTitle()));
        assertThat(userNotificationResponse.getPost().getBoardCategory(), is(post.getBoard().getCategory().name()));
        if (Objects.equals(globalStockCode, userNotificationResponse.getPost().getStockCode())) {
            assertThat(userNotificationResponse.getPost().getStockCode(), is(globalStock.getCode()));
            assertThat(userNotificationResponse.getPost().getStockName(), is(globalStock.getName()));
        } else {
            assertThat(userNotificationResponse.getPost().getStockCode(), is(stock.getCode()));
            assertThat(userNotificationResponse.getPost().getStockName(), is(stock.getName()));
        }
    }

    private void assertPaging(Paging paging, long totalElements) {
        assertThat(paging.getPage(), is(pageNumber));
        assertThat(paging.getTotalElements(), is(totalElements));
        assertThat(paging.getTotalPages(), is((int) Math.ceil(totalElements / (SIZE * 1.0))));
        assertThat(paging.getSize(), is(SIZE));
        assertThat(paging.getSorts().size(), is(1));
        itUtil.assertSort(paging.getSorts().get(0), CREATED_AT_DESC);
    }

    private Post createPost(User user, Board board) {
        return itUtil.createPost(board, user.getId());
    }

    private Notification createDeletedNotification(Post post) {
        final Notification notification = itUtil.createNotification(post.getId());
        notification.setStatus(Status.DELETED);
        final Notification updatedNotification = itUtil.updateNotification(notification);

        return notificationsTestHolder.addOrSet(updatedNotification);
    }

    private Notification createAlreadyInactiveNotification(Post post) {
        final Notification notification = itUtil.createNotification(post.getId());
        notification.setActiveEndDate(LocalDateTime.now().minusSeconds(someIntegerBetween(10, 1000)));
        final Notification updatedNotification = itUtil.updateNotification(notification);

        return notificationsTestHolder.addOrSet(updatedNotification);
    }

    private Notification createScheduledNotification(Post post) {
        final Notification notification = itUtil.createNotification(post.getId());
        notification.setActiveStartDate(LocalDateTime.now().plusSeconds(someIntegerBetween(10, 1000)));
        final Notification updatedNotification = itUtil.updateNotification(notification);

        return notificationsTestHolder.addOrSet(updatedNotification);
    }

    private Notification createNotification(Post post) {
        return notificationsTestHolder.addOrSet(itUtil.createNotification(post.getId()));
    }

    private long getTotalElementsOfNotifications() {
        return notificationsTestHolder.getItems().stream()
            .filter(it -> it.getStatus() == Status.ACTIVE)
            .count();
    }

    @Nested
    class WhenSearchAllUserNotifications {

        @Nested
        class AndFirstPage {

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
                final long totalElements = getTotalElementsOfNotifications();
                assertResponse(callApiAndGetResult(), totalElements);
            }

            private void assertResponse(ag.act.model.GetUserNotificationDataResponse result, long totalElements) {
                final Paging paging = result.getPaging();
                final List<UserNotificationResponse> userNotificationResponses = result.getData();

                assertPaging(paging, totalElements);
                assertThat(userNotificationResponses.size(), is(SIZE));
                assertNotificationDetailsResponse(notification5, post5, linkUrl5, userNotificationResponses.get(0));
                assertNotificationDetailsResponse(notification4, post4, linkUrl4, userNotificationResponses.get(1));
            }
        }

        @Nested
        class AndSecondPage {

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_2;
                params = Map.of(
                    "page", pageNumber.toString(),
                    "size", SIZE.toString()
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnPosts() throws Exception {
                final long totalElements = getTotalElementsOfNotifications();
                assertResponse(callApiAndGetResult(), totalElements);
            }

            private void assertResponse(ag.act.model.GetUserNotificationDataResponse result, long totalElements) {
                final Paging paging = result.getPaging();
                final List<UserNotificationResponse> userNotificationResponses = result.getData();

                assertPaging(paging, totalElements);
                assertThat(userNotificationResponses.size(), is(SIZE));
                assertNotificationDetailsResponse(notification3, post3, linkUrl3, userNotificationResponses.get(0));
                assertNotificationDetailsResponse(notification2, post2, linkUrl2, userNotificationResponses.get(1));
            }
        }

        @Nested
        class AndUserAlreadyReadNotifications {

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_1;
                params = Map.of(
                    "page", pageNumber.toString(),
                    "size", SIZE.toString()
                );

                itUtil.createNotificationUserView(loginUser.getId(), notification1.getId());
                itUtil.createNotificationUserView(loginUser.getId(), notification2.getId());
                itUtil.createNotificationUserView(loginUser.getId(), notification3.getId());
                itUtil.createNotificationUserView(loginUser.getId(), notification4.getId());
                itUtil.createNotificationUserView(loginUser.getId(), notification5.getId());
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnPosts() throws Exception {
                final long totalElements = getTotalElementsOfNotifications();
                assertResponse(callApiAndGetResult(), totalElements);
            }

            private void assertResponse(ag.act.model.GetUserNotificationDataResponse result, long totalElements) {
                final Paging paging = result.getPaging();
                final List<UserNotificationResponse> userNotificationResponses = result.getData();

                assertPaging(paging, totalElements);
                assertThat(userNotificationResponses.size(), is(SIZE));
                assertNotificationDetailsResponse(notification5, post5, linkUrl5, userNotificationResponses.get(0), true);
                assertNotificationDetailsResponse(notification4, post4, linkUrl4, userNotificationResponses.get(1), true);
            }
        }

        @DisplayName("이미 노출기간이 종료된 알림이 있을때")
        @Nested
        class AndHaveSomeInactiveNotifications {

            private Notification alreadyInactiveNotification;

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_1;
                params = Map.of(
                    "page", pageNumber.toString(),
                    "size", SIZE_OF_ALL_NOTIFICATIONS
                );
                alreadyInactiveNotification = createAlreadyInactiveNotification(createPost(author, board));
            }

            @DisplayName("알림리스트는 노출기간이 종료된 알림을 제외하고 반환한다")
            @Test
            void shouldReturnPostsWithoutInactiveNotifications() throws Exception {
                final List<UserNotificationResponse> userNotificationResponses = callApiAndGetResult().getData();
                userNotificationResponses
                    .forEach(it -> assertThat(it.getId(), not(alreadyInactiveNotification.getId())));
            }
        }

        @DisplayName("미래에 노출된 스케쥴된 알림이 있을때")
        @Nested
        class AndHaveSomeScheduledNotifications {

            private Notification scheduledNotification;

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_1;
                params = Map.of(
                    "page", pageNumber.toString(),
                    "size", SIZE_OF_ALL_NOTIFICATIONS
                );
                scheduledNotification = createScheduledNotification(createPost(author, board));
            }

            @DisplayName("알림리스트는 스케쥴된 알림을 제외하고 반환한다")
            @Test
            void shouldReturnPostsWithoutScheduledNotifications() throws Exception {
                final List<UserNotificationResponse> userNotificationResponses = callApiAndGetResult().getData();
                userNotificationResponses
                    .forEach(it -> assertThat(it.getId(), not(scheduledNotification.getId())));
            }
        }
    }
}
