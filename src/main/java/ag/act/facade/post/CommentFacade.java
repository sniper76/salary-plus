package ag.act.facade.post;

import ag.act.configuration.security.ActUserProvider;
import ag.act.entity.Comment;
import ag.act.entity.User;
import ag.act.exception.BadRequestException;
import ag.act.model.CommentDataResponse;
import ag.act.model.CommentPagingResponse;
import ag.act.model.CreateCommentRequest;
import ag.act.model.SimpleStringResponse;
import ag.act.model.UpdateCommentRequest;
import ag.act.service.stockboardgrouppost.BlockedUserService;
import ag.act.service.stockboardgrouppost.StockBoardGroupPostCommentService;
import ag.act.service.stockboardgrouppost.comment.CommentService;
import ag.act.util.StatusUtil;
import ag.act.validator.post.StockBoardGroupPostValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentFacade {
    private final StockBoardGroupPostValidator stockBoardGroupPostValidator;
    private final StockBoardGroupPostCommentService stockBoardGroupPostCommentService;
    private final CommentService commentService;
    private final BlockedUserService blockedUserService;

    public CommentDataResponse createBoardGroupPostComment(
        String stockCode, String boardGroupName, Long postId,
        CreateCommentRequest createCommentRequest
    ) {
        validateBoardGroupPost(stockCode, boardGroupName, postId);

        return stockBoardGroupPostCommentService.createBoardGroupPostComment(
            stockCode, postId, createCommentRequest
        );
    }

    public CommentDataResponse createBoardGroupPostCommentReply(
        String stockCode, String boardGroupName, Long postId, Long parentCommentId,
        ag.act.model.CreateCommentRequest createCommentRequest
    ) {
        validateForReply(stockCode, boardGroupName, postId, parentCommentId);

        return stockBoardGroupPostCommentService.createBoardGroupPostCommentAndReply(
            stockCode, postId, parentCommentId, createCommentRequest
        );
    }

    public CommentDataResponse updateBoardGroupPostComment(
        String stockCode, String boardGroupName, Long postId, Long commentId,
        UpdateCommentRequest updateCommentRequest
    ) {
        validateBoardGroupPost(stockCode, boardGroupName, postId);

        User user = ActUserProvider.getNoneNull();
        return stockBoardGroupPostCommentService.updateBoardGroupPostCommentAndReply(
            user.getId(), postId, commentId, updateCommentRequest
        );
    }

    public SimpleStringResponse deleteBoardGroupPostComment(
        String stockCode, String boardGroupName, Long postId, Long commentId
    ) {
        validateBoardGroupPost(stockCode, boardGroupName, postId);

        return stockBoardGroupPostCommentService.deleteBoardGroupPostComment(postId, commentId);
    }

    public CommentPagingResponse getBoardGroupPostComments(
        String stockCode, String boardGroupName, Long postId,
        Long parentCommentId, PageRequest pageRequest
    ) {
        validateBoardGroupPost(stockCode, boardGroupName, postId);

        return stockBoardGroupPostCommentService.getBoardGroupPostComments(
            postId,
            parentCommentId,
            blockedUserService.getBlockUserIdListOfMine(),
            pageRequest
        );
    }

    private void validateForReply(String stockCode, String boardGroupName, Long postId, Long parentCommentId) {
        validateBoardGroupPost(stockCode, boardGroupName, postId);
        Comment comment = commentService.getComment(parentCommentId, "답글 대상 댓글이 존재하지 않습니다.");
        if (comment.getParentId() != null) {
            throw new BadRequestException("답글 작성할 수 없는 댓글입니다.");
        }
    }

    private void validateBoardGroupPost(String stockCode, String boardGroupName, Long postId) {
        stockBoardGroupPostValidator.validateBoardGroupPost(
            postId, stockCode, boardGroupName, ActUserProvider.getActUser(), StatusUtil.getDeletedStatusesForPostDetails()
        );
    }
}
