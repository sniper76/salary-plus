package ag.act.service.stocksearchtrend;

import ag.act.entity.StockSearchTrend;
import ag.act.repository.interfaces.SimpleStock;
import ag.act.repository.stocksearchtrend.StockSearchTrendRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional
public class StockSearchTrendService {
    private static final int ONE_HOUR = 1;
    private static final int ONE_DAY = 1;
    private final StockSearchTrendRepository stockSearchTrendRepository;

    public StockSearchTrend createStockSearchTrend(String stockCode, Long userId) {
        return stockSearchTrendRepository.save(StockSearchTrend.of(stockCode, userId));
    }

    public List<SimpleStock> getTop5SimpleStocksBasedOnTrendsWithinLastHour() {
        return getTopTrendsFromGivenTime(calculateOneHourAgo());
    }

    public List<SimpleStock> getTop5SimpleStocksBasedOnTrendsWithinLastDay() {
        return getTopTrendsFromGivenTime(calculateOneDayAgo());
    }

    private List<SimpleStock> getTopTrendsFromGivenTime(LocalDateTime searchStartTime) {
        return stockSearchTrendRepository.findTop5TrendsFromGivenTime(searchStartTime);
    }

    private LocalDateTime calculateOneHourAgo() {
        return LocalDateTime.now().minusHours(ONE_HOUR);
    }

    private LocalDateTime calculateOneDayAgo() {
        return LocalDateTime.now().minusDays(ONE_DAY);
    }
}
