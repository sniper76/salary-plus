package ag.act.repository;

import ag.act.entity.PostUserView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PostUserViewRepository extends JpaRepository<PostUserView, Long> {

    Optional<PostUserView> findByPostIdAndUserId(Long postId, Long userId);

    Long countByPostId(Long postId);

    @Query("SELECT SUM(p.count) FROM PostUserView p WHERE p.postId = :postId")
    Optional<Long> sumViewCountByPostId(@Param("postId") Long postId);

    @Query("SELECT SUM(p.count) FROM PostUserView p WHERE p.createdAt between :start and :end")
    Optional<Long> sumViewCount(LocalDateTime start, LocalDateTime end);
}
