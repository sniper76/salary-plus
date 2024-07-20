package ag.act.repository;

import ag.act.entity.Comment;
import ag.act.model.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findFirstByPostIdAndIsAnonymousTrueOrderByAnonymousCountDesc(Long postId);

    Optional<Comment> findFirstByPostIdAndUserIdAndIsAnonymous(Long postId, Long userId, Boolean isAnonymous);

    long countByPostId(Long postId);

    long countByPostIdAndParentIdAndStatusNotIn(
        Long postId, Long parentCommentId,  List<Status> statuses);

    Page<Comment> findAllByPostIdAndParentIdIsNullAndStatusNotInAndUserIdNotIn(
        Long postId, List<Status> statuses, List<Long> userIdList, PageRequest pageRequest);

    Page<Comment> findAllByPostIdAndParentIdAndStatusNotInAndUserIdNotIn(
        Long postId, Long parentCommentId, List<Status> statuses, List<Long> userIdList, PageRequest pageRequest);

    List<Comment> findAllByParentId(Long parentId);

    Long countByPostIdAndUserIdNot(Long postId, Long userId);

    Long countByPostIdAndParentIdAndUserIdNot(Long postId, Long parentCommentId, Long userId);

    @Query(value = """
        SELECT id, 
               user_id, 
               post_id, 
               parent_id, 
               type, 
               content, 
               anonymous_count, 
               like_count, 
               reply_comment_count, 
               is_anonymous, 
               status, 
               created_at, 
               updated_at, 
               deleted_at, 
               edited_at 
        FROM (
            SELECT * , ROW_NUMBER() OVER (PARTITION BY parent_id ORDER BY created_at) AS row_num
            FROM comments 
            WHERE comments.type = 'REPLY_COMMENT' 
              AND parent_id IN :parentIds 
              AND id NOT IN :blockedUserList 
              AND status NOT IN :statuses
            ) subquery
            WHERE subquery.row_num <= :size
        """, nativeQuery = true)
    List<Comment> findAllByParentIdInAndIdNotInAndStatusNotIn(List<Long> parentIds, List<String> statuses, List<Long> blockedUserList, int size);

    Optional<Comment> findFirstByUserIdAndPostIdAndCreatedAtAfterOrderByCreatedAtDesc(Long userId, Long postId, LocalDateTime targetDateTime);
}
