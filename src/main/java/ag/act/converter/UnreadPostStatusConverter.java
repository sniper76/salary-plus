package ag.act.converter;

import ag.act.converter.stock.StockResponseConverter;
import ag.act.entity.Stock;
import ag.act.model.UnreadPostStatus;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UnreadPostStatusConverter {
    private final StockResponseConverter stockResponseConverter;

    public UnreadPostStatus convert(
        Boolean unreadGlobalBoard,
        Boolean unreadGlobalCommunity,
        Boolean unreadGlobalEvent,
        Boolean unreadDigitalDelegation,
        List<Stock> stocks
    ) {
        return new UnreadPostStatus()
            .unreadGlobalBoard(unreadGlobalBoard)
            .unreadGlobalCommunity(unreadGlobalCommunity)
            .unreadDigitalDelegation(unreadDigitalDelegation)
            .unreadGlobalEvent(unreadGlobalEvent)
            .unreadStocks(
                stockResponseConverter.convert(
                    ListUtils.emptyIfNull(stocks)
                )
            );
    }
}
