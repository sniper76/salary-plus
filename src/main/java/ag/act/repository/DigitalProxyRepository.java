package ag.act.repository;

import ag.act.entity.DigitalProxy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface DigitalProxyRepository extends JpaRepository<DigitalProxy, Long> {
    @Query("""
        SELECT DISTINCT s.code FROM DigitalProxy dp
        JOIN dp.post p
        LEFT JOIN p.board b
        LEFT JOIN b.stock s
        WHERE s.code in :stockCodes
        AND dp.targetStartDate <= current_timestamp
        AND dp.targetEndDate >= current_timestamp
        AND p.status = 'ACTIVE'
        """)
    Set<String> findAllInProgressByStockCodeIn(List<String> stockCodes);
}
