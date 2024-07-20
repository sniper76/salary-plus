package ag.act.validator;

import ag.act.dto.StockReferenceDateDto;
import ag.act.entity.Stock;
import ag.act.entity.StockReferenceDate;
import ag.act.exception.BadRequestException;
import ag.act.service.stock.StockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static ag.act.TestUtil.assertException;
import static ag.act.TestUtil.someStockCode;
import static org.mockito.BDDMockito.given;

@MockitoSettings(strictness = Strictness.LENIENT)
class StockReferenceDateValidatorTest {

    @InjectMocks
    private StockReferenceDateValidator validator;
    @Mock
    private StockService stockService;

    @Nested
    class ValidateStockReferenceDateList {
        private List<StockReferenceDate> referenceDateList;
        private String stockCode;
        private LocalDate referenceDate;

        @BeforeEach
        void setUp() {
            stockCode = someStockCode();
            referenceDate = LocalDate.now();
        }

        @Nested
        class WhenErrorListSizeMoreThanTwice {

            @BeforeEach
            void setUp() {
                StockReferenceDate stockReferenceDate1 = new StockReferenceDate();
                stockReferenceDate1.setStockCode(stockCode);
                stockReferenceDate1.setReferenceDate(referenceDate);
                StockReferenceDate stockReferenceDate2 = new StockReferenceDate();
                stockReferenceDate2.setStockCode(stockCode);
                stockReferenceDate2.setReferenceDate(referenceDate);
                referenceDateList = List.of(stockReferenceDate1, stockReferenceDate2);
            }

            @Test
            void shouldBeError() {
                assertException(
                    BadRequestException.class,
                    () -> validator.validateOnlyOneReferenceDate(referenceDateList, stockCode, referenceDate),
                    "%s 종목에 기준일 %s이 %d건 입니다.".formatted(stockCode, referenceDate, referenceDateList.size())
                );
            }
        }

        @Nested
        class WhenErrorListSizeZero {

            @BeforeEach
            void setUp() {
                referenceDateList = List.of();
            }

            @Test
            void shouldBeSizeZero() {
                assertException(
                    BadRequestException.class,
                    () -> validator.validateOnlyOneReferenceDate(referenceDateList, stockCode, referenceDate),
                    "%s 종목에 기준일 %s이 %d건 입니다.".formatted(stockCode, referenceDate, referenceDateList.size())
                );
            }
        }
    }

    @Nested
    class Validate {
        private StockReferenceDateDto stockReferenceDateDto;

        @Nested
        class WhenEmptyStock {

            @BeforeEach
            void setUp() {
                stockReferenceDateDto = new StockReferenceDateDto();
            }

            @Test
            void shouldBeException() {
                assertException(
                    BadRequestException.class,
                    () -> validator.validate(stockReferenceDateDto),
                    "종목코드를 확인해주세요."
                );
            }
        }

        @Nested
        class WhenEmptyReferenceDate {
            @Mock
            private Stock stock;

            @BeforeEach
            void setUp() {
                String stockCode = someStockCode();
                stockReferenceDateDto = new StockReferenceDateDto();
                stockReferenceDateDto.setStockCode(stockCode);

                given(stockService.findByCode(stockCode)).willReturn(Optional.of(stock));
            }

            @Test
            void shouldBeException() {
                assertException(
                    BadRequestException.class,
                    () -> validator.validate(stockReferenceDateDto),
                    "기준일을 확인해주세요."
                );
            }
        }
    }
}