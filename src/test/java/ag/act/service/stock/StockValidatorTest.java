package ag.act.service.stock;

import ag.act.entity.Stock;
import ag.act.exception.BadRequestException;
import ag.act.repository.StockRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;
import java.util.stream.Stream;

import static ag.act.TestUtil.assertException;
import static ag.act.TestUtil.someStockCode;
import static org.mockito.BDDMockito.given;

@MockitoSettings(strictness = Strictness.LENIENT)
class StockValidatorTest {

    @InjectMocks
    private StockService validator;

    @Mock
    private StockRepository stockRepository;

    @Nested
    class ValidateStockCode {
        @Mock
        private Stock stock;
        private String stockCode;

        @Nested
        class WhenStockCodeIsInvalid {

            @ParameterizedTest(name = "{index} => stockCode=''{0}''")
            @MethodSource("valueProvider")
            void shouldThrowBadRequest(String stockCode) {
                assertException(
                    BadRequestException.class,
                    () -> validator.validateStockCode(stockCode),
                    "종목코드를 확인해주세요."
                );
            }

            private static Stream<Arguments> valueProvider() {
                return Stream.of(
                    Arguments.of((String) null),
                    Arguments.of(""),
                    Arguments.of("     ")
                );
            }
        }

        @Nested
        class WhenStockNotFound {
            @Test
            void shouldThrowBadRequest() {

                // Given
                stockCode = someStockCode();
                given(stockRepository.findByCode(stockCode)).willReturn(Optional.empty());

                // When // Then
                assertException(
                    BadRequestException.class,
                    () -> validator.validateStockCode(stockCode),
                    "존재하지 않는 종목입니다."
                );
            }
        }

        @Nested
        class WhenStockFound {
            @Test
            void shouldNotThrowException() {

                // Given
                stockCode = someStockCode();
                given(stockRepository.findByCode(stockCode)).willReturn(Optional.of(stock));

                // When // Then
                validator.validateStockCode(stockCode);
            }
        }
    }
}
