package ag.act.handler.admin.comment;

import ag.act.api.AdminBoardGroupPostCommentApiDelegate;
import ag.act.converter.PageDataConverter;
import ag.act.core.annotation.HtmlContentTarget;
import ag.act.core.guard.IsAdminGuard;
import ag.act.core.guard.IsAdminOrAcceptorUserGuard;
import ag.act.core.guard.UseGuards;
import ag.act.facade.admin.post.AdminPostCommentFacade;
import ag.act.model.CommentDataResponse;
import ag.act.model.CommentPagingResponse;
import ag.act.model.CommentUpdateStatusRequest;
import ag.act.model.CreateCommentRequest;
import ag.act.model.UpdateCommentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AdminBoardGroupPostCommentApiDelegateImpl implements AdminBoardGroupPostCommentApiDelegate {
    private final AdminPostCommentFacade adminCommentFacade;
    private final PageDataConverter pageDataConverter;

    @UseGuards(IsAdminOrAcceptorUserGuard.class)
    @Override
    public ResponseEntity<CommentPagingResponse> getComments(
        String stockCode, String boardGroupName, Long postId,
        Integer page, Integer size, List<String> sorts
    ) {
        final PageRequest pageRequest = pageDataConverter.convert(page, size, sorts);
        return ResponseEntity.ok(adminCommentFacade.getComments(stockCode, boardGroupName, postId, pageRequest));
    }

    @UseGuards(IsAdminOrAcceptorUserGuard.class)
    @Override
    public ResponseEntity<CommentPagingResponse> getReplies(
        String stockCode, String boardGroupName, Long postId, Long commentId,
        Integer page, Integer size, List<String> sorts
    ) {
        final PageRequest pageRequest = pageDataConverter.convert(page, size, sorts);
        return ResponseEntity.ok(adminCommentFacade.getReplies(stockCode, boardGroupName, postId, commentId, pageRequest));
    }

    @UseGuards(IsAdminGuard.class)
    @Override
    public ResponseEntity<CommentDataResponse> updateStatusPostComment(
        String stockCode, String boardGroupName, Long postId, Long commentId,
        CommentUpdateStatusRequest commentUpdateStatusRequest
    ) {
        return ResponseEntity.ok(
            adminCommentFacade.updatePostCommentStatus(stockCode, boardGroupName, postId, commentId, commentUpdateStatusRequest)
        );
    }

    @UseGuards(IsAdminGuard.class)
    @Override
    public ResponseEntity<CommentDataResponse> updatePostComment(
        String stockCode,
        String boardGroupName,
        Long postId,
        Long commentId,
        @HtmlContentTarget UpdateCommentRequest commentUpdateRequest
    ) {
        return ResponseEntity.ok(
            adminCommentFacade.updatePostComment(stockCode, boardGroupName, postId, commentId, commentUpdateRequest)
        );
    }

    @UseGuards(IsAdminGuard.class)
    @Override
    public ResponseEntity<CommentDataResponse> createPostComment(
        String stockCode,
        String boardGroupName,
        Long postId,
        @HtmlContentTarget CreateCommentRequest createCommentRequest
    ) {
        return ResponseEntity.ok(
            adminCommentFacade.createPostComment(stockCode, boardGroupName, postId, createCommentRequest)
        );
    }

    @UseGuards(IsAdminGuard.class)
    @Override
    public ResponseEntity<CommentDataResponse> createPostCommentReplies(
        String stockCode, String boardGroupName, Long postId, Long commentId, CreateCommentRequest createCommentRequest
    ) {
        return ResponseEntity.ok(
            adminCommentFacade.createPostCommentReplies(stockCode, boardGroupName, postId, commentId, createCommentRequest)
        );
    }
}
