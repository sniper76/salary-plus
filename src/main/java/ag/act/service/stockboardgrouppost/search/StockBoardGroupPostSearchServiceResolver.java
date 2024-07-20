package ag.act.service.stockboardgrouppost.search;

import ag.act.enums.admin.PostSearchType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class StockBoardGroupPostSearchServiceResolver {

    private final List<StockBoardGroupPostSearchService> stockBoardGroupPostSearchServices;
    private final StockBoardGroupPostSearchByTitleService stockBoardGroupPostSearchByTitleService;

    public StockBoardGroupPostSearchService resolve(PostSearchType postSearchType) {
        return stockBoardGroupPostSearchServices
            .stream()
            .filter(stockBoardGroupPostSearchService -> stockBoardGroupPostSearchService.supports(postSearchType))
            .findFirst()
            .orElseGet(() -> stockBoardGroupPostSearchByTitleService);
    }
}
