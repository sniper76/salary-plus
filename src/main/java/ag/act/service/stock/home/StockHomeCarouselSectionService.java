package ag.act.service.stock.home;

import ag.act.converter.home.CarouselItemConverter;
import ag.act.converter.home.StockHomeSectionResponseConverter;
import ag.act.entity.Post;
import ag.act.enums.BoardGroup;
import ag.act.model.CarouselItemResponse;
import ag.act.model.StockHomeSectionResponse;
import ag.act.service.post.MostRecentThreePostQueryService;
import ag.act.service.stockboardgrouppost.BlockedUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockHomeCarouselSectionService {
    private static final List<BoardGroup> CAROUSEL_SECTIONS_BOARD_GROUP = List.of();
    private final MostRecentThreePostQueryService mostRecentThreePostQueryService;
    private final StockHomeSectionResponseConverter stockHomeSectionResponseConverter;
    private final CarouselItemConverter carouselItemConverter;
    private final BlockedUserService blockedUserService;

    public StockHomeSectionResponse getSections(String stockCode) {
        List<CarouselItemResponse> carouselItems = generateCarouselItems(stockCode);
        if (carouselItems.isEmpty()) {
            return null;
        }

        return stockHomeSectionResponseConverter.convert(carouselItems);
    }

    private List<CarouselItemResponse> generateCarouselItems(String stockCode) {
        return CAROUSEL_SECTIONS_BOARD_GROUP
            .stream()
            .map(boardGroup -> generateCarouselItem(stockCode, boardGroup))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    private CarouselItemResponse generateCarouselItem(String stockCode, BoardGroup boardGroup) {
        try {
            return generateCarouselItemWithPosts(stockCode, boardGroup);
        } catch (Exception e) {
            return null;
        }

    }

    private CarouselItemResponse generateCarouselItemWithPosts(String stockCode, BoardGroup boardGroup) {
        final List<Long> blockedUserIdList = blockedUserService.getBlockUserIdListOfMine();
        List<Post> posts = mostRecentThreePostQueryService.getPostsByStockCodeAndBoardGroup(stockCode, boardGroup, blockedUserIdList);
        if (posts.isEmpty()) {
            return null;
        }

        return carouselItemConverter.convert(stockCode, boardGroup, posts);
    }
}
