package ag.act.converter.stock;

import ag.act.converter.DateTimeConverter;
import ag.act.entity.Stock;
import ag.act.entity.StockGroup;
import ag.act.repository.interfaces.StockGroupSearchResultItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class StockGroupItemResponseConverter {

    private final SimpleStockResponseConverter simpleStockResponseConverter;

    public ag.act.model.StockGroupResponse convert(StockGroupSearchResultItem stockGroupSearchResultItem) {
        return new ag.act.model.StockGroupResponse()
            .id(stockGroupSearchResultItem.getId())
            .name(stockGroupSearchResultItem.getName())
            .description(stockGroupSearchResultItem.getDescription())
            .status(stockGroupSearchResultItem.getStatus())
            .stockCount(stockGroupSearchResultItem.getStockCount())
            .createdAt(DateTimeConverter.convert(stockGroupSearchResultItem.getCreatedAt()))
            .updatedAt(DateTimeConverter.convert(stockGroupSearchResultItem.getUpdatedAt()))
            .deletedAt(DateTimeConverter.convert(stockGroupSearchResultItem.getDeletedAt()));
    }

    public ag.act.model.StockGroupResponse convert(StockGroup stockGroup) {
        return new ag.act.model.StockGroupResponse()
            .id(stockGroup.getId())
            .name(stockGroup.getName())
            .description(stockGroup.getDescription())
            .status(stockGroup.getStatus())
            .stockCount(null)
            .createdAt(DateTimeConverter.convert(stockGroup.getCreatedAt()))
            .updatedAt(DateTimeConverter.convert(stockGroup.getUpdatedAt()))
            .deletedAt(DateTimeConverter.convert(stockGroup.getDeletedAt()));
    }

    public ag.act.model.StockGroupDetailsResponse convertToDetails(StockGroup stockGroup, List<Stock> stocks) {
        return new ag.act.model.StockGroupDetailsResponse()
            .id(stockGroup.getId())
            .name(stockGroup.getName())
            .description(stockGroup.getDescription())
            .status(stockGroup.getStatus())
            .createdAt(DateTimeConverter.convert(stockGroup.getCreatedAt()))
            .updatedAt(DateTimeConverter.convert(stockGroup.getUpdatedAt()))
            .deletedAt(DateTimeConverter.convert(stockGroup.getDeletedAt()))
            .stocks(simpleStockResponseConverter.convertList(stocks));
    }
}
