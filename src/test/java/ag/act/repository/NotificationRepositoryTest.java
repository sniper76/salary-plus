package ag.act.repository;

import ag.act.SpringDataJpaTest;
import ag.act.entity.Post;
import ag.act.entity.notification.Notification;
import ag.act.enums.notification.NotificationCategory;
import ag.act.enums.notification.NotificationType;
import ag.act.model.Status;
import ag.act.repository.notification.NotificationRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static shiver.me.timbers.data.random.RandomLongs.someLongBetween;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

@Disabled
@SpringDataJpaTest
@Transactional
public class NotificationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    public void shouldFindAllNotifications() {
        // Given
        final int recordCount = 10;

        for (int i = 0; i < recordCount; i++) {
            createNotification();
        }

        // When
        final List<Notification> notificationList = notificationRepository.findAll();

        // Then
        assertThat(notificationList.size(), is(recordCount));

        for (Notification notification : notificationList) {
            assertThat(notification.getPostId(), notNullValue());
        }
    }

    private Notification createNotification() {
        final Post post = createPost();

        final Notification notification = new Notification();
        notification.setPostId(post.getId());
        notification.setCategory(NotificationCategory.STOCKHOLDER_ACTION);
        notification.setType(NotificationType.POST);
        notification.setStatus(Status.ACTIVE);

        return entityManager.persistFlushFind(notification);
    }


    public Post createPost() {
        final Post post = new Post();

        post.setBoardId(1L);
        post.setTitle(someAlphanumericString(10));
        post.setAnonymousName(someAlphanumericString(10));
        post.setIsAnonymous(Boolean.FALSE);
        post.setContent(someAlphanumericString(10));
        post.setStatus(Status.ACTIVE);
        post.setUserId(100L);
        post.setViewCount(someLongBetween(0L, 100L));
        post.setLikeCount(someLongBetween(0L, 100L));
        post.setViewUserCount(someLongBetween(0L, 100L));
        post.setCommentCount(someLongBetween(0L, 100L));

        return entityManager.persistFlushFind(post);
    }
}
