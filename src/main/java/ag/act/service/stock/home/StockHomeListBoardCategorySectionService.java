package ag.act.service.stock.home;

import ag.act.converter.home.StockHomeSectionResponseConverter;
import ag.act.dto.stock.home.ConvertStockHomeSectionResponseDto;
import ag.act.enums.BoardCategory;
import ag.act.model.SectionItemResponse;
import ag.act.model.StockHomeSectionResponse;
import ag.act.util.AppLinkUrlGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static ag.act.enums.BoardCategory.CO_HOLDING_ARRANGEMENTS;
import static ag.act.enums.BoardCategory.DAILY_ACT;
import static ag.act.enums.BoardCategory.DEBATE;
import static ag.act.enums.BoardCategory.DIGITAL_DELEGATION;
import static ag.act.enums.BoardCategory.ETC;
import static ag.act.enums.BoardCategory.SOLIDARITY_LEADER_LETTERS;
import static ag.act.enums.BoardCategory.SURVEYS;

@Slf4j
// @Service
@AllArgsConstructor
public class StockHomeListBoardCategorySectionService implements StockHomeListSectionService {
    private static final List<BoardCategory> LIST_SECTIONS_BOARD_CATEGORIES = List.of(
        DEBATE,
        SOLIDARITY_LEADER_LETTERS,
        DAILY_ACT,
        DIGITAL_DELEGATION,
        CO_HOLDING_ARRANGEMENTS,
        ETC,
        SURVEYS
    );

    private final List<BoardCategory> listSectionsBoardCategories;
    private final StockHomeSectionResponseConverter stockHomeSectionResponseConverter;
    private final StockHomeListItemService stockHomeListItemService;
    private final AppLinkUrlGenerator appLinkUrlGenerator;

    // @Autowired
    public StockHomeListBoardCategorySectionService(
        StockHomeSectionResponseConverter stockHomeSectionResponseConverter,
        StockHomeListItemService stockHomeListItemService,
        AppLinkUrlGenerator appLinkUrlGenerator
    ) {
        this(
            LIST_SECTIONS_BOARD_CATEGORIES,
            stockHomeSectionResponseConverter,
            stockHomeListItemService,
            appLinkUrlGenerator
        );
    }

    @Override
    public List<StockHomeSectionResponse> getSections(String stockCode) {
        return listSectionsBoardCategories
            .stream()
            .map(boardCategory -> getListSection(stockCode, boardCategory))
            .toList();
    }

    private StockHomeSectionResponse getListSection(String stockCode, BoardCategory boardCategory) {
        try {
            return getListSectionByStockCodeAndBoardCategory(stockCode, boardCategory);
        } catch (Exception e) {
            return null;
        }
    }

    private StockHomeSectionResponse getListSectionByStockCodeAndBoardCategory(String stockCode, BoardCategory boardCategory) {
        final List<SectionItemResponse> listItems = stockHomeListItemService.getListItems(stockCode, boardCategory);

        if (listItems.isEmpty()) {
            return null;
        }

        ConvertStockHomeSectionResponseDto dto = ConvertStockHomeSectionResponseDto.builder()
            .appLinkUrl(appLinkUrlGenerator.generateBoardGroupCategoryLinkUrl(stockCode, boardCategory))
            .headerTitle(boardCategory.getDisplayName())
            .listItems(listItems)
            .build();

        return stockHomeSectionResponseConverter.convert(dto);
    }
}
