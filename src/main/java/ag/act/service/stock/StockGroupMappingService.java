package ag.act.service.stock;

import ag.act.entity.StockGroupMapping;
import ag.act.repository.StockGroupMappingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockGroupMappingService {
    private final StockGroupMappingRepository stockGroupMappingRepository;

    public StockGroupMappingService(StockGroupMappingRepository stockGroupMappingRepository) {
        this.stockGroupMappingRepository = stockGroupMappingRepository;
    }

    public List<String> getAllStockCodes(Long stockGroupId) {
        return getAllByStockGroupId(stockGroupId)
            .stream()
            .map(StockGroupMapping::getStockCode)
            .toList();
    }

    public Long countAllByStockGroupId(Long stockGroupId) {
        return stockGroupMappingRepository.countByStockGroupId(stockGroupId);
    }

    private List<Long> findAllStockGroupMappingIds(Long stockGroupId) {
        return getAllByStockGroupId(stockGroupId)
            .stream()
            .map(StockGroupMapping::getId)
            .toList();
    }

    private List<StockGroupMapping> getAllByStockGroupId(Long stockGroupId) {
        return stockGroupMappingRepository.findAllByStockGroupId(stockGroupId);
    }

    public List<StockGroupMapping> createMappings(Long stockGroupId, List<String> stockCodes) {
        return stockGroupMappingRepository.saveAll(
            stockCodes.stream()
                .map(stockCode -> StockGroupMapping.builder()
                    .stockGroupId(stockGroupId)
                    .stockCode(stockCode)
                    .build()
                )
                .toList()
        );
    }

    public void deleteStockGroupMappings(Long stockGroupId) {
        stockGroupMappingRepository.deleteAllByIdInBatch(findAllStockGroupMappingIds(stockGroupId));
    }
}
