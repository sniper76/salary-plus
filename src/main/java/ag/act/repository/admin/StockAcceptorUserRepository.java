package ag.act.repository.admin;

import ag.act.entity.StockAcceptorUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockAcceptorUserRepository extends JpaRepository<StockAcceptorUser, Long> {
    Optional<StockAcceptorUser> findByStockCode(String stockCode);

    Optional<StockAcceptorUser> findByStockCodeAndUserId(String stockCode, Long userId);

    List<StockAcceptorUser> findAllByUserId(Long userId);
}
