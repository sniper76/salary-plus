package ag.act.repository.admin;

import ag.act.dto.admin.DashboardStatisticsCountDto;
import ag.act.entity.admin.DashboardStockStatistics;
import ag.act.enums.admin.DashboardStatisticsType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DashboardStockStatisticsRepository extends JpaRepository<DashboardStockStatistics, Long> {
    Optional<DashboardStockStatistics> findByTypeAndDateAndStockCode(DashboardStatisticsType type, String date, String stockCode);

    List<DashboardStockStatistics> findByTypeAndStockCodeOrderByDateDesc(DashboardStatisticsType type, String stockCode);

    @Query("""
            select new ag.act.dto.admin.DashboardStatisticsCountDto(type, date, sum(value))
            from DashboardStockStatistics 
            where type in :typeList
              and date between :startDate and :endDate
            group by type, date
            order by type, date desc
        """)
    List<DashboardStatisticsCountDto> findAllDailyByTypeInAndDateBetweenOrderByDate(
        List<DashboardStatisticsType> typeList, String startDate, String endDate);

    @Query("""
            select new ag.act.dto.admin.DashboardStatisticsCountDto(type, date, value)
            from DashboardStockStatistics 
            where type in :typeList
              and date between :startDate and :endDate
              and stockCode = :stockCode
            order by type, date desc
        """)
    List<DashboardStatisticsCountDto> findAllDailyStockByTypeInAndDateBetweenOrderByDate(
        List<DashboardStatisticsType> typeList, String startDate, String endDate, String stockCode);

    @Query(value = """
        select new ag.act.dto.admin.DashboardStatisticsCountDto(type, SUBSTRING(date, 1, 7), SUM(value))
        from DashboardStockStatistics 
        where type in :typeList
          and date between :startDate and :endDate
        group by type, SUBSTRING(date, 1, 7)
        order by type, SUBSTRING(date, 1, 7) desc
        """)
    List<DashboardStatisticsCountDto> findAllMonthlyByTypeInAndDateBetweenOrderByDate(
        List<DashboardStatisticsType> typeList, String startDate, String endDate);

    @Query(value = """
        select new ag.act.dto.admin.DashboardStatisticsCountDto(type, SUBSTRING(date, 1, 7), SUM(value))
        from DashboardStockStatistics 
        where type in :typeList
          and date between :startDate and :endDate
              and stockCode = :stockCode
        group by type, SUBSTRING(date, 1, 7)
        order by type, SUBSTRING(date, 1, 7) desc
        """)
    List<DashboardStatisticsCountDto> findAllMonthlyStockByTypeInAndDateBetweenOrderByDate(
        List<DashboardStatisticsType> typeList, String startDate, String endDate, String stockCode);
}
