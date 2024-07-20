package ag.act.facade;

import ag.act.converter.stock.StockReferenceDateRequestConverter;
import ag.act.converter.stock.StockReferenceDateResponseConverter;
import ag.act.dto.StockReferenceDateDto;
import ag.act.entity.StockReferenceDate;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.exception.BadRequestException;
import ag.act.exception.NotFoundException;
import ag.act.facade.stock.StockReferenceDateFacade;
import ag.act.service.digitaldocument.DigitalDocumentService;
import ag.act.service.stock.StockReferenceDateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.util.Optional;

import static ag.act.TestUtil.assertException;
import static ag.act.TestUtil.someLocalDate;
import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static shiver.me.timbers.data.random.RandomLongs.someLongBetween;

@MockitoSettings(strictness = Strictness.LENIENT)
class StockReferenceDateFacadeTest {
    @InjectMocks
    private StockReferenceDateFacade facade;
    @Mock
    private StockReferenceDateResponseConverter stockReferenceDateResponseConverter;
    @Mock
    private StockReferenceDateRequestConverter stockReferenceDateRequestConverter;
    @Mock
    private StockReferenceDateService stockReferenceService;
    @Mock
    private DigitalDocumentService digitalDocumentService;

    @Nested
    class CreateStockReferenceDate {
        @Mock
        private ag.act.model.CreateStockReferenceDateRequest createStockReferenceDateRequest;
        @Mock
        private StockReferenceDate stockReferenceDate;
        @Mock
        private ag.act.model.StockReferenceDateResponse stockReferenceDateResponse;
        @Mock
        private StockReferenceDateDto stockReferenceDateDto;
        private ag.act.model.StockReferenceDateResponse actualResult;
        private String stockCode;

        @BeforeEach
        void setUp() {
            stockCode = someStockCode();
            given(stockReferenceDateRequestConverter.convert(stockCode, createStockReferenceDateRequest))
                .willReturn(stockReferenceDateDto);
            given(stockReferenceService.createStockReferenceDate(stockReferenceDateDto))
                .willReturn(stockReferenceDate);
            given(stockReferenceDateResponseConverter.apply(stockReferenceDate))
                .willReturn(stockReferenceDateResponse);

            actualResult = facade.createStockReferenceDate(stockCode, createStockReferenceDateRequest);
        }

        @Test
        void shouldReturnStockReferenceDateResponse() {
            assertThat(actualResult, is(stockReferenceDateResponse));
        }

        @Test
        void shouldConvertToDto() {
            then(stockReferenceDateRequestConverter).should().convert(stockCode, createStockReferenceDateRequest);
        }

        @Test
        void shouldCreateStockReferenceDate() {
            then(stockReferenceService).should().createStockReferenceDate(stockReferenceDateDto);
        }

        @Test
        void shouldCallPushResponseConverterToConvertPush() {
            then(stockReferenceDateResponseConverter).should().apply(stockReferenceDate);
        }
    }

    @Nested
    class DeleteStockReferenceDate {

        @Mock
        private StockReferenceDate stockReferenceDate;
        private String stockCode;
        private Long stockReferenceDateId;

        @BeforeEach
        void setUp() {
            stockCode = someStockCode();
            stockReferenceDateId = someLongBetween(1L, 10L);
            final LocalDate stockReferenceLocalDate = someLocalDate();

            given(stockReferenceDate.getStockCode()).willReturn(stockCode);
            given(stockReferenceDate.getReferenceDate()).willReturn(stockReferenceLocalDate);
            given(stockReferenceService.findById(stockReferenceDateId))
                .willReturn(Optional.of(stockReferenceDate));
            given(digitalDocumentService.findDigitalDocument(stockCode, stockReferenceDate.getReferenceDate()))
                .willReturn(Optional.empty());

            facade.deleteStockReferenceDate(stockReferenceDateId);
        }

        @Test
        void shouldGetStockReferenceDate() {
            then(stockReferenceService).should().findById(stockReferenceDateId);
        }

        @Test
        void shouldNotGetDigitalDocument() {
            then(digitalDocumentService).should().findDigitalDocument(stockCode, stockReferenceDate.getReferenceDate());
        }

        @Test
        void shouldCallStockReferenceDate() {
            then(stockReferenceService).should().deleteStockReferenceDate(stockReferenceDate);
        }
    }

    @Nested
    class NotAbleToDeleteStockReferenceDate {

        @Mock
        private StockReferenceDate stockReferenceDate;
        private String stockCode;
        private Long stockReferenceDateId;

        @BeforeEach
        void setUp() {
            stockCode = someStockCode();
            stockReferenceDateId = someLongBetween(1L, 10L);
            final LocalDate stockReferenceLocalDate = someLocalDate();

            given(stockReferenceDate.getStockCode()).willReturn(stockCode);
            given(stockReferenceDate.getReferenceDate()).willReturn(stockReferenceLocalDate);
            given(stockReferenceService.findById(stockReferenceDateId))
                .willReturn(Optional.of(stockReferenceDate));
            given(digitalDocumentService.findDigitalDocument(stockCode, stockReferenceDate.getReferenceDate()))
                .willReturn(Optional.empty());

            facade.deleteStockReferenceDate(stockReferenceDateId);
        }


        @Nested
        class NotExist {

            @BeforeEach
            void setUp() {
                given(stockReferenceService.findById(stockReferenceDateId))
                    .willReturn(Optional.empty());
            }

            @Test
            void shouldThrowNotFoundException() {
                assertException(
                    NotFoundException.class,
                    () -> facade.deleteStockReferenceDate(stockReferenceDateId),
                    "존재하지 않는 기준일입니다."
                );
            }
        }

        @Nested
        class AlreadyUsedInDigitalDocuments {

            @Mock
            private DigitalDocument digitalDocument;

            @BeforeEach
            void setUp() {
                given(digitalDocumentService.findDigitalDocument(stockCode, stockReferenceDate.getReferenceDate()))
                    .willReturn(Optional.of(digitalDocument));
            }

            @Test
            void shouldThrowBadRequestException() {
                assertException(
                    BadRequestException.class,
                    () -> facade.deleteStockReferenceDate(stockReferenceDateId),
                    "전자문서에서 사용된 종목(%s)의 기준일(%s)은 삭제할 수 없습니다."
                        .formatted(stockReferenceDate.getStockCode(), stockReferenceDate.getReferenceDate())
                );
            }
        }
    }
}
