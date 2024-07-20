package ag.act.repository.admin;

import ag.act.dto.admin.DashboardStatisticsGenderCountDto;
import ag.act.entity.admin.DashboardGenderStatistics;
import ag.act.enums.admin.DashboardStatisticsType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DashboardGenderStatisticsRepository extends JpaRepository<DashboardGenderStatistics, Long> {
    Optional<DashboardGenderStatistics> findByTypeAndDate(DashboardStatisticsType type, String date);

    @Query(value = """
        select new ag.act.dto.admin.DashboardStatisticsGenderCountDto(type, date, maleValue, femaleValue)
        from DashboardGenderStatistics 
        where type = 'DAILY_USER_GENDER_COUNT'
          and date in :dateList
        order by date desc 
        """)
    List<DashboardStatisticsGenderCountDto> findByTypeAndInDate(List<String> dateList);
}
