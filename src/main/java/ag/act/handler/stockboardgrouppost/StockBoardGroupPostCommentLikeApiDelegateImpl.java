package ag.act.handler.stockboardgrouppost;

import ag.act.api.StockBoardGroupPostCommentLikeApiDelegate;
import ag.act.core.guard.IsActiveUserGuard;
import ag.act.core.guard.UseGuards;
import ag.act.dto.post.comment.CommentLikeRequestDto;
import ag.act.model.SimpleStringResponse;
import ag.act.service.stockboardgrouppost.StockBoardGroupPostCommentLikeService;
import ag.act.util.SimpleStringResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@UseGuards({IsActiveUserGuard.class})
public class StockBoardGroupPostCommentLikeApiDelegateImpl implements StockBoardGroupPostCommentLikeApiDelegate {
    private final StockBoardGroupPostCommentLikeService stockBoardGroupPostCommentLikeService;

    @Override
    public ResponseEntity<SimpleStringResponse> likeBoardGroupPostComment(
        String stockCode, String boardGroupName, Long postId, Long commentId
    ) {
        stockBoardGroupPostCommentLikeService.like(
            CommentLikeRequestDto.of(
                stockCode, boardGroupName, postId, commentId
            )
        );

        return SimpleStringResponseUtil.okResponse();
    }

    @Override
    public ResponseEntity<SimpleStringResponse> unlikeBoardGroupPostComment(
        String stockCode, String boardGroupName, Long postId, Long commentId
    ) {
        stockBoardGroupPostCommentLikeService.unlike(
            CommentLikeRequestDto.of(
                stockCode, boardGroupName, postId, commentId
            )
        );

        return SimpleStringResponseUtil.okResponse();
    }
}
