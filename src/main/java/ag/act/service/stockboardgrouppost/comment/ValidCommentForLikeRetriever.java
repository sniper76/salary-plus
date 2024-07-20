package ag.act.service.stockboardgrouppost.comment;

import ag.act.constants.MessageConstants;
import ag.act.dto.post.comment.CommentLikeRequestDto;
import ag.act.entity.Comment;
import ag.act.util.StatusUtil;
import ag.act.validator.post.StockBoardGroupPostCommentValidator;
import ag.act.validator.post.StockBoardGroupPostValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ValidCommentForLikeRetriever implements MessageConstants {
    private final StockBoardGroupPostValidator stockBoardGroupPostValidator;
    private final CommentService commentService;
    private final StockBoardGroupPostCommentValidator stockBoardGroupPostCommentValidator;

    public Comment getValidCommentForLike(final CommentLikeRequestDto commentLikeRequestDto) {
        stockBoardGroupPostValidator.validateBoardGroupPost(
            commentLikeRequestDto.postId(),
            commentLikeRequestDto.stockCode(),
            commentLikeRequestDto.boardGroupName(),
            StatusUtil.getDeletedStatusesForPostDetails()
        );

        Comment comment = getComment(commentLikeRequestDto.commentId());

        stockBoardGroupPostCommentValidator.validateStatusNotDeleted(comment.getStatus());

        return comment;
    }

    private Comment getComment(Long commentId) {
        return commentService.getComment(commentId, "좋아요 %s".formatted(COMMENT_NOT_FOUND_ERROR_MESSAGE));
    }
}
