package ag.act.service.post;

import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.UnreadPostStatusConverter;
import ag.act.converter.UnreadStockBoardGroupPostStatusConverter;
import ag.act.core.configuration.GlobalBoardManager;
import ag.act.entity.Stock;
import ag.act.enums.BoardGroup;
import ag.act.model.UnreadPostStatus;
import ag.act.model.UnreadStockBoardGroupPostStatusResponse;
import ag.act.repository.LatestUserPostsViewQueryDslRepository;
import ag.act.service.stock.StockService;
import ag.act.service.user.UserHoldingStockOnReferenceDateService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UnreadPostService {
    private final UnreadPostStatusConverter unreadPostStatusConverter;
    private final UnreadStockBoardGroupPostStatusConverter unreadStockBoardGroupPostStatusConverter;
    private final LatestUserPostsViewQueryDslRepository latestUserPostsViewQueryDslRepository;
    private final UserHoldingStockOnReferenceDateService userHoldingStockOnReferenceDateService;
    private final StockService stockService;
    private final GlobalBoardManager globalBoardManager;

    public UnreadPostStatus getUnreadPostStatus(Long userId) {
        return unreadPostStatusConverter.convert(
            getUnreadGlobalBoard(userId),
            getUnreadGlobalCommunity(userId),
            getUnreadGlobalEvent(userId),
            getUnreadDigitalDelegation(userId),
            getUnreadStocks(userId)
        );
    }

    public UnreadStockBoardGroupPostStatusResponse getUnreadStockBoardGroupPostStatusResponse(String stockCode) {
        final Long userId = ActUserProvider.getNoneNull().getId();

        return unreadStockBoardGroupPostStatusConverter.convert(
            getUnreadAnalysis(userId, stockCode),
            getUnreadAction(userId, stockCode),
            getUnreadDebate(userId, stockCode)
        );
    }

    private Boolean getUnreadGlobalBoard(Long userId) {
        List<String> stockCodes = List.of(globalBoardManager.getStockCode());

        return latestUserPostsViewQueryDslRepository.existsUnreadPost(
            stockCodes,
            userId,
            BoardGroup.GLOBALBOARD
        );
    }

    private Boolean getUnreadGlobalCommunity(Long userId) {
        List<String> stockCodes = List.of(globalBoardManager.getStockCode());

        return latestUserPostsViewQueryDslRepository.existsUnreadPost(
            stockCodes,
            userId,
            BoardGroup.GLOBALCOMMUNITY
        );
    }

    private Boolean getUnreadDigitalDelegation(Long userId) {
        return userHoldingStockOnReferenceDateService.countByPostUserViewDigitalDelegateForReferenceDate(userId) > 0;
    }

    private Boolean getUnreadGlobalEvent(Long userId) {
        List<String> stockCodes = List.of(globalBoardManager.getStockCode());

        return latestUserPostsViewQueryDslRepository.existsUnreadPost(
            stockCodes,
            userId,
            BoardGroup.GLOBALEVENT
        );
    }

    private List<Stock> getUnreadStocks(Long userId) {
        return stockService.findAllWithUnreadPosts(userId);
    }

    private Boolean getUnreadAnalysis(Long userId, String stockCode) {
        return latestUserPostsViewQueryDslRepository.existsUnreadPost(
            List.of(stockCode),
            userId,
            BoardGroup.ANALYSIS
        );
    }

    private Boolean getUnreadAction(Long userId, String stockCode) {
        return latestUserPostsViewQueryDslRepository.existsUnreadPost(
            List.of(stockCode),
            userId,
            BoardGroup.ACTION
        );
    }

    private Boolean getUnreadDebate(Long userId, String stockCode) {
        return latestUserPostsViewQueryDslRepository.existsUnreadPost(
            List.of(stockCode),
            userId,
            BoardGroup.DEBATE
        );
    }
}
