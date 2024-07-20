package ag.act.service.stockboardgrouppost.comment;

import ag.act.entity.CommentUserLike;
import ag.act.repository.CommentUserLikeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CommentUserLikeService {
    private final CommentUserLikeRepository commentUserLikeRepository;

    public CommentUserLikeService(CommentUserLikeRepository commentUserLikeRepository) {
        this.commentUserLikeRepository = commentUserLikeRepository;
    }

    public void deleteCommentUserLike(CommentUserLike deleteLike) {
        commentUserLikeRepository.delete(deleteLike);
    }

    public Long countByCommentLike(Long postId, Long commentId) {
        return commentUserLikeRepository.countByPostIdAndCommentId(postId, commentId);
    }

    public void saveCommentUserLike(CommentUserLike postUserLike) {
        commentUserLikeRepository.save(postUserLike);
    }

    public List<CommentUserLike> findAllByCommentIdIn(Long postId, Long userId, List<Long> commentIds) {
        return commentUserLikeRepository.findAllByPostIdAndUserIdAndCommentIdIn(postId, userId, commentIds);
    }

    public Optional<CommentUserLike> findCommentUserLike(Long postId, Long userId, Long commentId) {
        return commentUserLikeRepository.findByPostIdAndUserIdAndCommentId(postId, userId, commentId);
    }
}
