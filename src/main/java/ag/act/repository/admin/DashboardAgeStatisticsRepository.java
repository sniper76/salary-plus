package ag.act.repository.admin;

import ag.act.dto.admin.DashboardStatisticsAgeCountDto;
import ag.act.entity.admin.DashboardAgeStatistics;
import ag.act.enums.admin.DashboardStatisticsType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DashboardAgeStatisticsRepository extends JpaRepository<DashboardAgeStatistics, Long> {
    Optional<DashboardAgeStatistics> findByTypeAndDate(DashboardStatisticsType type, String date);

    @Query(value = """
        select new ag.act.dto.admin.DashboardStatisticsAgeCountDto(
        type, date, age10Value, age20Value, age30Value, age40Value, age50Value, age60Value, age70Value, age80Value, age90Value
        )
        from DashboardAgeStatistics 
        where type = 'DAILY_USER_AGE_COUNT'
          and date in :dateList
        order by date desc
        """)
    List<DashboardStatisticsAgeCountDto> findByTypeAndInDate(List<String> dateList);
}
