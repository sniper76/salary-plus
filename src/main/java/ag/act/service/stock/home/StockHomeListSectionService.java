package ag.act.service.stock.home;

import ag.act.model.StockHomeSectionResponse;

import java.util.List;

public interface StockHomeListSectionService {
    List<StockHomeSectionResponse> getSections(String stockCode);
}
