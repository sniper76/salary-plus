package ag.act.facade.admin.post;

import ag.act.core.guard.BoardGroupPostGuard;
import ag.act.core.guard.UseGuards;
import ag.act.model.CommentDataResponse;
import ag.act.model.CommentPagingResponse;
import ag.act.model.CommentUpdateStatusRequest;
import ag.act.model.CreateCommentRequest;
import ag.act.model.UpdateCommentRequest;
import ag.act.service.stockboardgrouppost.AdminStockBoardGroupPostCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@SuppressWarnings("unused")
@RequiredArgsConstructor
@Service
public class AdminPostCommentFacade {
    private final AdminStockBoardGroupPostCommentService adminStockBoardGroupPostCommentService;

    @UseGuards({BoardGroupPostGuard.class})
    public CommentPagingResponse getComments(String stockCode, String boardGroupName, Long postId, PageRequest pageRequest) {
        return adminStockBoardGroupPostCommentService.getBoardGroupPostComments(postId, 0L, pageRequest);
    }

    @UseGuards({BoardGroupPostGuard.class})
    public CommentPagingResponse getReplies(String stockCode, String boardGroupName, Long postId, Long commentId, PageRequest pageRequest) {
        return adminStockBoardGroupPostCommentService.getBoardGroupPostComments(postId, commentId, pageRequest);
    }

    @UseGuards({BoardGroupPostGuard.class})
    public CommentDataResponse updatePostCommentStatus(
        String stockCode, String boardGroupName, Long postId, Long commentId,
        CommentUpdateStatusRequest commentUpdateStatusRequest
    ) {
        return adminStockBoardGroupPostCommentService.updatePostCommentStatus(commentId, commentUpdateStatusRequest.getStatus());
    }

    @UseGuards({BoardGroupPostGuard.class})
    public CommentDataResponse updatePostComment(
        String stockCode, String boardGroupName, Long postId, Long commentId, UpdateCommentRequest commentUpdateRequest
    ) {
        return adminStockBoardGroupPostCommentService.updatePostComment(commentId, commentUpdateRequest);
    }

    @UseGuards({BoardGroupPostGuard.class})
    public CommentDataResponse createPostComment(
        String stockCode, String boardGroupName, Long postId, CreateCommentRequest createCommentRequest
    ) {
        return adminStockBoardGroupPostCommentService.createPostComment(stockCode, postId, createCommentRequest);
    }

    @UseGuards({BoardGroupPostGuard.class})
    public CommentDataResponse createPostCommentReplies(
        String stockCode, String boardGroupName, Long postId, Long parentCommentId, CreateCommentRequest createCommentRequest
    ) {
        return adminStockBoardGroupPostCommentService.createPostCommentReplies(
            stockCode, postId, parentCommentId, createCommentRequest
        );
    }
}
