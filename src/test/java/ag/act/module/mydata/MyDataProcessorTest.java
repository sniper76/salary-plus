package ag.act.module.mydata;

import ag.act.dto.mydata.AccountProductDto;
import ag.act.dto.mydata.AccountTransactionDto;
import ag.act.dto.mydata.BasicInfoDto;
import ag.act.dto.mydata.IntermediateUserHoldingStockDto;
import ag.act.dto.mydata.InvestInfo;
import ag.act.dto.mydata.MyDataDto;
import ag.act.entity.Stock;
import ag.act.enums.StockCreditType;
import ag.act.service.stock.StockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Map;

import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static shiver.me.timbers.data.random.RandomLongs.someLongBetween;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class MyDataProcessorTest {

    @InjectMocks
    private MyDataProcessor processor;
    @Mock
    private StockService stockService;

    @Nested
    class ExtractValidAccountProducts {

        private static final String VALID_PROD_TYPE = "401";
        private List<AccountProductDto> actualAccountProductDtos;

        @BeforeEach
        void setUp() {
            final MyDataDto myDataDto = stubMyDataDto();

            actualAccountProductDtos = processor.extractValidAccountProducts(myDataDto);
        }

        @Test
        void shouldReturnCorrectResultSize() {
            assertThat(actualAccountProductDtos.size(), is(2));
        }

        @Test
        void shouldHaveValidAccountProductDtos() {
            actualAccountProductDtos.forEach(accountProductDto -> assertThat(accountProductDto.getProdType(), is(VALID_PROD_TYPE)));
        }

        private MyDataDto stubMyDataDto() {
            return MyDataDto.builder()
                .investInfo(List.of(stubInvestInfo(), stubInvestInfo()))
                .build();
        }

        private InvestInfo stubInvestInfo() {
            return InvestInfo.builder()
                .acctPrdList(List.of(
                    AccountProductDto.builder()
                        .prodType(VALID_PROD_TYPE)
                        .prodCode(someString(5))
                        .build(),
                    AccountProductDto.builder()
                        .prodType(someString(5))
                        .prodCode(someString(5))
                        .build()
                ))
                .acctTranList(List.of())
                .build();
        }
    }

    @Nested
    class GetStockMapByMyDataCode {

        @Mock
        private Stock stock1;
        @Mock
        private Stock stock2;
        @Mock
        private Stock stock3;

        private Map<String, Stock> actualResult;
        private String stockCode1;
        private String stockCode2;
        private String standardStockCode1;
        private String standardStockCode2;

        @BeforeEach
        void setUp() {

            stockCode1 = someStockCode();
            stockCode2 = someStockCode();
            standardStockCode1 = someStockCode();
            standardStockCode2 = someStockCode();

            final List<String> stockCodes = List.of(
                stockCode1,
                stockCode2,
                standardStockCode1,
                standardStockCode2
            );

            final List<Stock> stockListByStockCode = List.of(stock1, stock2);
            final List<Stock> stockListByStandardCode = List.of(stock2, stock3);

            given(stock1.getCode()).willReturn(stockCode1);
            given(stock2.getCode()).willReturn(stockCode2);
            given(stock2.getStandardCode()).willReturn(standardStockCode1);
            given(stock3.getStandardCode()).willReturn(standardStockCode2);
            given(stockService.findAllByCodes(stockCodes)).willReturn(stockListByStockCode);
            given(stockService.findByStandardCodes(stockCodes)).willReturn(stockListByStandardCode);

            actualResult = processor.getStockMapByMyDataCode(stockCodes);
        }

        @Test
        void shouldReturnCorrectMapSize() {
            assertThat(actualResult.size(), is(4));
        }

        @Test
        void shouldIncludeStocks() {
            assertThat(actualResult.get(stockCode1), is(stock1));
            assertThat(actualResult.get(stockCode2), is(stock2));
            assertThat(actualResult.get(standardStockCode1), is(stock2));
            assertThat(actualResult.get(standardStockCode2), is(stock3));
        }
    }

    @Nested
    class GetIntermediateUserHoldingStockDtoList {

        @Mock
        private AccountProductDto accountProductDto1;
        @Mock
        private AccountProductDto accountProductDto2;
        @Mock
        private AccountProductDto accountProductDto3Null;
        private String stockCode;
        @Mock
        private Stock stock;
        private List<IntermediateUserHoldingStockDto> actualResult;
        private String holdingNum1;
        private String purchaseAmt1;
        private String holdingNum2;
        private String purchaseAmt2;

        @BeforeEach
        void setUp() {
            final String stockName = someString(7);
            stockCode = someString(5);
            holdingNum1 = someLongBetween(1000L, 9999L).toString();
            purchaseAmt1 = someLongBetween(1000L, 9999L).toString();
            holdingNum2 = someLongBetween(1000L, 9999L).toString();
            purchaseAmt2 = someLongBetween(1000L, 9999L).toString();

            final List<AccountProductDto> accountProductDtoList = List.of(
                accountProductDto1,
                accountProductDto2,
                accountProductDto3Null
            );

            final Map<String, Stock> myDataStockMap = Map.of(
                stockCode, stock
            );

            given(stock.getCode()).willReturn(stockCode);
            given(stock.getName()).willReturn(stockName);

            given(accountProductDto1.getHoldingNum()).willReturn(holdingNum1);
            given(accountProductDto1.getProdCode()).willReturn(stockCode);
            given(accountProductDto1.getStockCreditType()).willReturn(StockCreditType.CASH);
            given(accountProductDto1.getPurchaseAmt()).willReturn(purchaseAmt1);

            given(accountProductDto2.getHoldingNum()).willReturn(holdingNum2);
            given(accountProductDto2.getProdCode()).willReturn(stockCode);
            given(accountProductDto2.getStockCreditType()).willReturn(StockCreditType.CASH);
            given(accountProductDto2.getPurchaseAmt()).willReturn(purchaseAmt2);

            actualResult = processor.getIntermediateUserHoldingStockDtoList(accountProductDtoList, myDataStockMap);
        }

        @Test
        void shouldReturnCorrectSizeOfResult() {
            assertThat(actualResult.size(), is(1));
        }

        @Test
        void shouldResultHaveStockCode() {
            assertThat(actualResult.get(0).getStockCode(), is(stockCode));
        }

        @Test
        void shouldResultHaveStockName() {
            assertThat(actualResult.get(0).getStockName(), is(stock.getName()));
        }

        @Test
        void shouldResultHavePurchasePrice() {
            assertThat(actualResult.get(0).getPurchasePrice(),
                is(Long.parseLong(purchaseAmt1) + Long.parseLong(purchaseAmt2)));
        }

        @Test
        void shouldResultHaveQuantity() {
            assertThat(actualResult.get(0).getQuantity(),
                is(Long.parseLong(holdingNum1) + Long.parseLong(holdingNum2)));
        }

        @Test
        void shouldResultHaveCashQuantity() {
            assertThat(actualResult.get(0).getCashQuantity(),
                is(Long.parseLong(holdingNum1) + Long.parseLong(holdingNum2)));
        }

        @Test
        void shouldResultHaveCreditQuantity() {
            assertThat(actualResult.get(0).getCreditQuantity(), is(0L));
        }

        @Test
        void shouldResultHaveSecureLoanQuantity() {
            assertThat(actualResult.get(0).getSecureLoanQuantity(), is(0L));
        }

        @Test
        void shouldResultHaveCreditType() {
            assertThat(actualResult.get(0).getCreditType(), is(StockCreditType.CASH));
        }

        @Test
        void shouldResultHaveMyDataProdCode() {
            assertThat(actualResult.get(0).getMyDataProdCode(), is(stockCode));
        }
    }

    @Nested
    class CalculateLoanPrice {

        private long expectedLoanPrice;
        @Mock
        private BasicInfoDto basicInfoDto1;
        @Mock
        private BasicInfoDto basicInfoDto2;

        @BeforeEach
        void setUp() {
            final Long loanPrice1 = someLongBetween(100L, 999L);
            final Long loanPrice2 = someLongBetween(100L, 999L);

            expectedLoanPrice = loanPrice1 + loanPrice2 + loanPrice1 + loanPrice2;

            given(basicInfoDto1.getLoanPrice()).willReturn(loanPrice1);
            given(basicInfoDto2.getLoanPrice()).willReturn(loanPrice2);

        }

        @Test
        void shouldReturnCorrectLoanPrice() {
            long actualLoanPrice = processor.calculateLoanPrice(stubMyDataDto());

            assertThat(actualLoanPrice, is(expectedLoanPrice));
        }

        private MyDataDto stubMyDataDto() {
            return MyDataDto.builder()
                .investInfo(List.of(stubInvestInfo(), stubInvestInfo()))
                .build();
        }

        private InvestInfo stubInvestInfo() {
            return InvestInfo.builder()
                .basicList(List.of(basicInfoDto1, basicInfoDto2))
                .build();
        }
    }

    @Nested
    class ExtractValidAccountTransactions {

        private List<AccountTransactionDto> actualAccountTransactionDtos;
        @Mock
        private AccountTransactionDto validAccountTransactionDto;
        @Mock
        private AccountTransactionDto invalidAccountTransactionDto;

        @BeforeEach
        void setUp() {
            final MyDataDto myDataDto = stubMyDataDto();

            given(validAccountTransactionDto.isValid()).willReturn(true);
            given(invalidAccountTransactionDto.isValid()).willReturn(false);

            actualAccountTransactionDtos = processor.extractValidAccountTransactions(myDataDto);
        }

        @Test
        void shouldReturnCorrectResultSize() {
            assertThat(actualAccountTransactionDtos.size(), is(2));
        }

        @Test
        void shouldHaveValidAccountTransactionDtos() {
            actualAccountTransactionDtos.forEach(accountTransactionDto -> assertThat(accountTransactionDto.isValid(), is(true)));
        }

        private MyDataDto stubMyDataDto() {
            return MyDataDto.builder()
                .investInfo(List.of(stubInvestInfo(), stubInvestInfo()))
                .build();
        }

        private InvestInfo stubInvestInfo() {
            return InvestInfo.builder()
                .acctTranList(List.of(validAccountTransactionDto, invalidAccountTransactionDto))
                .build();
        }
    }

    @Nested
    class CalculatePensionPaidAmount {

        private long expectedPensionPaidAmount;
        @Mock
        private InvestInfo investInfo1;
        @Mock
        private InvestInfo investInfo2;

        @BeforeEach
        void setUp() {
            final Long pensionPaidAmount1 = someLongBetween(100L, 999L);
            final Long pensionPaidAmount2 = someLongBetween(100L, 999L);

            expectedPensionPaidAmount = pensionPaidAmount1 + pensionPaidAmount2;

            given(investInfo1.getPaidInAmt()).willReturn(pensionPaidAmount1.toString());
            given(investInfo2.getPaidInAmt()).willReturn(pensionPaidAmount2.toString());
        }

        @Test
        void shouldReturnCorrectPensionPaidAmount() {
            long actualPensionPaidAmount = processor.calculatePensionPaidAmount(stubMyDataDto());

            assertThat(actualPensionPaidAmount, is(expectedPensionPaidAmount));
        }

        private MyDataDto stubMyDataDto() {
            return MyDataDto.builder()
                .investInfo(List.of(investInfo1, investInfo2))
                .build();
        }
    }
}