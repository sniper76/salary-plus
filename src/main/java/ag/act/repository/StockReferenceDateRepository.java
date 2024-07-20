package ag.act.repository;

import ag.act.entity.StockReferenceDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockReferenceDateRepository extends JpaRepository<StockReferenceDate, Long> {

    List<StockReferenceDate> findAllByStockCodeAndReferenceDateBetween(String stockCode, LocalDate startDate, LocalDate endDate);

    List<StockReferenceDate> findAllByStockCodeInAndReferenceDateBetween(List<String> stockCodes, LocalDate startDate, LocalDate endDate);

    Optional<StockReferenceDate> findByStockCodeAndReferenceDate(String stockCode, LocalDate referenceDate);

    List<StockReferenceDate> findAllByStockCode(String stockCode);

    List<StockReferenceDate> findAllByStockCodeAndReferenceDate(String stockCode, LocalDate referenceDate);
}
