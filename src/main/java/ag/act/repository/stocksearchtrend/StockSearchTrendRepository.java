package ag.act.repository.stocksearchtrend;

import ag.act.entity.StockSearchTrend;
import ag.act.repository.interfaces.SimpleStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockSearchTrendRepository extends JpaRepository<StockSearchTrend, Long> {

    List<StockSearchTrend> findAllByStockCodeAndUserId(String stockCode, Long userId);

    @Query(value = """
        SELECT sst.stock_code AS code, s.name, s.standard_code as standardCode
        FROM stock_search_trends sst
            INNER JOIN stocks s
            ON s.code = sst.stock_code
            AND s.status = 'ACTIVE'
        WHERE sst.created_at >= :searchStartTime
        GROUP BY sst.stock_code, s.name, s.standard_code
        ORDER BY count(1) DESC
        LIMIT 5
        """, nativeQuery = true)
    List<SimpleStock> findTop5TrendsFromGivenTime(LocalDateTime searchStartTime);
}
