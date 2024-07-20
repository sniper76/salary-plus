package ag.act.converter.user;

import ag.act.converter.stock.SimpleStockResponseConverter;
import ag.act.entity.UserHoldingStock;
import ag.act.model.MyStockAuthenticationResponse;
import ag.act.service.user.UserHoldingStockService;
import ag.act.util.badge.BadgeLabelGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MyStockAuthenticationResponseConverter {
    private final SimpleStockResponseConverter simpleStockResponseConverter;
    private final UserHoldingStockService userHoldingStockService;
    private final BadgeLabelGenerator badgeLabelGenerator;

    public MyStockAuthenticationResponse convert(Long userId, String stockCode) {
        final UserHoldingStock userHoldingStock = userHoldingStockService.getUserHoldingStock(userId, stockCode);
        return new MyStockAuthenticationResponse()
            .stock(simpleStockResponseConverter.convert(userHoldingStock.getStock()))
            .individualStockCountLabel(badgeLabelGenerator.generateStockQuantityBadge(userHoldingStock.getQuantity()));
    }
}
