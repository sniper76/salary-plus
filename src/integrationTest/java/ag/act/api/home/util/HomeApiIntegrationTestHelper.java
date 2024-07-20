package ag.act.api.home.util;

import ag.act.entity.Solidarity;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.itutil.ITUtil;
import ag.act.model.Status;
import ag.act.util.KoreanDateTimeUtil;

public class HomeApiIntegrationTestHelper {
    private final ITUtil itUtil;

    public HomeApiIntegrationTestHelper(ITUtil itUtil) {
        this.itUtil = itUtil;
    }

    public void mockUserHoldingStockWithStockRanking(User user, int displayOrder, Status soliidarityStatus, int stakeRank) {
        final Stock stock = itUtil.createStock();
        itUtil.createStockRanking(stock.getCode(), KoreanDateTimeUtil.getYesterdayLocalDate(), stakeRank);
        mockUserHoldingStock(stock, user, displayOrder, soliidarityStatus);
    }

    public void mockUserHoldingStock(User user, int displayOrder, Status soliidarityStatus) {
        final Stock stock = itUtil.createStock();
        mockUserHoldingStock(stock, user, displayOrder, soliidarityStatus);
    }

    public void mockUserHoldingStock(User user, String stockCode, int displayOrder, Status soliidarityStatus) {
        final Stock stock = itUtil.createStock();
        stock.setCode(stockCode);
        itUtil.updateStock(stock);
        mockUserHoldingStock(stock, user, displayOrder, soliidarityStatus);
    }

    public void mockUserHoldingStock(User user, String stockCode, String stockName, int displayOrder, Status soliidarityStatus) {
        final Stock stock = itUtil.createStock();
        stock.setCode(stockCode);
        stock.setName(stockName);
        itUtil.updateStock(stock);
        mockUserHoldingStock(stock, user, displayOrder, soliidarityStatus);
    }

    private void mockUserHoldingStock(Stock stock, User user, int displayOrder, Status soliidarityStatus) {
        Solidarity solidarity = itUtil.createSolidarity(stock.getCode());
        solidarity.setStatus(soliidarityStatus);
        itUtil.updateSolidarity(solidarity);

        final UserHoldingStock userHoldingStock = itUtil.createUserHoldingStock(stock.getCode(), user);
        userHoldingStock.setDisplayOrder(displayOrder);
        itUtil.updateUserHoldingStock(userHoldingStock);
    }
}
