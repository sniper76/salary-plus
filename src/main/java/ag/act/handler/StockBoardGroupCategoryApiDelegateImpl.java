package ag.act.handler;

import ag.act.api.StockBoardGroupCategoryApiDelegate;
import ag.act.enums.BoardGroup;
import ag.act.enums.virtualboard.VirtualBoardGroup;
import ag.act.model.BoardCategoryDataArrayResponse;
import ag.act.service.BoardService;
import ag.act.service.virtualboard.VirtualBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockBoardGroupCategoryApiDelegateImpl implements StockBoardGroupCategoryApiDelegate {
    private final BoardService boardService;
    private final VirtualBoardService virtualBoardService;

    @Override
    public ResponseEntity<BoardCategoryDataArrayResponse> getBoardGroupCategories(String stockCode, String boardGroupName) {
        boolean isVirtualBoardGroup = VirtualBoardGroup.isVirtualBoardGroupName(boardGroupName);

        BoardCategoryDataArrayResponse response =
            isVirtualBoardGroup
                ? virtualBoardService.getVirtualBoardGroupCategories(VirtualBoardGroup.fromValue(boardGroupName))
                : boardService.getBoardGroupCategories(BoardGroup.fromValue(boardGroupName));

        return ResponseEntity.ok(response);
    }
}


