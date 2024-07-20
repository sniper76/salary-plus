package ag.act.repository.campaign;

import ag.act.entity.campaign.CampaignDownload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CampaignDownloadRepository extends JpaRepository<CampaignDownload, Long> {

    List<CampaignDownload> findAllByCampaignId(Long campaignId);

    Optional<CampaignDownload> findFirstByZipFileKey(String zipFileKey);
}
