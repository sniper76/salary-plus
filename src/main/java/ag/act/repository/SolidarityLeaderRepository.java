package ag.act.repository;

import ag.act.entity.SolidarityLeader;
import ag.act.repository.interfaces.SimpleStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SolidarityLeaderRepository extends JpaRepository<SolidarityLeader, Long> {

    boolean existsByUserId(Long userId);

    Optional<SolidarityLeader> findBySolidarityId(Long solidarityId);

    @Query(value = """
        SELECT s.code as code, s.name as name, s.standard_code as standardCode
          FROM stocks s
          INNER JOIN solidarities sol ON s.code = sol.stock_code
          INNER JOIN solidarity_leaders sl ON sol.id = sl.solidarity_id
          WHERE sl.user_id = :userId
            AND s.status = 'ACTIVE'
        """, nativeQuery = true)
    List<SimpleStock> findAllLeadingSimpleStocks(Long userId);

    @Query(value = """
        SELECT s.name
          FROM stocks s
          INNER JOIN solidarities sol ON s.code = sol.stock_code
          INNER JOIN solidarity_leaders sl ON sol.id = sl.solidarity_id
          WHERE sl.user_id = :userId
            AND s.status = 'ACTIVE'
        """, nativeQuery = true)
    List<String> findAllLeadingStockNames(Long userId);
}
