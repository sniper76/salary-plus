package ag.act.handler.stockboardgrouppost;

import ag.act.api.StockBoardGroupPostCommentReplyApiDelegate;
import ag.act.converter.PageDataConverter;
import ag.act.core.annotation.HtmlContentTarget;
import ag.act.core.guard.ContentCreateGuard;
import ag.act.core.guard.IsActiveUserGuard;
import ag.act.core.guard.UseGuards;
import ag.act.facade.post.CommentFacade;
import ag.act.model.CommentDataResponse;
import ag.act.model.CommentPagingResponse;
import ag.act.model.CreateCommentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockBoardGroupPostCommentReplyApiDelegateImpl implements StockBoardGroupPostCommentReplyApiDelegate {
    private final CommentFacade commentFacade;
    private final PageDataConverter pageDataConverter;

    @UseGuards({IsActiveUserGuard.class, ContentCreateGuard.class})
    @Override
    public ResponseEntity<CommentDataResponse> createBoardGroupPostCommentReply(
        String stockCode,
        String boardGroupName,
        Long postId,
        Long commentId,
        @HtmlContentTarget CreateCommentRequest createCommentRequest
    ) {
        return ResponseEntity.ok(
            commentFacade.createBoardGroupPostCommentReply(
                stockCode, boardGroupName, postId, commentId, createCommentRequest
            )
        );
    }

    @Override
    public ResponseEntity<CommentPagingResponse> getBoardGroupPostCommentsReplies(
        String stockCode, String boardGroupName,
        Long postId, Long commentId,
        Integer page, Integer size, List<String> sorts
    ) {
        final PageRequest pageRequest = pageDataConverter.convert(page, size, sorts);
        return ResponseEntity.ok(
            commentFacade.getBoardGroupPostComments(
                stockCode, boardGroupName, postId, commentId, pageRequest
            )
        );
    }
}
