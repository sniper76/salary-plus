package ag.act.repository;

import ag.act.entity.CommentUserLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentUserLikeRepository extends JpaRepository<CommentUserLike, Long> {

    List<CommentUserLike> findAllByPostIdAndUserIdAndCommentIdIn(Long postId, Long userId, List<Long> commentIds);

    Optional<CommentUserLike> findByPostIdAndUserIdAndCommentId(Long postId, Long userId, Long commentId);

    List<CommentUserLike> findAllByPostIdAndCommentIdIn(Long postId, List<Long> commentIds);

    Long countByPostIdAndCommentId(Long postId, Long commentId);
}
