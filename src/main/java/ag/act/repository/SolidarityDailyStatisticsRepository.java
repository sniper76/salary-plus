package ag.act.repository;

import ag.act.entity.SolidarityDailyStatistics;
import ag.act.repository.interfaces.StockStatisticsResultItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SolidarityDailyStatisticsRepository extends JpaRepository<SolidarityDailyStatistics, Long> {

    @Query("""
        SELECT TO_CHAR(sds.date, 'YYYY-MM-DD') AS date,
            sds.stake as stake,
            sds.stockCode as stockCode,
            sds.stockQuantity as stockQuantity,
            sds.marketValue as marketValue,
            sds.memberCount as memberCount
        FROM SolidarityDailyStatistics sds
        WHERE sds.stockCode = :code
        AND sds.date BETWEEN :periodFrom AND :periodTo
        ORDER BY sds.date ASC
        """
    )
    List<StockStatisticsResultItem> findDailyStatisticsBy(String code, LocalDate periodFrom, LocalDate periodTo);

    @Query("""
        SELECT TO_CHAR(sds.date, 'YYYY-MM') AS date,
            sds.stake as stake,
            sds.stockCode as stockCode,
            sds.stockQuantity as stockQuantity,
            sds.marketValue as marketValue,
            sds.memberCount as memberCount
        FROM SolidarityDailyStatistics sds
        WHERE sds.stockCode = :code
        AND sds.date in (
            SELECT max(sds.date) AS date
            FROM SolidarityDailyStatistics sds
            WHERE sds.stockCode = :code
            AND sds.date BETWEEN :periodFrom AND :periodTo
            GROUP BY DATE_TRUNC('month', sds.date)
        )
        ORDER BY sds.date ASC
        """
    )
    List<StockStatisticsResultItem> findMonthlyStatisticsBy(String code, LocalDate periodFrom, LocalDate periodTo);

    Optional<SolidarityDailyStatistics> findBySolidarityIdAndDate(Long solidarityId, LocalDate date);

    @Query(value = """
        SELECT COALESCE(SUM(u.marketValue), 0) 
        FROM SolidarityDailyStatistics u 
        WHERE u.date = :searchDate
        """)
    Double sumMarketValueAndDate(LocalDate searchDate);
}
