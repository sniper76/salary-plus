package ag.act.repository.admin;

import ag.act.dto.admin.DashboardStatisticsCountDto;
import ag.act.entity.admin.DashboardStatistics;
import ag.act.enums.admin.DashboardStatisticsType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DashboardStatisticsRepository extends JpaRepository<DashboardStatistics, Long> {
    Optional<DashboardStatistics> findByTypeAndDate(DashboardStatisticsType type, String date);

    @Query("""
            select new ag.act.dto.admin.DashboardStatisticsCountDto(type, date, value)
            from DashboardStatistics 
            where type in :typeList
              and date between :startDate and :endDate
            order by type, date desc
        """)
    List<DashboardStatisticsCountDto> findAllDailyByTypeInAndDateBetweenOrderByDate(
        List<DashboardStatisticsType> typeList, String startDate, String endDate);

    @Query("""
            select new ag.act.dto.admin.DashboardStatisticsCountDto(type, date, value)
            from DashboardStatistics 
            where type in :typeList
              and date in :dateList
            order by type, date desc
        """)
    List<DashboardStatisticsCountDto> findAllDailyByTypeInAndDateInOrderByDate(
        List<DashboardStatisticsType> typeList, List<String> dateList);

    @Query(value = """
        select new ag.act.dto.admin.DashboardStatisticsCountDto(type, SUBSTRING(date, 1, 7), SUM(value))
        from DashboardStatistics 
        where type in :typeList
          and date between :startDate and :endDate
        group by type, SUBSTRING(date, 1, 7)
        order by type, SUBSTRING(date, 1, 7) desc
        """)
    List<DashboardStatisticsCountDto> findAllMonthlyByTypeInAndDateBetweenOrderByDate(
        List<DashboardStatisticsType> typeList, String startDate, String endDate);
}
