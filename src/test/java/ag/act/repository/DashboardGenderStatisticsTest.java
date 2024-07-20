package ag.act.repository;

import ag.act.SpringDataJpaTest;
import ag.act.entity.admin.DashboardGenderStatistics;
import ag.act.enums.admin.DashboardStatisticsType;
import ag.act.repository.admin.DashboardGenderStatisticsRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Disabled
@SpringDataJpaTest
public class DashboardGenderStatisticsTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DashboardGenderStatisticsRepository dashboardGenderStatisticsRepository;

    @Test
    public void shouldBeSuccess() {
        final DashboardGenderStatistics dashboardGenderStatistics = new DashboardGenderStatistics();
        dashboardGenderStatistics.setDate("2023-10-10");
        dashboardGenderStatistics.setType(DashboardStatisticsType.DAILY_ACTIVE_USER);
        dashboardGenderStatistics.setMaleValue(1L);
        dashboardGenderStatistics.setFemaleValue(1L);
        entityManager.persist(dashboardGenderStatistics);

        entityManager.flush();

        List<DashboardGenderStatistics> dashboardGenderStatisticsList = dashboardGenderStatisticsRepository.findAll();

        assertThat(dashboardGenderStatisticsList.isEmpty(), is(false));
    }
}

