package ag.act.module.mydata.load;

import ag.act.dto.mydata.AccountProductDto;
import ag.act.dto.mydata.AccountTransactionDto;
import ag.act.dto.mydata.BasicInfoDto;
import ag.act.dto.mydata.IntermediateUserHoldingStockDto;
import ag.act.dto.mydata.InvestInfo;
import ag.act.dto.mydata.MyDataDto;
import ag.act.dto.mydata.MyDataProductType;
import ag.act.entity.Stock;
import ag.act.service.stock.StockService;
import ag.act.util.DateTimeUtil;
import ag.act.util.NumberUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class MyDataProcessor {
    private final StockService stockService;

    public MyDataProcessor(StockService stockService) {
        this.stockService = stockService;
    }

    public List<AccountProductDto> extractValidAccountProducts(MyDataDto myDataDto) {

        final String defaultBaseDate = DateTimeUtil.formatLocalDateTime(DateTimeUtil.getTodayLocalDateTime(), "yyyyMMdd");

        final List<AccountProductDto> virtualAccountProductDtos = myDataDto.getInvestInfo().stream()
            .map(InvestInfo::getAcctTranList)
            .flatMap(List::stream)
            .filter(AccountTransactionDto::isValid)
            .filter(it -> "KRW".equals(it.getCurrencyCode())) // "KRW" 통화코드만 처리
            .collect(Collectors.toMap(AccountTransactionDto::getProdCode, Function.identity(), (v1, v2) -> v1))
            .values().stream()
            .map(it -> AccountProductDto.builder()
                .prodCode(it.getProdCode())
                .prodName(it.getProdName())
                .baseDate(defaultBaseDate)
                .prodType(MyDataProductType.DOMESTIC_STOCK.getProdCode())
                .holdingNum("0")
                .currencyCode(it.getCurrencyCode())
                .creditType("01")
                .purchaseAmt("0")
                .build())
            .toList();

        final List<AccountProductDto> accountProductDtos = myDataDto.getInvestInfo().stream()
            .map(InvestInfo::getAcctPrdList)
            .flatMap(List::stream)
            .filter(AccountProductDto::isValid)
            .toList();

        return Stream.concat(virtualAccountProductDtos.stream(), accountProductDtos.stream())
            .collect(
                Collectors.toMap(
                    AccountProductDto::getProdCode,
                    Function.identity(),
                    (v1, v2) -> {
                        // AccountProductDto accountProductDto =
                        // NumberUtil.toLong(v1.getHoldingNum(), 0L) > NumberUtil.toLong(v2.getHoldingNum(), 0L) ? v1 : v2;

                        // TODO creditType 가 다른 경우가 합쳐이면 엉망이 된다.이 부분을 수정해야 한다.
                        v1.setHoldingNum(sum(v1.getHoldingNum(), v2.getHoldingNum()));
                        v1.setPurchaseAmt(sum(v1.getPurchaseAmt(), v2.getPurchaseAmt()));

                        return v1;
                    }
                )
            ).values().stream()
            .toList();
    }

    private String sum(String purchaseAmt1, String purchaseAmt2) {
        return String.valueOf(NumberUtil.toLong(purchaseAmt1, 0L)
            + NumberUtil.toLong(purchaseAmt2, 0L));
    }

    public Map<String, Stock> getStockMapByMyDataCode(List<String> stockCodes) {

        final Map<String, Stock> stockMapByCode = stockService.findAllByCodes(stockCodes)
            .stream()
            .collect(Collectors.toMap(Stock::getCode, Function.identity(), (v1, v2) -> v1));

        final Map<String, Stock> stockMapByStandardCode = stockService.findByStandardCodes(stockCodes)
            .stream()
            .collect(Collectors.toMap(Stock::getStandardCode, Function.identity(), (v1, v2) -> v1));

        return Stream.of(stockMapByCode, stockMapByStandardCode)
            .flatMap(map -> map.entrySet().stream())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1));
    }

    public List<IntermediateUserHoldingStockDto> getIntermediateUserHoldingStockDtoList(
        List<AccountProductDto> accountProductDtoList,
        Map<String, Stock> myDataStockMap
    ) {
        return accountProductDtoList
            .stream()
            .map(accountProductDto -> createIntermediateUserHoldingStockDto(myDataStockMap, accountProductDto))
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(
                IntermediateUserHoldingStockDto::getStockCode,
                Function.identity(),
                this::mergeIntermediateUserHoldingStockDtos)
            )
            .values()
            .stream()
            .toList();
    }

    private IntermediateUserHoldingStockDto createIntermediateUserHoldingStockDto(
        Map<String, Stock> myDataStockMap,
        AccountProductDto accountProductDto
    ) {
        try {
            final IntermediateUserHoldingStockDto intermediateUserHoldingStockDto = IntermediateUserHoldingStockDto.builder()
                .quantity(NumberUtil.toLong(accountProductDto.getHoldingNum(), 0L))
                .stockCode(myDataStockMap.get(accountProductDto.getProdCode()).getCode())
                .stockName(myDataStockMap.get(accountProductDto.getProdCode()).getName())
                .myDataProdCode(accountProductDto.getProdCode())
                .purchasePrice(NumberUtil.toLong(accountProductDto.getPurchaseAmt(), 0L)) // 순 매입금액
                .creditType(accountProductDto.getStockCreditType())
                .build();

            intermediateUserHoldingStockDto.withCreditType(accountProductDto.getStockCreditType());

            return intermediateUserHoldingStockDto;
        } catch (NullPointerException e) {
            log.info("Failed to process MyData accountProductList - Message: {}, ProdCode:{}, ProdType:{}, CreditType:{}",
                e.getMessage(),
                accountProductDto.getProdCode(),
                accountProductDto.getProdType(),
                accountProductDto.getStockCreditType()
            );
        } catch (Exception e) {
            log.error("Unknown error while processing MyData accountProductList", e);
        }
        return null;
    }

    private IntermediateUserHoldingStockDto mergeIntermediateUserHoldingStockDtos(
        IntermediateUserHoldingStockDto target,
        IntermediateUserHoldingStockDto source
    ) {
        return target.getCreditType().merge(target, source);
    }

    public List<AccountTransactionDto> extractValidAccountTransactions(MyDataDto myDataDto) {
        return myDataDto.getInvestInfo().stream()
            .map(InvestInfo::getAcctTranList)
            .flatMap(List::stream)
            .filter(AccountTransactionDto::isValid)
            .toList();
    }

    public long calculateLoanPrice(MyDataDto myDataDto) {
        return myDataDto.getInvestInfo().stream()
            .map(InvestInfo::getBasicList)
            .flatMap(List::stream)
            .mapToLong(BasicInfoDto::getLoanPrice)
            .sum();
    }

    public Long calculatePensionPaidAmount(MyDataDto myDataDto) {
        return myDataDto.getInvestInfo().stream()
            .map(InvestInfo::getPaidInAmt)
            .mapToLong(NumberUtil::toLong)
            .sum();
    }
}
