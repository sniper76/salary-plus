package ag.act.repository;

import ag.act.SpringDataJpaTest;
import ag.act.entity.admin.DashboardStockStatistics;
import ag.act.enums.admin.DashboardStatisticsType;
import ag.act.repository.admin.DashboardStockStatisticsRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Disabled
@SpringDataJpaTest
public class DashboardStockStatisticsTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DashboardStockStatisticsRepository dashboardStockStatisticsRepository;

    @Test
    public void shouldBeSuccess() {
        final DashboardStockStatistics dashboardStockStatistics = new DashboardStockStatistics();
        dashboardStockStatistics.setStockCode(someStockCode());
        dashboardStockStatistics.setDate("2023-10-10");
        dashboardStockStatistics.setType(DashboardStatisticsType.DAILY_ACTIVE_USER);
        dashboardStockStatistics.setValue(1.0);
        entityManager.persist(dashboardStockStatistics);

        entityManager.flush();

        List<DashboardStockStatistics> dashboardStatisticsList = dashboardStockStatisticsRepository.findAll();

        assertThat(dashboardStatisticsList.isEmpty(), is(false));
    }
}

