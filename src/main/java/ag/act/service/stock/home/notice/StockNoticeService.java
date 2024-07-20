package ag.act.service.stock.home.notice;

import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.StockNotice;
import ag.act.model.StockNoticeResponse;

import java.util.List;

public interface StockNoticeService {

    List<StockNoticeResponse> getNotice(Stock stock, User user);

    default StockNoticeResponse makeStockNoticeResponse(StockNotice stockNotice) {
        return new StockNoticeResponse()
            .noticeLevel(stockNotice.getNoticeLevel())
            .message(stockNotice.getMessage());
    }
}
