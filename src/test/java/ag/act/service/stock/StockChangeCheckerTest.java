package ag.act.service.stock;

import ag.act.entity.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.BDDMockito.then;
import static shiver.me.timbers.data.random.RandomDoubles.someDoubleBetween;
import static shiver.me.timbers.data.random.RandomLongs.someLongBetween;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

@MockitoSettings(strictness = Strictness.LENIENT)
class StockChangeCheckerTest {
    @InjectMocks
    private StockChangeChecker stockChangeChecker;
    @Mock
    private StockChangeNotifier stockChangeNotifier;
    private Stock newStock;
    private Stock originalStock;
    private String stockName;
    private String stockCode;
    private Long newStockTotalIssuedQuantity;
    private long originalStockTotalIssuedQuantity;

    @Nested
    class WhenStockChangeIsOverThreshold {

        @BeforeEach
        void setUp() {
            stockName = someAlphanumericString(20);
            stockCode = someAlphanumericString(30);
            newStockTotalIssuedQuantity = someLongBetween(1000L, 10000L);
            originalStockTotalIssuedQuantity = Double.valueOf(newStockTotalIssuedQuantity * someDoubleBetween(0.1, 0.2))
                .longValue();

            newStock = new Stock();
            newStock.setTotalIssuedQuantity(newStockTotalIssuedQuantity);

            originalStock = new Stock();
            originalStock.setTotalIssuedQuantity(originalStockTotalIssuedQuantity);
            originalStock.setName(stockName);
            originalStock.setCode(stockCode);

            stockChangeChecker.checkStockTotalIssuedQuantity(newStock, originalStock);
        }

        @Test
        void shouldCallStockChangeNotifier() {
            then(stockChangeNotifier).should().notify(
                stockName,
                stockCode,
                originalStockTotalIssuedQuantity,
                newStockTotalIssuedQuantity
            );
        }
    }

    @Nested
    class WhenStockChangeIsNotOverThreshold {

        @BeforeEach
        void setUp() {
            newStockTotalIssuedQuantity = someLongBetween(1000L, 10000L);
            originalStockTotalIssuedQuantity = Double.valueOf(newStockTotalIssuedQuantity * someDoubleBetween(0.91, 1.0))
                .longValue();

            newStock = new Stock();
            newStock.setTotalIssuedQuantity(newStockTotalIssuedQuantity);

            originalStock = new Stock();
            originalStock.setTotalIssuedQuantity(originalStockTotalIssuedQuantity);

            stockChangeChecker.checkStockTotalIssuedQuantity(newStock, originalStock);
        }

        @Test
        void shouldNotCallStockChangeNotifier() {
            then(stockChangeNotifier).shouldHaveNoInteractions();
        }
    }
}
