package ag.act.module.krx;

import ag.act.dto.krx.StkItemPriceDto;
import ag.act.dto.krx.StockItemDto;
import ag.act.entity.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class KrxStockAggregatorTest {

    @InjectMocks
    private KrxStockAggregator aggregator;
    @Mock
    private KrxService krxService;
    @Mock
    private KrxStockConverter krxStockConverter;
    @Mock
    private StockItemDto stockItemDto1;
    @Mock
    private StockItemDto stockItemDto2;
    @Mock
    private StkItemPriceDto stkItemPriceDto1;
    @Mock
    private StkItemPriceDto stkItemPriceDto2;
    @Mock
    private Stock stock1;
    @Mock
    private Stock stock2;
    private String date;
    private List<Stock> stockList;

    @BeforeEach
    void setUp() {
        date = someString(5);
        stockList = List.of(stock1, stock2);

        given(krxService.getAllIsuBasicInfos(date)).willReturn(List.of(stockItemDto1, stockItemDto2));
        given(krxService.getAllIsuDailyInfos(date)).willReturn(List.of(stkItemPriceDto1, stkItemPriceDto2));
        given(stkItemPriceDto1.getISU_CD()).willReturn(someString(7));
        given(stkItemPriceDto2.getISU_CD()).willReturn(someString(11));
        given(krxStockConverter.convert(anyMap(), eq(stockItemDto1))).willReturn(stock1);
        given(krxStockConverter.convert(anyMap(), eq(stockItemDto2))).willReturn(stock2);
    }

    @Test
    void shouldReturnStockList() {
        // When
        final List<Stock> actual = aggregator.getStocksFromKrxService(date);

        // Then
        assertThat(actual, is(stockList));
    }
}
