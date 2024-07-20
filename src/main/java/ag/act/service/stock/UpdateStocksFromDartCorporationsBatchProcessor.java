package ag.act.service.stock;

import ag.act.entity.Stock;
import ag.act.entity.StockDartCorporation;
import ag.act.service.IBatchProcessor;
import ag.act.service.dart.StockDartCorporationService;
import ag.act.util.ChunkUtil;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Component
@Transactional
public class UpdateStocksFromDartCorporationsBatchProcessor implements IBatchProcessor {
    private static final String EMPTY = "";
    private final ChunkUtil chunkUtil;
    private final StockService stockService;
    private final StockDartCorporationService stockDartCorporationService;
    private final EntityManager entityManager;

    public List<StockDartCorporation> getSourceStockDartCorporations() {
        return stockDartCorporationService.getAllDartCorporationsWithStock();
    }

    public void updateStocks(
        List<StockDartCorporation> stockDartCorporations,
        BatchProcessorParameters batchProcessorParameters
    ) {
        final Consumer<Integer> batchCountLog = batchProcessorParameters.batchCountLog();
        final AtomicInteger updateCount = batchProcessorParameters.updateCount();
        final int chunkSize = batchProcessorParameters.chunkSize();

        chunkStocks(stockDartCorporations, chunkSize)
            .forEach(chunkStockDartCorporations -> {
                final List<Stock> stocksInDatabase = getStocksInDatabase(chunkStockDartCorporations);

                stocksInDatabase.forEach(stock -> {
                    setStocksFromDartCorporations(chunkStockDartCorporations, stock);
                    updateStock(stock);
                    updateCount.incrementAndGet();
                    batchCountLog.accept(updateCount.get());
                });
            });
    }

    private void setStocksFromDartCorporations(List<StockDartCorporation> chunkStockDartCorporations, Stock stock) {
        chunkStockDartCorporations.stream()
            .filter(corporation -> corporation.getStockCode().equals(stock.getCode()))
            .findFirst()
            .ifPresent(corporation -> {
                stock.setRepresentativePhoneNumber(Optional.ofNullable(corporation.getRepresentativePhoneNumber()).orElse(EMPTY));
                stock.setAddress(Optional.ofNullable(corporation.getAddress()).orElse(EMPTY));
            });
    }

    private List<List<StockDartCorporation>> chunkStocks(List<StockDartCorporation> stockDartCorporations, int chunkSize) {
        return chunkUtil.getChunks(stockDartCorporations, chunkSize);
    }

    private List<Stock> getStocksInDatabase(List<StockDartCorporation> sourceStocks) {
        return stockService.findAllByCodes(
            sourceStocks.stream()
                .map(StockDartCorporation::getStockCode)
                .toList()
        );
    }

    private void updateStock(Stock stock) {
        entityManager.persist(stock);
    }
}
