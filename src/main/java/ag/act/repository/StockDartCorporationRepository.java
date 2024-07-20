package ag.act.repository;

import ag.act.entity.StockDartCorporation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockDartCorporationRepository extends JpaRepository<StockDartCorporation, Long> {

    Optional<StockDartCorporation> findByCorpCode(String corpCode);

    @Query(value = """
        SELECT sdc
        FROM StockDartCorporation sdc
        INNER JOIN Stock s
            ON s.code = sdc.stockCode
        """
    )
    List<StockDartCorporation> getAllDartCorporationsWithStock();
}
