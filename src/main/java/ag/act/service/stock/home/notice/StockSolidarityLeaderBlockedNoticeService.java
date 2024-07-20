package ag.act.service.stock.home.notice;

import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.StockNotice;
import ag.act.model.StockNoticeResponse;
import ag.act.service.stockboardgrouppost.BlockedUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class StockSolidarityLeaderBlockedNoticeService implements StockNoticeService {

    private final BlockedUserService blockedUserService;

    @Override
    public List<StockNoticeResponse> getNotice(Stock stock, User user) {
        if (isStockSolidarityLeaderBlocked(stock, user)) {
            return List.of(makeStockNoticeResponse(StockNotice.BLOCK_SOLIDARITY_LEADER));
        }

        return Collections.emptyList();
    }

    private boolean isStockSolidarityLeaderBlocked(Stock stock, User user) {
        return Optional.ofNullable(stock.getSolidarity().getSolidarityLeader())
            .map(solidarityLeader -> blockedUserService.isBlockedUser(user.getId(), solidarityLeader.getUserId()))
            .orElse(false);
    }
}
