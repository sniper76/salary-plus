package ag.act.module.krx;

import ag.act.entity.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomLongs.somePositiveLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class KrxStockMapperTest {
    @InjectMocks
    private KrxStockMapper krxStockMapper;

    @Mock
    private Stock stockFromKrx;
    @Mock
    private Stock stock;
    private Integer closingPrice;
    private String stockType;
    private String marketType;
    private String fullName;
    private String name;
    private String standardCode;
    private Stock actualStock;
    private Long totalIssuedQuantity;

    @BeforeEach
    void setUp() {
        stockType = someString(15);
        closingPrice = someIntegerBetween(100, 1000000);
        totalIssuedQuantity = somePositiveLong();
        marketType = someString(13);
        fullName = someString(11);
        name = someString(9);
        standardCode = someString(7);

        given(stockFromKrx.getStandardCode()).willReturn(standardCode);
        given(stockFromKrx.getName()).willReturn(name);
        given(stockFromKrx.getFullName()).willReturn(fullName);
        given(stockFromKrx.getMarketType()).willReturn(marketType);
        given(stockFromKrx.getStockType()).willReturn(stockType);
        given(stockFromKrx.getClosingPrice()).willReturn(closingPrice);
        given(stockFromKrx.getTotalIssuedQuantity()).willReturn(totalIssuedQuantity);

        actualStock = krxStockMapper.mergeStocks(stockFromKrx, stock);
    }

    @Test
    void shouldSetStandardCode() {
        then(actualStock).should().setStandardCode(standardCode);
    }

    @Test
    void shouldSetName() {
        then(actualStock).should().setName(name);
    }

    @Test
    void shouldSetFullName() {
        then(actualStock).should().setFullName(fullName);
    }

    @Test
    void shouldSetMarketType() {
        then(actualStock).should().setMarketType(marketType);
    }

    @Test
    void shouldSetStockType() {
        then(actualStock).should().setStockType(stockType);
    }

    @Test
    void shouldSetClosingPrice() {
        then(actualStock).should().setClosingPrice(closingPrice);
    }

    @Test
    void shouldSetTotalIssuedQuantity() {
        then(actualStock).should().setTotalIssuedQuantity(totalIssuedQuantity);
    }
}
