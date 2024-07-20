package ag.act.api.admin.user;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.notification.Notification;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.RoleType;
import ag.act.enums.notification.NotificationCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Map;

import static ag.act.TestUtil.someStockCode;
import static ag.act.TestUtil.toMultiValueMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminNotificationListApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/notifications";
    private Integer pageNumber;
    private String jwt;
    private Board board;
    private Board board2;
    private Post post1;
    private Post post2;
    private Post post3;
    private Post post4;
    private Post post5;
    private User user;
    private Map<String, Object> params;

    private Notification notification1;
    private Notification notification2;
    private Notification notification3;
    private Notification notification4;
    private Notification notification5;

    @BeforeEach
    void setUp() throws Exception {
        itUtil.init();
        user = itUtil.createUser();
        user = itUtil.createUserRole(user, RoleType.ADMIN);
        jwt = itUtil.createJwt(user.getId());
        Stock stock = itUtil.createStock(someStockCode());
        Stock stock2 = itUtil.createStock(someStockCode());
        itUtil.createSolidarity(stock.getCode());
        itUtil.createUserHoldingStock(stock.getCode(), user);
        board = itUtil.createBoard(stock, BoardGroup.GLOBALBOARD, BoardCategory.STOCKHOLDER_ACTION);
        board2 = itUtil.createBoard(stock2, BoardGroup.GLOBALBOARD, BoardCategory.STOCKHOLDER_ACTION);
        post1 = itUtil.createPost(board, user.getId(), "Promise", false);
        post2 = itUtil.createPost(board, user.getId(), "Devotion", false);
        post3 = itUtil.createPost(board2, user.getId(), "Eternity", false);
        post4 = itUtil.createPost(board2, user.getId(), "Promise", false);
        post5 = itUtil.createPost(board2, user.getId(), "Trust", false);

        notification1 = itUtil.createNotification(post1.getId());
        notification2 = itUtil.createNotification(post2.getId());
        notification3 = itUtil.createNotification(post3.getId());
        notification4 = itUtil.createNotification(post4.getId());
        notification5 = itUtil.createNotification(post5.getId());
    }

    @Nested
    class WhenSearchAllNotifications {

        @Nested
        class AndFirstPage {

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_1;
                params = Map.of(
                    "category", NotificationCategory.STOCKHOLDER_ACTION.name(),
                    "postTitle", "",
                    "page", pageNumber.toString(),
                    "size", SIZE.toString()
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnUsers() throws Exception {
                assertResponse(callApiAndGetResult());
            }

            private void assertResponse(ag.act.model.GetNotificationDataResponse result) {
                final List<ag.act.model.NotificationResponse> NotificationResponses = result.getData();

                assertThat(NotificationResponses.size(), is(SIZE));
                assertPostResponse(notification5, NotificationResponses.get(0));
                assertPostResponse(notification4, NotificationResponses.get(1));
            }
        }

        @Nested
        class AndSecondPage {

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_2;
                params = Map.of(
                    "category", NotificationCategory.STOCKHOLDER_ACTION.name(),
                    "postTitle", "",
                    "page", pageNumber.toString(),
                    "size", SIZE.toString()
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnUsers() throws Exception {
                assertResponse(callApiAndGetResult());
            }

            private void assertResponse(ag.act.model.GetNotificationDataResponse result) {
                final List<ag.act.model.NotificationResponse> NotificationResponses = result.getData();

                assertThat(NotificationResponses.size(), is(SIZE));
                assertPostResponse(notification3, NotificationResponses.get(0));
                assertPostResponse(notification2, NotificationResponses.get(1));
            }
        }
    }

    @Nested
    class WhenSearchByPostTitle {

        @Nested
        class AndFoundNotifications {

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_1;
                params = Map.of(
                    "category", NotificationCategory.STOCKHOLDER_ACTION.name(),
                    "postTitle", "Promise",
                    "page", pageNumber.toString(),
                    "size", SIZE.toString()
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnNotifications() throws Exception {
                assertResponse(callApiAndGetResult());
            }

            private void assertResponse(ag.act.model.GetNotificationDataResponse result) {
                final ag.act.model.Paging paging = result.getPaging();
                final List<ag.act.model.NotificationResponse> NotificationResponses = result.getData();

                assertPaging(paging, 2L);
                assertThat(NotificationResponses.size(), is(2));
                assertPostResponse(notification4, NotificationResponses.get(0));
                assertPostResponse(notification1, NotificationResponses.get(1));
            }
        }

        @Nested
        class AndNotFound {

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_1;
                params = Map.of(
                    "category", NotificationCategory.STOCKHOLDER_ACTION.name(),
                    "postTitle", "Love",
                    "page", pageNumber.toString(),
                    "size", SIZE.toString()
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnUsers() throws Exception {
                assertResponse(callApiAndGetResult());
            }

            private void assertResponse(ag.act.model.GetNotificationDataResponse result) {
                final ag.act.model.Paging paging = result.getPaging();
                final List<ag.act.model.NotificationResponse> NotificationResponses = result.getData();

                assertPaging(paging, 0L);
                assertThat(NotificationResponses.size(), is(0));
            }
        }
    }


    private ag.act.model.GetNotificationDataResponse callApiAndGetResult() throws Exception {
        MvcResult response = mockMvc
            .perform(
                get(TARGET_API)
                    .params(toMultiValueMap(params))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(status().isOk())
            .andReturn();

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            ag.act.model.GetNotificationDataResponse.class
        );
    }

    private void assertPostResponse(Notification notification, ag.act.model.NotificationResponse notificationResponse) {
        assertThat(notificationResponse.getId(), is(notification.getId()));
        assertThat(notificationResponse.getPostId(), is(notification.getPostId()));
        assertThat(notificationResponse.getCategory(), is(notification.getCategory().name()));
        assertThat(notificationResponse.getType(), is(notification.getType().name()));
    }

    private void assertPaging(ag.act.model.Paging paging, long totalElements) {
        assertThat(paging.getPage(), is(pageNumber));
        assertThat(paging.getTotalElements(), is(totalElements));
        assertThat(paging.getTotalPages(), is((int) Math.ceil(totalElements / (SIZE * 1.0))));
        assertThat(paging.getSize(), is(SIZE));
        assertThat(paging.getSorts().size(), is(1));
        assertThat(paging.getSorts().get(0), is(CREATED_AT_DESC));
    }
}