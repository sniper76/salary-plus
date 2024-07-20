package ag.act.repository;

import ag.act.SpringDataJpaTest;
import ag.act.entity.admin.DashboardStatistics;
import ag.act.enums.admin.DashboardStatisticsType;
import ag.act.repository.admin.DashboardStatisticsRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Disabled
@SpringDataJpaTest
public class DashboardStatisticsTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DashboardStatisticsRepository dashboardStatisticsRepository;

    @Test
    public void shouldBeSuccess() {
        final DashboardStatistics dashboardStatistics = new DashboardStatistics();
        dashboardStatistics.setDate("2023-10-10");
        dashboardStatistics.setType(DashboardStatisticsType.DAILY_ACTIVE_USER);
        dashboardStatistics.setValue(1.0);
        entityManager.persist(dashboardStatistics);

        entityManager.flush();

        List<DashboardStatistics> dashboardStatisticsList = dashboardStatisticsRepository.findAll();

        assertThat(dashboardStatisticsList.isEmpty(), is(false));
    }
}

