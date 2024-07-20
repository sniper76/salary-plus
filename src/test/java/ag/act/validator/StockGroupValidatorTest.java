package ag.act.validator;

import ag.act.entity.Stock;
import ag.act.exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static ag.act.TestUtil.assertException;
import static ag.act.TestUtil.someStockCode;
import static org.mockito.BDDMockito.given;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class StockGroupValidatorTest {

    @InjectMocks
    private StockGroupValidator validator;

    @Nested
    class ValidateStockCodes {
        private List<String> stockCodes;
        private List<Stock> stocks;
        @Mock
        private Stock stock1;
        @Mock
        private Stock stock2;
        private String stockCode1;

        @BeforeEach
        void setUp() {
            stockCode1 = someStockCode();
            String stockCode2 = someStockCode();
            stockCodes = List.of(stockCode1, stockCode2);
            stocks = List.of(stock1, stock2);

            given(stock1.getCode()).willReturn(stockCode1);
            given(stock2.getCode()).willReturn(stockCode2);
        }

        @Nested
        class ValidationSuccess {
            @Test
            void shouldValidateWithoutException() {
                validator.validateStockCodes(stockCodes, stocks);
            }
        }

        @Nested
        class ValidationFail {

            @Test
            void shouldThrowBadRequestException() {
                // Given
                final String notFoundStockCode = someString(5);
                stockCodes = List.of(stockCode1, notFoundStockCode);
                stocks = List.of(stock1, stock2);

                // When // Then
                assertException(
                    BadRequestException.class,
                    () -> validator.validateStockCodes(stockCodes, stocks),
                    "해당 종목코드를 찾을 수 없습니다. (%s)".formatted(notFoundStockCode)
                );
            }
        }
    }
}