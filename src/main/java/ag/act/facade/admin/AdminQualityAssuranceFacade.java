package ag.act.facade.admin;

import ag.act.converter.stock.TestStockConverter;
import ag.act.entity.Stock;
import ag.act.entity.TestStock;
import ag.act.model.CreateOrUpdateTestStockRequest;
import ag.act.service.stock.StockService;
import ag.act.service.stock.TestStockService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class AdminQualityAssuranceFacade {
    private final StockService stockService;
    private final TestStockService testStockService;
    private final TestStockConverter testStockConverter;

    public AdminQualityAssuranceFacade(
        StockService stockService,
        TestStockService testStockService,
        TestStockConverter testStockConverter
    ) {
        this.stockService = stockService;
        this.testStockService = testStockService;
        this.testStockConverter = testStockConverter;
    }

    public void createOrUpdateTestStock(String stockCode, CreateOrUpdateTestStockRequest request) {
        testStockService.findByCode(stockCode)
            .map(testStock -> updateTestStock(testStock, request))
            .orElseGet(() -> createTestStock(stockCode, request));
    }

    private TestStock updateTestStock(TestStock testStock, CreateOrUpdateTestStockRequest request) {

        final Stock stock = testStockConverter.convert(testStock);
        testStock.setCode(stock.getCode());
        testStock.setName(stock.getName());
        testStock.setSolidarityLeader(request.getSolidarityLeader());
        testStock.setUsers(request.getUsers());

        return testStockService.save(testStock);
    }

    private TestStock createTestStock(String stockCode, CreateOrUpdateTestStockRequest request) {
        final TestStock testStock = new TestStock();
        testStock.setCode(stockCode);
        testStock.setName(stockCode);

        return updateTestStock(testStock, request);
    }

    public List<TestStock> getTestStocks() {
        return testStockService.getTestStocks();
    }

    public boolean syncSomeDataByTestStock(TestStock testStock) {
        if (stockService.findByCode(testStock.getCode()).isEmpty()) {
            return false;
        }

        testStockService.syncSolidarityLeader(testStock);
        testStockService.syncUserHoldingStock(testStock);
        testStockService.syncMyDataSummary(testStock);

        return true;
    }
}
