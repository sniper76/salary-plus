package ag.act.module.krx;

import ag.act.dto.krx.StkItemPriceDto;
import ag.act.dto.krx.StockItemDto;
import ag.act.entity.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Map;

import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static shiver.me.timbers.data.random.RandomLongs.someLongBetween;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class KrxStockConverterTest {

    @InjectMocks
    private KrxStockConverter krxStockConverter;

    @Mock
    private StockItemDto stockItemDto;
    @Mock
    private StkItemPriceDto stkItemPriceDto;
    private String code;
    private String standardCode;
    private String name;
    private String fullName;
    private String marketType;
    private String stockType;
    private Integer price;

    private Stock actualStock;
    private Map<String, StkItemPriceDto> priceMap;
    private Long totalIssuedQuantity;

    @BeforeEach
    void setUp() {
        code = someStockCode();
        standardCode = someString(7);
        name = someString(9);
        fullName = someString(11);
        marketType = someString(13);
        stockType = someString(15);
        totalIssuedQuantity = someLongBetween(1L, 1000000000L);
        price = 1000;

        given(stockItemDto.getISU_SRT_CD()).willReturn(code);
        given(stockItemDto.getISU_CD()).willReturn(standardCode);
        given(stockItemDto.getISU_ABBRV()).willReturn(name);
        given(stockItemDto.getISU_NM()).willReturn(fullName);
        given(stockItemDto.getMKT_TP_NM()).willReturn(marketType);
        given(stockItemDto.getKIND_STKCERT_TP_NM()).willReturn(stockType);
        given(stockItemDto.getLIST_SHRS()).willReturn(totalIssuedQuantity.toString());

        priceMap = Map.of(code, stkItemPriceDto);
        given(stkItemPriceDto.getTDD_CLSPRC()).willReturn(price.toString());
    }

    @Nested
    class ConvertWithPrice extends DefaultTestCases {

        @BeforeEach
        void setUp() {
            actualStock = krxStockConverter.convert(priceMap, stockItemDto);
        }

        @Test
        void shouldSetClosingPrice() {
            assertThat(actualStock.getClosingPrice(), is(price));
        }
    }

    @Nested
    class ConvertWithoutPrice extends DefaultTestCases {

        @BeforeEach
        void setUp() {
            actualStock = krxStockConverter.convert(Map.of(), stockItemDto);
        }

        @Test
        void shouldSetZeroClosingPrice() {
            assertThat(actualStock.getClosingPrice(), is(0));
        }
    }

    @SuppressWarnings("unused")
    class DefaultTestCases {

        @Test
        void shouldSetCode() {
            assertThat(actualStock.getCode(), is(code));
        }

        @Test
        void shouldSetStandardCode() {
            assertThat(actualStock.getStandardCode(), is(standardCode));
        }

        @Test
        void shouldSetName() {
            assertThat(actualStock.getName(), is(name));
        }

        @Test
        void shouldSetFullName() {
            assertThat(actualStock.getFullName(), is(fullName));
        }

        @Test
        void shouldSetMarketType() {
            assertThat(actualStock.getMarketType(), is(marketType));
        }

        @Test
        void shouldSetStockType() {
            assertThat(actualStock.getStockType(), is(stockType));
        }

        @Test
        void shouldSetTotalIssuedQuantity() {
            assertThat(actualStock.getTotalIssuedQuantity(), is(totalIssuedQuantity));
        }
    }
}
