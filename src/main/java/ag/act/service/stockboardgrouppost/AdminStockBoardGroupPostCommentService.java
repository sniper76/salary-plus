package ag.act.service.stockboardgrouppost;

import ag.act.constants.MessageConstants;
import ag.act.entity.Comment;
import ag.act.entity.CommentUserProfile;
import ag.act.model.CommentDataResponse;
import ag.act.model.CreateCommentRequest;
import ag.act.model.Status;
import ag.act.model.UpdateCommentRequest;
import ag.act.service.stockboardgrouppost.comment.CommentService;
import ag.act.validator.post.AdminStockBoardGroupPostCommentValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminStockBoardGroupPostCommentService implements MessageConstants {
    private final CommentService commentService;
    private final AdminStockBoardGroupPostCommentValidator adminStockBoardGroupPostCommentValidator;
    private final StockBoardGroupPostCommentService stockBoardGroupPostCommentService;

    public ag.act.model.CommentPagingResponse getBoardGroupPostComments(
        Long postId,
        Long parentCommentId,
        PageRequest pageRequest
    ) {
        return stockBoardGroupPostCommentService.getBoardGroupPostComments(postId, parentCommentId, List.of(), pageRequest);
    }

    public CommentDataResponse updatePostCommentStatus(Long commentId, String status) {
        final Status updateStatus = Status.fromValue(status);
        adminStockBoardGroupPostCommentValidator.validateUpdateStatusForAdmin(updateStatus);

        final Comment comment = saveStatus(
            commentService.getComment(commentId, getErrorMessageForUpdateStatus(updateStatus)),
            updateStatus
        );

        return getCommentDataResponse(comment, comment.getCommentUserProfile());
    }

    public CommentDataResponse updatePostComment(Long commentId, UpdateCommentRequest commentUpdateRequest) {
        final Comment comment = commentService.getComment(commentId, "대상 댓글/답글이 존재하지 않습니다.");
        adminStockBoardGroupPostCommentValidator.validateAdminRole(comment.getUserId());

        comment.setContent(commentUpdateRequest.getContent());
        final Comment savedComment = commentService.save(comment);

        return getCommentDataResponse(savedComment, savedComment.getCommentUserProfile());
    }

    public CommentDataResponse createPostComment(
        String stockCode, Long postId, CreateCommentRequest createCommentRequest
    ) {
        return stockBoardGroupPostCommentService.createBoardGroupPostCommentAndReply(stockCode, postId, 0L, createCommentRequest);
    }

    public CommentDataResponse createPostCommentReplies(
        String stockCode, Long postId, Long parentCommentId, CreateCommentRequest createCommentRequest
    ) {
        return stockBoardGroupPostCommentService.createBoardGroupPostCommentAndReply(stockCode, postId, parentCommentId, createCommentRequest);
    }

    private Comment saveStatus(Comment comment, Status status) {
        comment.setDeletedAt(LocalDateTime.now());
        comment.setStatus(status);
        return commentService.save(comment);
    }

    private String getErrorMessageForUpdateStatus(Status updateStatus) {
        final String prefix = updateStatus == Status.DELETED_BY_ADMIN ? "삭제" : "삭제취소";
        return "%s %s".formatted(prefix, COMMENT_NOT_FOUND_ERROR_MESSAGE);
    }

    private CommentDataResponse getCommentDataResponse(Comment comment, CommentUserProfile commentUserProfile) {
        return new CommentDataResponse()
            .data(
                stockBoardGroupPostCommentService.getCommentResponse(
                    comment,
                    false,
                    false,
                    commentUserProfile
                )
            );
    }
}
