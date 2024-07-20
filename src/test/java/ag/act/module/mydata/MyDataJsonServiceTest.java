package ag.act.module.mydata;

import ag.act.dto.mydata.AccountTransactionDto;
import ag.act.dto.mydata.IntermediateUserHoldingStockDto;
import ag.act.dto.mydata.TransactionType;
import ag.act.entity.mydata.JsonMyDataStock;
import ag.act.util.DateTimeUtil;
import ag.act.util.KoreanDateTimeUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static ag.act.TestUtil.someStockCode;
import static org.apache.commons.collections4.ListUtils.emptyIfNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mockStatic;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomLongs.someLongBetween;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class MyDataJsonServiceTest {

    @InjectMocks
    private MyDataJsonService service;
    private List<MockedStatic<?>> statics;
    @Mock
    private IntermediateUserHoldingStockDto stockInfoDto;
    @Mock
    private AtomicLong quantity;
    private String stockCode;
    private String stockName;
    private String myDataProdCode;
    private Long quantityValue;
    @Mock
    private LocalDateTime endOfLastYear;
    @Mock
    private LocalDate endOfLastYearLocalDate;
    private final LocalDateTime currentDateTime = LocalDateTime.now(); // before mocking.
    private final LocalDate currentDate = LocalDate.now(); // before mocking.

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(DateTimeUtil.class), mockStatic(KoreanDateTimeUtil.class));
        stockCode = someStockCode();
        stockName = someString(10);
        myDataProdCode = someString(10);
        quantityValue = someLong();

        given(stockInfoDto.getStockCode()).willReturn(stockCode);
        given(stockInfoDto.getStockName()).willReturn(stockName);
        given(stockInfoDto.getMyDataProdCode()).willReturn(myDataProdCode);
        given(quantity.get()).willReturn(quantityValue);
        given(KoreanDateTimeUtil.getEndOfLastYear()).willReturn(endOfLastYear);
        given(KoreanDateTimeUtil.getEndOfLastYearLocalDate()).willReturn(endOfLastYearLocalDate);
        given(DateTimeUtil.getTodayLocalDate()).willReturn(currentDate);
        given(DateTimeUtil.getTodayLocalDateTime()).willReturn(currentDateTime);
    }

    @AfterEach
    void tearDown() {
        emptyIfNull(statics).forEach(MockedStatic::close);
    }

    @Nested
    class CreateEndOfLastYearJsonMyDataStock {

        @Test
        void shouldCreateTheEndOfLastYearJsonMyDataStock() {

            // When
            final JsonMyDataStock actual = service.createEndOfLastYearJsonMyDataStock(stockInfoDto, quantityValue);

            // Then
            assertThat(actual.getCode(), is(stockCode));
            assertThat(actual.getName(), is(stockName));
            assertThat(actual.getMyDataProdCode(), is(myDataProdCode));
            assertThat(actual.getQuantity(), is(quantityValue));
            assertThat(actual.getUpdatedAt(), is(currentDateTime));
            assertThat(actual.getRegisterDate(), is(currentDate));
            assertThat(actual.getReferenceDate(), is(endOfLastYearLocalDate));
        }
    }

    @Nested
    class CreateNewJsonMyDataStocks {
        @Mock
        private AccountTransactionDto accountTransactionDto;
        private List<AccountTransactionDto> accountTransactionDtos;
        @Mock
        private LocalDateTime lastUpdatedAt;
        @Mock
        private LocalDateTime transactionLocalDateTime;
        private final LocalDate referenceDate = currentDate;
        private Long quantityValue;
        private Long transactionNumber;

        @BeforeEach
        void setUp() {
            quantityValue = someLongBetween(100L, 200L);
            transactionNumber = someLongBetween(10L, 100L);
            accountTransactionDtos = List.of(accountTransactionDto);

            given(accountTransactionDto.getTransNum()).willReturn(transactionNumber.toString());
            given(accountTransactionDto.getTransactionLocalDateTime()).willReturn(transactionLocalDateTime);
            given(accountTransactionDto.getTransactionType()).willReturn(TransactionType.BUY);
            given(transactionLocalDateTime.isAfter(lastUpdatedAt)).willReturn(true);
            given(transactionLocalDateTime.toLocalDate()).willReturn(referenceDate);
            given(quantity.get()).willReturn(quantityValue);
        }

        @Test
        void shouldCreateNewJsonMyDataStocks() {

            final List<JsonMyDataStock> actual = service.createNewJsonMyDataStocks(
                accountTransactionDtos,
                stockInfoDto,
                lastUpdatedAt,
                quantity
            );

            assertThat(actual.size(), is(2));
            final JsonMyDataStock jsonMyDataStock = actual.get(0);
            assertThat(jsonMyDataStock.getCode(), is(stockCode));
            assertThat(jsonMyDataStock.getName(), is(stockName));
            assertThat(jsonMyDataStock.getMyDataProdCode(), is(myDataProdCode));
            assertThat(jsonMyDataStock.getQuantity(), is(quantityValue));
            assertThat(jsonMyDataStock.getUpdatedAt(), is(currentDateTime));
            assertThat(jsonMyDataStock.getRegisterDate(), is(currentDate));
            assertThat(jsonMyDataStock.getReferenceDate(), is(referenceDate));

            then(quantity).should().set(quantityValue - transactionNumber);
        }
    }
}