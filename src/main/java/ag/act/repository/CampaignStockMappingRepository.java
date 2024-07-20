package ag.act.repository;

import ag.act.entity.CampaignStockMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignStockMappingRepository extends JpaRepository<CampaignStockMapping, Long> {
    List<CampaignStockMapping> findAllByCampaignId(Long campaignId);

    Long countByCampaignId(Long campaignId);
}
