package ag.act.repository;

import ag.act.entity.TestStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TestStockRepository extends JpaRepository<TestStock, Long> {
    Optional<TestStock> findByCode(String stockCode);
}
