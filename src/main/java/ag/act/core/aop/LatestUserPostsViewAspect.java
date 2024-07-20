package ag.act.core.aop;

import ag.act.configuration.security.ActUserProvider;
import ag.act.dto.CreateLatestUserPostsViewDto;
import ag.act.dto.GetBoardGroupPostDto;
import ag.act.entity.User;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.PostsViewType;
import ag.act.service.notification.LatestUserPostsViewService;
import ag.act.service.stock.StockService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@RequiredArgsConstructor
@Component
public class LatestUserPostsViewAspect {
    private final StockService stockService;
    private final LatestUserPostsViewService latestUserPostsViewService;

    @Before("execution(* ag.act.facade.stock.home.StockHomeFacade.getStockHome(String)) && args(stockCode)")
    public void createLatestUserPostsView(String stockCode) {
        ActUserProvider.get().ifPresent(user -> createWhenPostsViewTypeStockHome(stockCode));
    }

    @Before(value = """
            execution(* ag.act.service.stockboardgrouppost.StockBoardGroupPostService.getBoardGroupPosts(ag.act.dto.GetBoardGroupPostDto, ..))
            && args(getBoardGroupPostDto, ..)
        """)
    public void createLatestUserPostsView(GetBoardGroupPostDto getBoardGroupPostDto) {
        if (ActUserProvider.getActUser().isGuest()) {
            return;
        }

        final String stockCode = getBoardGroupPostDto.getStockCode();
        final BoardGroup boardGroup = getBoardGroupPostDto.getBoardGroup();
        final List<BoardCategory> boardCategories = getBoardGroupPostDto.getBoardCategories();
        final boolean isStockBoardGroupCategory = !boardGroup.isGlobal() && CollectionUtils.isNotEmpty(boardCategories);

        if (isStockBoardGroupCategory) {
            return;
        }

        createWhenPostsViewTypeBoardGroup(stockCode, boardGroup);
    }

    private void createWhenPostsViewTypeStockHome(String stockCode) {
        CreateLatestUserPostsViewDto dto = buildCreateLatestUserPostsViewDto(
            stockCode,
            null,
            null,
            PostsViewType.STOCK_HOME
        );
        latestUserPostsViewService.createOrUpdate(dto);
    }

    private void createWhenPostsViewTypeBoardGroup(String stockCode, BoardGroup boardGroup) {
        CreateLatestUserPostsViewDto dto = buildCreateLatestUserPostsViewDto(
            stockCode,
            boardGroup,
            null,
            PostsViewType.BOARD_GROUP
        );
        latestUserPostsViewService.createOrUpdate(dto);
    }

    private CreateLatestUserPostsViewDto buildCreateLatestUserPostsViewDto(
        String stockCode,
        BoardGroup boardGroup,
        BoardCategory boardCategory,
        PostsViewType postsViewType
    ) {
        return CreateLatestUserPostsViewDto.builder()
            .stock(stockService.getStock(stockCode))
            .user(getUserNonNull())
            .boardGroup(boardGroup)
            .boardCategory(boardCategory)
            .postsViewType(postsViewType)
            .build();
    }

    private User getUserNonNull() {
        return ActUserProvider.getNoneNull();
    }
}
