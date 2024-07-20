package ag.act.handler.stockboardgrouppost;

import ag.act.api.StockBoardGroupPostLikeApiDelegate;
import ag.act.core.guard.IsActiveUserGuard;
import ag.act.core.guard.UseGuards;
import ag.act.model.PostDataResponse;
import ag.act.service.stockboardgrouppost.StockBoardGroupPostLikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@UseGuards({IsActiveUserGuard.class})
public class StockBoardGroupPostLikeApiDelegateImpl implements StockBoardGroupPostLikeApiDelegate {
    private final StockBoardGroupPostLikeService stockBoardGroupPostLikeService;

    public StockBoardGroupPostLikeApiDelegateImpl(StockBoardGroupPostLikeService stockBoardGroupPostLikeService) {
        this.stockBoardGroupPostLikeService = stockBoardGroupPostLikeService;
    }

    @Override
    public ResponseEntity<PostDataResponse> likeBoardGroupPost(String stockCode, String boardGroupName, Long postId) {
        return ResponseEntity.ok(stockBoardGroupPostLikeService.likeBoardGroupPost(stockCode, boardGroupName, postId, true));
    }

    @Override
    public ResponseEntity<PostDataResponse> unlikeBoardGroupPost(String stockCode, String boardGroupName, Long postId) {
        return ResponseEntity.ok(stockBoardGroupPostLikeService.likeBoardGroupPost(stockCode, boardGroupName, postId, false));
    }
}
