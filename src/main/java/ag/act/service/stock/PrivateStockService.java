package ag.act.service.stock;

import ag.act.converter.stock.PrivateStockConverter;
import ag.act.entity.PrivateStock;
import ag.act.entity.Stock;
import ag.act.model.CreatePrivateStockRequest;
import ag.act.model.Status;
import ag.act.repository.PrivateStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PrivateStockService {
    private final PrivateStockRepository privateStockRepository;
    private final PrivateStockConverter privateStockConverter;

    public List<Stock> getActiveStocks() {
        return getActivePrivateStocks().stream().map(privateStockConverter::convert).toList();
    }

    private List<PrivateStock> getActivePrivateStocks() {
        return privateStockRepository.findAllByStatus(Status.ACTIVE);
    }

    public Optional<PrivateStock> findByCode(String stockCode) {
        return privateStockRepository.findByCode(stockCode);
    }

    public PrivateStock save(CreatePrivateStockRequest request) {
        final PrivateStock privateStock = privateStockConverter.convert(request);
        return privateStockRepository.save(privateStock);
    }
}
