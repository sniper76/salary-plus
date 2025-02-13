package ag.act.sp.repository;

import ag.act.sp.entity.PricePlan;
import ag.act.sp.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PricePlanRepository extends JpaRepository<PricePlan, Long> {
}
