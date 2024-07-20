package ag.act.service.stock.home;

import ag.act.converter.home.StockHomeSectionResponseConverter;
import ag.act.dto.stock.home.ConvertStockHomeSectionResponseDto;
import ag.act.enums.BoardGroup;
import ag.act.model.SectionItemResponse;
import ag.act.model.StockHomeSectionResponse;
import ag.act.util.AppLinkUrlGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class StockHomeListBoardGroupSectionService implements StockHomeListSectionService {
    private static final List<BoardGroup> LIST_SECTIONS_BOARD_GROUPS = List.of(
        BoardGroup.DEBATE,
        BoardGroup.ANALYSIS,
        BoardGroup.ACTION
    );

    private final List<BoardGroup> listSectionsBoardGroups;
    private final StockHomeSectionResponseConverter stockHomeSectionResponseConverter;
    private final StockHomeListItemService stockHomeListItemService;
    private final AppLinkUrlGenerator appLinkUrlGenerator;

    @Autowired
    public StockHomeListBoardGroupSectionService(
        StockHomeSectionResponseConverter stockHomeSectionResponseConverter,
        StockHomeListItemService stockHomeListItemService,
        AppLinkUrlGenerator appLinkUrlGenerator
    ) {
        this(
            LIST_SECTIONS_BOARD_GROUPS,
            stockHomeSectionResponseConverter,
            stockHomeListItemService,
            appLinkUrlGenerator
        );
    }

    @Override
    public List<StockHomeSectionResponse> getSections(String stockCode) {
        return listSectionsBoardGroups
            .stream()
            .map(boardGroup -> getListSection(stockCode, boardGroup))
            .toList();
    }

    private StockHomeSectionResponse getListSection(String stockCode, BoardGroup boardGroup) {
        try {
            return getListSectionByStockCodeAndBoardGroup(stockCode, boardGroup);
        } catch (Exception e) {
            return null;
        }
    }

    private StockHomeSectionResponse getListSectionByStockCodeAndBoardGroup(String stockCode, BoardGroup boardGroup) {
        final List<SectionItemResponse> listItems = stockHomeListItemService.getListItems(stockCode, boardGroup);

        if (listItems.isEmpty()) {
            return null;
        }

        ConvertStockHomeSectionResponseDto dto = ConvertStockHomeSectionResponseDto.builder()
            .appLinkUrl(appLinkUrlGenerator.generateBoardGroupLinkUrl(stockCode, boardGroup))
            .headerTitle(boardGroup.getDisplayName())
            .listItems(listItems)
            .build();

        return stockHomeSectionResponseConverter.convert(dto);
    }
}
