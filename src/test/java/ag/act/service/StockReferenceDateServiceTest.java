package ag.act.service;

import ag.act.converter.stock.StockReferenceDateRequestConverter;
import ag.act.dto.StockReferenceDateDto;
import ag.act.entity.StockReferenceDate;
import ag.act.exception.BadRequestException;
import ag.act.repository.DigitalDocumentRepository;
import ag.act.repository.StockReferenceDateRepository;
import ag.act.service.stock.StockReferenceDateService;
import ag.act.validator.StockReferenceDateValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static ag.act.TestUtil.assertException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static shiver.me.timbers.data.random.RandomLongs.someLongBetween;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class StockReferenceDateServiceTest {
    @InjectMocks
    private StockReferenceDateService service;
    @Mock
    private StockReferenceDateRepository stockReferenceDateRepository;
    @Mock
    private StockReferenceDateValidator stockReferenceDateValidator;
    @Mock
    private StockReferenceDateRequestConverter stockReferenceDateRequestConverter;
    @Mock
    private DigitalDocumentRepository digitalDocumentRepository;

    @Nested
    class CreateStockReferenceDate {
        @Nested
        class Success {
            @Mock
            private StockReferenceDateDto stockReferenceDateDto;
            @Mock
            private StockReferenceDate stockReferenceDate;

            @BeforeEach
            void setUp() {
                given(stockReferenceDateRequestConverter.convert(stockReferenceDateDto))
                    .willReturn(stockReferenceDate);

                service.createStockReferenceDate(stockReferenceDateDto);
            }

            @Test
            void shouldValidateCreateStockReferenceDateRequest() {
                then(stockReferenceDateValidator).should().validate(stockReferenceDateDto);
            }

            @Test
            void shouldConvertCreateStockReferenceDateRequest() {
                then(stockReferenceDateRequestConverter).should().convert(stockReferenceDateDto);
            }

            @Test
            void shouldSaveStockReferenceDate() {
                then(stockReferenceDateRepository).should().save(stockReferenceDate);
            }
        }

        @Nested
        class WhenSameDataExists {
            @Mock
            private StockReferenceDateDto stockReferenceDateDto;
            @Mock
            private StockReferenceDate stockReferenceDate;

            @BeforeEach
            void setUp() {
                given(stockReferenceDateRepository.findByStockCodeAndReferenceDate(
                    stockReferenceDateDto.getStockCode(),
                    stockReferenceDateDto.getReferenceDate()
                )).willReturn(Optional.of(stockReferenceDate));
            }

            @Test
            void shouldThrowException() {
                assertException(
                    BadRequestException.class,
                    () -> service.createStockReferenceDate(stockReferenceDateDto),
                    "해당종목에 이미 존재하는 기준일입니다."
                );

                then(stockReferenceDateRepository).should(never()).save(any(StockReferenceDate.class));
            }
        }
    }

    @Nested
    class UpdateStockReferenceDate {
        @Nested
        class ErrorNotFound {
            @Mock
            private StockReferenceDateDto stockReferenceDateDto;
            private final Long stockReferenceDateId = someLongBetween(1L, 10L);

            @BeforeEach
            void setUp() {
                given(stockReferenceDateRepository.findById(stockReferenceDateId))
                    .willReturn(Optional.empty());
            }

            @Test
            void shouldThrowException() {
                assertException(
                    BadRequestException.class,
                    () -> service.updateStockReferenceDate(stockReferenceDateId, stockReferenceDateDto),
                    "해당 기준일 정보가 없습니다."
                );
            }
        }

        @Nested
        class ErrorStockCode {
            @Mock
            private StockReferenceDateDto stockReferenceDateDto;
            @Mock
            private StockReferenceDate stockReferenceDate;
            private final Long stockReferenceDateId = someLongBetween(1L, 10L);
            private final String stockCode1 = someString(6);
            private final String stockCode2 = someString(6);

            @BeforeEach
            void setUp() {
                given(stockReferenceDateRepository.findById(stockReferenceDateId))
                    .willReturn(Optional.of(stockReferenceDate));
                given(stockReferenceDate.getStockCode()).willReturn(stockCode1);
                given(stockReferenceDateDto.getStockCode()).willReturn(stockCode2);
            }

            @Test
            void shouldThrowException() {
                assertException(
                    BadRequestException.class,
                    () -> service.updateStockReferenceDate(stockReferenceDateId, stockReferenceDateDto),
                    "수정 대상 기준일 종목코드가 일치하지 않습니다."
                );
            }
        }
    }
}