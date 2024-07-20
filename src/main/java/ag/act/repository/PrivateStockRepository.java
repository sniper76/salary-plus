package ag.act.repository;

import ag.act.entity.PrivateStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrivateStockRepository extends JpaRepository<PrivateStock, Long> {
    Optional<PrivateStock> findByCode(String stockCode);

    List<PrivateStock> findAllByStatus(ag.act.model.Status status);
}
