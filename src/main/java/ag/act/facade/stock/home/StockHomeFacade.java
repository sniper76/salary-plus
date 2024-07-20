package ag.act.facade.stock.home;

import ag.act.converter.stock.SimpleStockResponseConverter;
import ag.act.model.LeaderElectionFeatureActiveConditionResponse;
import ag.act.model.SimpleStockResponse;
import ag.act.model.StockHomeResponse;
import ag.act.service.post.UnreadPostService;
import ag.act.service.solidarity.SolidarityDashboardService;
import ag.act.service.stock.StockService;
import ag.act.service.stock.home.StockHomeSectionService;
import ag.act.service.stock.home.StockHomeSolidarityLeaderElectionFeatureActiveConditionService;
import ag.act.service.stock.home.StockHomeSolidarityLeaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StockHomeFacade {
    private final StockHomeSectionService stockHomeSectionService;
    private final StockHomeSolidarityLeaderService stockHomeSolidarityLeaderService;
    private final SolidarityDashboardService solidarityDashboardService;
    private final StockHomeNoticeFacade stockHomeNoticeFacade;
    private final StockHomeSolidarityLeaderElectionFeatureActiveConditionService stockHomeSolidarityLeaderElectionFeatureActiveConditionService;
    private final StockHomeSolidarityLeaderElectionFacade solidarityLeaderElectionFacade;
    private final StockService stockService;
    private final SimpleStockResponseConverter simpleStockResponseConverter;
    private final UnreadPostService unreadPostService;

    public StockHomeResponse getStockHome(String stockCode) {
        return new StockHomeResponse()
            .stock(getSimpleStockResponse(stockCode))
            .dashboard(solidarityDashboardService.getDashboard(stockCode))
            .leader(stockHomeSolidarityLeaderService.getStockHomeLeader(stockCode))
            .leaderApplication(solidarityLeaderElectionFacade.getLeaderApplication(stockCode))
            .sections(stockHomeSectionService.getSections(stockCode))
            .notices(stockHomeNoticeFacade.getNotices(stockCode))
            .leaderElectionDetail(solidarityLeaderElectionFacade.getLeaderElectionDetail(stockCode))
            .leaderElectionFeatureActiveCondition(getLeaderElectionFeatureActiveCondition(stockCode))
            .unreadStockBoardGroupPostStatus(unreadPostService.getUnreadStockBoardGroupPostStatusResponse(stockCode));
    }

    private SimpleStockResponse getSimpleStockResponse(String stockCode) {
        return simpleStockResponseConverter.convert(stockService.getSimpleStock(stockCode));
    }

    private LeaderElectionFeatureActiveConditionResponse getLeaderElectionFeatureActiveCondition(String stockCode) {
        return stockHomeSolidarityLeaderElectionFeatureActiveConditionService.getLeaderElectionFeatureActiveCondition(stockCode);
    }
}
