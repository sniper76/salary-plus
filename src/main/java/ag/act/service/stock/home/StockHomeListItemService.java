package ag.act.service.stock.home;

import ag.act.converter.home.SectionItemResponseConverter;
import ag.act.entity.Post;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.model.SectionItemResponse;
import ag.act.service.post.MostRecentThreePostQueryService;
import ag.act.service.stockboardgrouppost.BlockedUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockHomeListItemService {
    private final BlockedUserService blockedUserService;
    private final MostRecentThreePostQueryService mostRecentThreePostQueryService;
    private final SectionItemResponseConverter sectionItemResponseConverter;

    public List<SectionItemResponse> getListItems(String stockCode, BoardGroup boardGroup) {
        final List<Long> blockedUserIdList = blockedUserService.getBlockUserIdListOfMine();
        List<Post> posts = mostRecentThreePostQueryService.getPostsByStockCodeAndBoardGroup(stockCode, boardGroup, blockedUserIdList);

        return posts.stream()
            .map(sectionItemResponseConverter::convert)
            .toList();
    }

    public List<SectionItemResponse> getListItems(String stockCode, BoardCategory boardCategory) {
        List<Post> posts = mostRecentThreePostQueryService.getPostsByStockCodeAndBoardCategory(stockCode, boardCategory);

        return posts.stream()
            .map(sectionItemResponseConverter::convert)
            .toList();
    }
}
