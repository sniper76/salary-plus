package ag.act.repository;

import ag.act.entity.PostUserLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostUserLikeRepository extends JpaRepository<PostUserLike, Long> {

    List<PostUserLike> findAllByPostIdAndUserId(Long postId, Long userId);

    long countByPostId(Long postId);

    boolean existsByPostIdAndUserId(Long postId, Long userId);
}
