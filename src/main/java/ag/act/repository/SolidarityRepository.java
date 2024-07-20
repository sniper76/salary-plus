package ag.act.repository;

import ag.act.entity.Solidarity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SolidarityRepository extends JpaRepository<Solidarity, Long> {
    Optional<Solidarity> findByStockCode(String stockCode);

    @Query("""
        select s
        from Solidarity s
        left join fetch s.mostRecentDailySummary
        left join fetch s.secondMostRecentDailySummary
        left join fetch s.stock
        left join fetch s.solidarityLeader
        """
    )
    List<Solidarity> getAllSolidarities();
}
