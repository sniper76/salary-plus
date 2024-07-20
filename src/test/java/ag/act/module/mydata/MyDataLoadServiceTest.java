package ag.act.module.mydata;

import ag.act.dto.mydata.AccountProductDto;
import ag.act.dto.mydata.AccountTransactionDto;
import ag.act.dto.mydata.IntermediateUserHoldingStockDto;
import ag.act.dto.mydata.MyDataDto;
import ag.act.dto.mydata.MyDataStockInfoDto;
import ag.act.entity.Stock;
import ag.act.entity.UserHoldingStock;
import ag.act.model.Status;
import ag.act.service.stock.StockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomLongs.somePositiveLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class MyDataLoadServiceTest {

    @InjectMocks
    private MyDataLoadService service;
    @Mock
    private MyDataJsonReader myDataJsonReader;
    @Mock
    private MyDataProcessor myDataProcessor;
    @Mock
    private StockService stockService;

    @Nested
    class GetMyDataStocks {
        @Mock
        private MyDataDto myDataDto;
        @Mock
        private AccountProductDto accountProductDto;
        private String encodedJsonContent;
        private List<AccountProductDto> accountProductDtoList;
        private String prodCode;
        @Mock
        private Map<String, Stock> myDataStockMap;
        @Mock
        private List<AccountTransactionDto> accountTransactionDtoList;
        private MyDataStockInfoDto actualMyDataStockInfoDto;
        @Mock
        private List<IntermediateUserHoldingStockDto> intermediateUserHoldingStockDtoList;
        private Long pensionPaidAmount;
        private Long loanPrice;

        @BeforeEach
        void setUp() {
            encodedJsonContent = someString(5);
            accountProductDtoList = List.of(accountProductDto);
            prodCode = someStockCode();
            pensionPaidAmount = somePositiveLong();
            loanPrice = somePositiveLong();

            given(myDataJsonReader.readEncodedMyData(encodedJsonContent)).willReturn(myDataDto);
            given(myDataProcessor.extractValidAccountProducts(myDataDto)).willReturn(accountProductDtoList);
            given(accountProductDto.getProdCode()).willReturn(prodCode);
            given(myDataProcessor.getStockMapByMyDataCode(List.of(prodCode))).willReturn(myDataStockMap);
            given(myDataProcessor.extractValidAccountTransactions(myDataDto)).willReturn(accountTransactionDtoList);
            given(myDataProcessor.getIntermediateUserHoldingStockDtoList(accountProductDtoList, myDataStockMap))
                .willReturn(intermediateUserHoldingStockDtoList);
            given(myDataProcessor.calculatePensionPaidAmount(myDataDto)).willReturn(pensionPaidAmount);
            given(myDataProcessor.calculateLoanPrice(myDataDto)).willReturn(loanPrice);
            given(stockService.findByCode(anyString())).willReturn(Optional.empty());

            actualMyDataStockInfoDto = service.getMyDataStocks(encodedJsonContent);
        }

        @Test
        void shouldResultHaveAccountTransactionDtoList() {
            assertThat(actualMyDataStockInfoDto.getAccountTransactionDtoList(), is(accountTransactionDtoList));
        }

        @Test
        void shouldResultHaveIntermediateUserHoldingStockDtoList() {
            assertThat(actualMyDataStockInfoDto.getIntermediateUserHoldingStockDtoList(), is(intermediateUserHoldingStockDtoList));
        }

        @Test
        void shouldResultHavePensionPaidAmount() {
            assertThat(actualMyDataStockInfoDto.getPensionPaidAmount(), is(pensionPaidAmount));
        }

        @Test
        void shouldResultHaveLoanPrice() {
            assertThat(actualMyDataStockInfoDto.getLoanPrice(), is(loanPrice));
        }

        @Test
        void shouldReadEncodedMyData() {
            then(myDataJsonReader).should().readEncodedMyData(encodedJsonContent);
        }

        @Test
        void shouldExtractValidAccountProducts() {
            then(myDataProcessor).should().extractValidAccountProducts(myDataDto);
        }

        @Test
        void shouldGetStockMapByMyDataCode() {
            then(myDataProcessor).should().getStockMapByMyDataCode(List.of(prodCode));
        }

        @Test
        void shouldExtractValidAccountTransactions() {
            then(myDataProcessor).should().extractValidAccountTransactions(myDataDto);
        }

        @Test
        void shouldGetIntermediateUserHoldingStockDtoList() {
            then(myDataProcessor).should().getIntermediateUserHoldingStockDtoList(accountProductDtoList, myDataStockMap);
        }

        @Test
        void shouldCalculatePensionPaidAmount() {
            then(myDataProcessor).should().calculatePensionPaidAmount(myDataDto);
        }

        @Test
        void shouldCalculateLoanPrice() {
            then(myDataProcessor).should().calculateLoanPrice(myDataDto);
        }
    }

    @Nested
    class GetUserHoldingStocksForDelete {

        @Mock
        private UserHoldingStock userHoldingStock1;
        @Mock
        private UserHoldingStock userHoldingStock2;
        private List<UserHoldingStock> actualUserHoldingStocks;

        @BeforeEach
        void setUp() {
            final Map<String, UserHoldingStock> userHoldingStockMap = Map.of(
                someStockCode(), userHoldingStock1,
                someStockCode(), userHoldingStock2
            );

            actualUserHoldingStocks = service.getUserHoldingStocksForDelete(userHoldingStockMap);
        }

        @Test
        void shouldHaveTheSameSizeOfMap() {
            assertThat(actualUserHoldingStocks.size(), is(2));
        }

        @Test
        void shouldSetInactiveByUserToAllItems() {
            actualUserHoldingStocks.forEach(userHoldingStock ->
                then(userHoldingStock).should().setStatus(Status.INACTIVE_BY_USER)
            );
        }
    }

    @Nested
    class GetUserHoldingStocksForInsertUpdate {

        private Long userId;
        @Mock
        private IntermediateUserHoldingStockDto existingIntermediateUserHoldingStockDto;
        @Mock
        private IntermediateUserHoldingStockDto newIntermediateUserHoldingStockDto;
        @Mock
        private UserHoldingStock existingUserHoldingStock;
        private List<UserHoldingStock> actualUserHoldingStocks;

        @BeforeEach
        void setUp() {
            userId = someLong();
            final String existingStockCode = someStockCode();
            final Map<String, UserHoldingStock> userHoldingStockMap = new HashMap<>(Map.of(existingStockCode, existingUserHoldingStock));
            final List<IntermediateUserHoldingStockDto> intermediateUserHoldingStockDtoList = List.of(
                existingIntermediateUserHoldingStockDto,
                newIntermediateUserHoldingStockDto
            );

            given(existingIntermediateUserHoldingStockDto.getStockCode()).willReturn(existingStockCode);
            given(existingIntermediateUserHoldingStockDto.getQuantity()).willReturn(somePositiveLong());
            given(existingUserHoldingStock.getStockCode()).willReturn(existingStockCode);
            given(existingUserHoldingStock.getQuantity()).willReturn(somePositiveLong());

            mockNewStockInfoDto(newIntermediateUserHoldingStockDto);

            actualUserHoldingStocks = service.getUserHoldingStocksForInsertUpdate(
                userId,
                intermediateUserHoldingStockDtoList,
                userHoldingStockMap
            );
        }

        private void mockNewStockInfoDto(IntermediateUserHoldingStockDto newIntermediateUserHoldingStockDto) {

            given(newIntermediateUserHoldingStockDto.getStockCode()).willReturn(someStockCode());
            given(newIntermediateUserHoldingStockDto.getQuantity()).willReturn(somePositiveLong());
            given(newIntermediateUserHoldingStockDto.getCashQuantity()).willReturn(somePositiveLong());
            given(newIntermediateUserHoldingStockDto.getCreditQuantity()).willReturn(somePositiveLong());
            given(newIntermediateUserHoldingStockDto.getSecureLoanQuantity()).willReturn(somePositiveLong());
            given(newIntermediateUserHoldingStockDto.getPurchasePrice()).willReturn(somePositiveLong());
        }

        @Test
        void shouldReturnMatchedUserHoldingStocks() {
            assertThat(actualUserHoldingStocks.get(0), is(existingUserHoldingStock));
        }

        @Test
        void shouldHaveCorrectDisplayOrderOfNewUserHoldingStock() {
            assertThat(actualUserHoldingStocks.get(1).getDisplayOrder(), is(100_000));
        }

        @Test
        void shouldHaveCorrectUserIdOfNewUserHoldingStock() {
            assertThat(actualUserHoldingStocks.get(1).getUserId(), is(userId));
        }

        @Test
        void shouldHaveCorrectStockCodeOfNewUserHoldingStock() {
            assertThat(actualUserHoldingStocks.get(1).getStockCode(), is(newIntermediateUserHoldingStockDto.getStockCode()));
        }

        @Test
        void shouldHaveCorrectStatusOfNewUserHoldingStock() {
            assertThat(actualUserHoldingStocks.get(1).getStatus(), is(ag.act.model.Status.ACTIVE));
        }

        @Test
        void shouldHaveCorrectQuantityOfNewUserHoldingStock() {
            assertThat(actualUserHoldingStocks.get(1).getQuantity(), is(newIntermediateUserHoldingStockDto.getQuantity()));
        }

        @Test
        void shouldHaveCorrectCashQuantityOfNewUserHoldingStock() {
            assertThat(actualUserHoldingStocks.get(1).getCashQuantity(), is(newIntermediateUserHoldingStockDto.getCashQuantity()));
        }

        @Test
        void shouldHaveCorrectCreditQuantityOfNewUserHoldingStock() {
            assertThat(actualUserHoldingStocks.get(1).getCreditQuantity(), is(newIntermediateUserHoldingStockDto.getCreditQuantity()));
        }

        @Test
        void shouldHaveCorrectSecureLoanQuantityOfNewUserHoldingStock() {
            assertThat(actualUserHoldingStocks.get(1).getSecureLoanQuantity(), is(newIntermediateUserHoldingStockDto.getSecureLoanQuantity()));
        }

        @Test
        void shouldHaveCorrectPurchasePriceOfNewUserHoldingStock() {
            assertThat(actualUserHoldingStocks.get(1).getPurchasePrice(), is(newIntermediateUserHoldingStockDto.getPurchasePrice()));
        }
    }
}