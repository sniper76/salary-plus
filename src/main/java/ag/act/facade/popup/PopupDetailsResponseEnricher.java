package ag.act.facade.popup;

import ag.act.entity.Popup;
import ag.act.model.PopupDetailsResponse;
import ag.act.service.stock.StockGroupService;
import ag.act.service.stock.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PopupDetailsResponseEnricher {
    private final StockService stockService;
    private final StockGroupService stockGroupService;

    public PopupDetailsResponse enrichStockInfo(Popup popup, PopupDetailsResponse popupDetailsResponse) {
        if (popup == null) {
            return null;
        }

        popupDetailsResponse.setStockName(getStockName(popup.getStockCode()));
        popupDetailsResponse.setStockGroupName(getStockGroupName(popup.getStockGroupId()));

        return popupDetailsResponse;
    }

    private String getStockName(String stockCode) {
        if (stockCode == null) {
            return null;
        }
        return stockService.getStock(stockCode).getName();
    }

    private String getStockGroupName(Long stockGroupId) {
        if (stockGroupId == null) {
            return null;
        }
        return stockGroupService.findByIdNoneNull(stockGroupId).getName();
    }
}
