package ag.act.repository;

import ag.act.entity.StockGroupMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockGroupMappingRepository extends JpaRepository<StockGroupMapping, Long> {

    List<StockGroupMapping> findAllByStockGroupId(Long stockGroupId);

    Long countByStockGroupId(Long stockGroupId);

}
