package ag.act.handler.stockboardgrouppost;

import ag.act.api.StockBoardGroupPostCommentApiDelegate;
import ag.act.converter.PageDataConverter;
import ag.act.core.annotation.HtmlContentTarget;
import ag.act.core.guard.ContentCreateGuard;
import ag.act.core.guard.ContentUpdateGuard;
import ag.act.core.guard.IsActiveUserGuard;
import ag.act.core.guard.UseGuards;
import ag.act.facade.post.CommentFacade;
import ag.act.model.CommentDataResponse;
import ag.act.model.CommentPagingResponse;
import ag.act.model.CreateCommentRequest;
import ag.act.model.SimpleStringResponse;
import ag.act.model.UpdateCommentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockBoardGroupPostCommentApiDelegateImpl implements StockBoardGroupPostCommentApiDelegate {
    private final CommentFacade commentFacade;
    private final PageDataConverter pageDataConverter;

    @UseGuards({IsActiveUserGuard.class, ContentCreateGuard.class})
    @Override
    public ResponseEntity<CommentDataResponse> createBoardGroupPostComment(
        String stockCode,
        String boardGroupName,
        Long postId,
        @HtmlContentTarget CreateCommentRequest createCommentRequest
    ) {
        return ResponseEntity.ok(
            commentFacade.createBoardGroupPostComment(
                stockCode, boardGroupName, postId, createCommentRequest
            )
        );
    }

    @UseGuards({IsActiveUserGuard.class, ContentUpdateGuard.class})
    @Override
    public ResponseEntity<CommentDataResponse> updateBoardGroupPostComment(
        String stockCode,
        String boardGroupName,
        Long postId,
        Long commentId,
        @HtmlContentTarget UpdateCommentRequest updateCommentRequest
    ) {
        return ResponseEntity.ok(
            commentFacade.updateBoardGroupPostComment(
                stockCode, boardGroupName, postId, commentId, updateCommentRequest
            )
        );
    }

    @UseGuards({IsActiveUserGuard.class})
    @Override
    public ResponseEntity<SimpleStringResponse> deleteBoardGroupPostComment(
        String stockCode, String boardGroupName, Long postId, Long commentId
    ) {
        return ResponseEntity.ok(
            commentFacade.deleteBoardGroupPostComment(
                stockCode, boardGroupName, postId, commentId
            )
        );
    }

    @Override
    public ResponseEntity<CommentPagingResponse> getBoardGroupPostComments(
        String stockCode, String boardGroupName, Long postId,
        Integer page, Integer size, List<String> sorts
    ) {
        final PageRequest pageRequest = pageDataConverter.convert(page, size, sorts);
        return ResponseEntity.ok(
            commentFacade.getBoardGroupPostComments(
                stockCode, boardGroupName, postId, 0L, pageRequest
            )
        );
    }
}
