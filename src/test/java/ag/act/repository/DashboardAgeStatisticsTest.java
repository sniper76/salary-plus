package ag.act.repository;

import ag.act.SpringDataJpaTest;
import ag.act.entity.admin.DashboardAgeStatistics;
import ag.act.enums.admin.DashboardStatisticsType;
import ag.act.repository.admin.DashboardAgeStatisticsRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Disabled
@SpringDataJpaTest
public class DashboardAgeStatisticsTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DashboardAgeStatisticsRepository dashboardAgeStatisticsRepository;

    @Test
    public void shouldBeSuccess() {
        final DashboardAgeStatistics dashboardAgeStatistics = new DashboardAgeStatistics();
        dashboardAgeStatistics.setDate("2023-10-10");
        dashboardAgeStatistics.setType(DashboardStatisticsType.DAILY_ACTIVE_USER);
        dashboardAgeStatistics.setAge10Value(1L);
        dashboardAgeStatistics.setAge20Value(1L);
        dashboardAgeStatistics.setAge30Value(1L);
        dashboardAgeStatistics.setAge40Value(1L);
        dashboardAgeStatistics.setAge50Value(1L);
        dashboardAgeStatistics.setAge60Value(1L);
        dashboardAgeStatistics.setAge70Value(1L);
        dashboardAgeStatistics.setAge80Value(1L);
        dashboardAgeStatistics.setAge90Value(1L);
        entityManager.persist(dashboardAgeStatistics);

        entityManager.flush();

        List<DashboardAgeStatistics> dashboardAgeStatisticsList = dashboardAgeStatisticsRepository.findAll();

        assertThat(dashboardAgeStatisticsList.isEmpty(), is(false));
    }
}

