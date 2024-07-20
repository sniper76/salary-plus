package ag.act.repository;

import ag.act.entity.SolidarityDailySummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolidarityDailySummaryRepository extends JpaRepository<SolidarityDailySummary, Long> {
}
