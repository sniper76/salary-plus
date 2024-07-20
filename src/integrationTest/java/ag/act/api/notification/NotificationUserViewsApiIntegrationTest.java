package ag.act.api.notification;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.notification.Notification;
import ag.act.entity.notification.NotificationUserView;
import ag.act.enums.BoardGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static ag.act.TestUtil.someBoardCategory;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;

public class NotificationUserViewsApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/notifications/{notificationId}";

    private String jwt;
    private User user;
    private Long notificationId;

    @BeforeEach
    void setUp() {
        itUtil.init();
        user = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());

        final Stock stock = itUtil.createStock();
        final BoardGroup boardGroup = someEnum(BoardGroup.class);
        final Board board = itUtil.createBoard(stock, boardGroup, someBoardCategory(boardGroup));
        final Post post = createPost(user, board);

        Notification notification = createNotification(post);
        notificationId = notification.getId();
    }

    private void assertResponse(ag.act.model.SimpleStringResponse result) {
        assertThat(result.getStatus(), is("ok"));

        NotificationUserView databaseNnotificationUserView = itUtil.findByNotificationIdAndUserId(
            notificationId, user.getId()
        ).orElseThrow();

        assertThat(databaseNnotificationUserView, is(notNullValue()));
    }

    private ag.act.model.SimpleStringResponse callApiAndGetResult() throws Exception {
        MvcResult response = mockMvc
            .perform(
                post(TARGET_API, notificationId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(status().isOk())
            .andReturn();

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            ag.act.model.SimpleStringResponse.class
        );
    }

    private Post createPost(User user, Board board) {
        return itUtil.createPost(board, user.getId());
    }

    private Notification createNotification(Post post) {
        return itUtil.createNotification(post.getId());
    }

    private NotificationUserView createNotificationUserView(Long notificationId, Long userId) {
        return itUtil.createNotificationUserView(notificationId, userId);
    }

    @Nested
    class WhenCreateNotificationUserView {

        @Nested
        class AndFound {

            @BeforeEach
            void setUp() {
                createNotificationUserView(notificationId, user.getId());
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnPosts() throws Exception {
                assertResponse(callApiAndGetResult());
            }
        }

        @Nested
        class AndNotFound {

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnPosts() throws Exception {
                assertResponse(callApiAndGetResult());
            }
        }
    }
}
