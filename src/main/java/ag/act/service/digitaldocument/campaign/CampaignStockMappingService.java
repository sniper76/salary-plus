package ag.act.service.digitaldocument.campaign;

import ag.act.entity.CampaignStockMapping;
import ag.act.repository.CampaignStockMappingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CampaignStockMappingService {
    private final CampaignStockMappingRepository campaignStockMappingRepository;

    public List<CampaignStockMapping> createMappings(Long campaignId, List<String> stockCodes) {
        return campaignStockMappingRepository.saveAll(
            toCampaignStockMappings(campaignId, stockCodes)
        );
    }

    @NotNull
    private List<CampaignStockMapping> toCampaignStockMappings(Long campaignId, List<String> stockCodes) {
        return stockCodes.stream()
            .map(stockCode -> CampaignStockMapping.of(campaignId, stockCode))
            .toList();
    }

    public Long countByCampaignId(Long campaignId) {
        return campaignStockMappingRepository.countByCampaignId(campaignId);
    }
}
