package ag.act.facade.stock.home;

import ag.act.configuration.security.ActUserProvider;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.model.StockNoticeResponse;
import ag.act.service.stock.StockService;
import ag.act.service.stock.home.notice.StockNoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class StockHomeNoticeFacade {

    private final List<StockNoticeService> stockNoticeServices;
    private final StockService stockService;

    public List<StockNoticeResponse> getNotices(String stockCode) {
        final User user = ActUserProvider.getNoneNull();
        Stock stock = getStock(stockCode);

        return stockNoticeServices.stream()
            .map(stockNoticeService -> stockNoticeService.getNotice(stock, user))
            .flatMap(List::stream)
            .toList();
    }

    private Stock getStock(String stockCode) {
        return stockService.getStock(stockCode);
    }
}
