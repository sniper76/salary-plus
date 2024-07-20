package ag.act.core.aop;

import ag.act.dto.CreateLatestPostTimestampDto;
import ag.act.dto.post.CreatePostRequestDto;
import ag.act.entity.Board;
import ag.act.entity.Stock;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.service.notification.LatestPostTimestampService;
import ag.act.service.stock.StockService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@RequiredArgsConstructor
@Component
public class LatestPostTimestampAspect {
    private final StockService stockService;
    private final LatestPostTimestampService latestPostTimestampService;

    @Before("""
            execution(* ag.act.service.stockboardgrouppost
            .StockBoardGroupPostCreateService.createBoardGroupPost(ag.act.dto.post.CreatePostRequestDto, ..))
            && args(createPostRequestDto, ..)
        """)
    public void createLatestPostTimestamp(CreatePostRequestDto createPostRequestDto) {
        Stock stock = stockService.getStock(createPostRequestDto.getStockCode());
        BoardGroup boardGroup = BoardGroup.fromValue(createPostRequestDto.getBoardGroupName());
        BoardCategory boardCategory = BoardCategory.fromValue(
            createPostRequestDto.getCreatePostRequest().getBoardCategory()
        );

        if (boardCategory == null) {
            return;
        }

        createOrUpdateLatestPostTimestamp(stock, boardGroup, boardCategory);
    }

    @Before("execution(* ag.act.service.post.duplication.PostDuplicateProcessor.copyPost(..)) && args(.., newBoard)")
    public void createLatestPostTimestampOf(Board newBoard) {
        final Stock stock = newBoard.getStock();
        final BoardCategory boardCategory = newBoard.getCategory();
        final BoardGroup boardGroup = boardCategory.getBoardGroup();

        createOrUpdateLatestPostTimestamp(stock, boardGroup, boardCategory);
    }

    private void createOrUpdateLatestPostTimestamp(Stock stock, BoardGroup boardGroup, BoardCategory boardCategory) {
        latestPostTimestampService.createOrUpdate(
            CreateLatestPostTimestampDto.builder()
                .stock(stock)
                .boardGroup(boardGroup)
                .boardCategory(boardCategory)
                .build()
        );
    }
}
