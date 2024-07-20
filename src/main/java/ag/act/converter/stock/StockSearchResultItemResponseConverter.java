package ag.act.converter.stock;

import ag.act.converter.DateTimeConverter;
import ag.act.repository.interfaces.StockSearchResultItem;
import org.springframework.stereotype.Component;

@Component
public class StockSearchResultItemResponseConverter {

    public ag.act.model.StockResponse convert(StockSearchResultItem stockSearchResultItem) {
        return new ag.act.model.StockResponse()
            .code(stockSearchResultItem.getCode())
            .name(stockSearchResultItem.getName())
            .totalIssuedQuantity(stockSearchResultItem.getTotalIssuedQuantity())
            .representativePhoneNumber(stockSearchResultItem.getRepresentativePhoneNumber())
            .status(stockSearchResultItem.getStatus())
            .stake(stockSearchResultItem.getStake().floatValue())
            .memberCount(stockSearchResultItem.getMemberCount())
            .isPrivate(stockSearchResultItem.getIsPrivate())
            .createdAt(DateTimeConverter.convert(stockSearchResultItem.getCreatedAt()))
            .updatedAt(DateTimeConverter.convert(stockSearchResultItem.getUpdatedAt()));
    }
}
