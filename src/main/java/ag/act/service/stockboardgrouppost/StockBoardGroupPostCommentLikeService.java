package ag.act.service.stockboardgrouppost;

import ag.act.configuration.security.ActUserProvider;
import ag.act.constants.MessageConstants;
import ag.act.dto.post.comment.CommentLikeRequestDto;
import ag.act.entity.Comment;
import ag.act.entity.CommentUserLike;
import ag.act.exception.BadRequestException;
import ag.act.service.stockboardgrouppost.comment.CommentService;
import ag.act.service.stockboardgrouppost.comment.CommentUserLikeService;
import ag.act.service.stockboardgrouppost.comment.ValidCommentForLikeRetriever;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class StockBoardGroupPostCommentLikeService implements MessageConstants {
    private final CommentService commentService;
    private final CommentUserLikeService commentUserLikeService;
    private final ValidCommentForLikeRetriever validCommentForLikeRetriever;

    public void like(CommentLikeRequestDto requestDto) {
        updateLikeUnLikeComment(
            requestDto,
            commentUserLike -> {
                if (commentUserLike.isEmpty()) {
                    likeComment(
                        requestDto.postId(),
                        ActUserProvider.getNoneNull().getId(),
                        requestDto.commentId()
                    );
                }
            }
        );
    }

    public void unlike(CommentLikeRequestDto requestDto) {
        updateLikeUnLikeComment(
            requestDto,
            commentUserLike -> commentUserLike.ifPresent(this::unlikeComment)
        );
    }

    private void updateLikeUnLikeComment(CommentLikeRequestDto requestDto, Consumer<Optional<CommentUserLike>> consumer) {
        final Optional<CommentUserLike> commentUserLike = findMyCommentUserLike(requestDto.postId(), requestDto.commentId());

        consumer.accept(commentUserLike);

        updateCommentLikeCount(requestDto);
    }

    private Optional<CommentUserLike> findMyCommentUserLike(final Long postId, final Long commentId) {
        final Long likedUserId = ActUserProvider.getNoneNull().getId();
        return commentUserLikeService.findCommentUserLike(postId, likedUserId, commentId);
    }

    private void updateCommentLikeCount(CommentLikeRequestDto requestDto) {
        Comment comment = validCommentForLikeRetriever.getValidCommentForLike(requestDto);
        comment.setLikeCount(commentUserLikeService.countByCommentLike(requestDto.postId(), requestDto.commentId()));

        commentService.save(comment);
    }

    private void unlikeComment(CommentUserLike commentUserLike) {
        commentUserLikeService.deleteCommentUserLike(commentUserLike);
    }

    private void likeComment(Long postId, Long userId, Long commentId) {
        CommentUserLike commentUserLike = new CommentUserLike();
        commentUserLike.setPostId(postId);
        commentUserLike.setUserId(userId);
        commentUserLike.setCommentId(commentId);

        try {
            commentUserLikeService.saveCommentUserLike(commentUserLike);
        } catch (DataIntegrityViolationException e) {
            log.warn("DataIntegrityViolationException occurred to create comment like", e);
        } catch (Exception e) {
            log.error("Error occurred to create comment like", e);
            throw new BadRequestException("좋아요 등록중 오류가 발생하였습니다.", e);
        }
    }
}
