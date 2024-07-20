package ag.act.repository;

import ag.act.SpringDataJpaTest;
import ag.act.entity.PostUserView;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled
@SpringDataJpaTest
public class PostUserViewRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PostUserViewRepository postUserViewRepository;

    @Test
    public void shouldSumViewCountByPostId() {
        // Given
        final Long postId1 = 1L;
        final Long postId2 = 2L;

        final PostUserView view1 = new PostUserView();
        view1.setPostId(postId1);
        view1.setUserId(1L);
        view1.setCount(10L);
        entityManager.persist(view1);

        final PostUserView view2 = new PostUserView();
        view2.setPostId(postId1);
        view2.setUserId(2L);
        view2.setCount(20L);
        entityManager.persist(view2);

        final PostUserView view3 = new PostUserView();
        view3.setPostId(postId2);
        view3.setUserId(3L);
        view3.setCount(30L);
        entityManager.persist(view3);

        entityManager.flush();

        // When
        Long sumPostId1 = postUserViewRepository.sumViewCountByPostId(postId1).get();
        Long sumPostId2 = postUserViewRepository.sumViewCountByPostId(postId2).get();

        // Then
        assertThat(sumPostId1).isEqualTo(30L);
        assertThat(sumPostId2).isEqualTo(30L);
    }
}
