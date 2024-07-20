package ag.act.repository.admin;

import ag.act.entity.StockAcceptorUserHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockAcceptorUserHistoryRepository extends JpaRepository<StockAcceptorUserHistory, Long> {
    List<StockAcceptorUserHistory> findAllByStockCodeAndUserId(String stockCode, Long userId);

    Optional<StockAcceptorUserHistory> findFirstByStockCodeAndUserIdOrderByCreatedAtDesc(String stockCode, Long userId);
}
