package ag.act.module.mydata;

import ag.act.dto.mydata.AccountProductDto;
import ag.act.dto.mydata.IntermediateUserHoldingStockDto;
import ag.act.dto.mydata.MyDataDto;
import ag.act.dto.mydata.MyDataStockInfoDto;
import ag.act.entity.Stock;
import ag.act.entity.UserHoldingStock;
import ag.act.entity.UserHoldingStockBase;
import ag.act.service.stock.StockService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MyDataLoadService {
    private final MyDataJsonReader myDataJsonReader;
    private final MyDataProcessor myDataProcessor;
    private final StockService stockService;

    public MyDataStockInfoDto getMyDataStocks(String jsonContent) {
        final MyDataDto myDataDto = myDataJsonReader.readEncodedMyData(jsonContent);

        final List<AccountProductDto> accountProductDtoList = myDataProcessor.extractValidAccountProducts(myDataDto);

        final Map<String, Stock> myDataStockMap = myDataProcessor.getStockMapByMyDataCode(
            accountProductDtoList.stream()
                .map(AccountProductDto::getProdCode)
                .toList()
        );

        return MyDataStockInfoDto.builder()
            .accountTransactionDtoList(myDataProcessor.extractValidAccountTransactions(myDataDto))
            .intermediateUserHoldingStockDtoList(myDataProcessor.getIntermediateUserHoldingStockDtoList(accountProductDtoList, myDataStockMap))
            .pensionPaidAmount(myDataProcessor.calculatePensionPaidAmount(myDataDto))
            .loanPrice(myDataProcessor.calculateLoanPrice(myDataDto))
            .build();
    }

    public List<UserHoldingStock> getUserHoldingStocksForDelete(Map<String, UserHoldingStock> userHoldingStockMap) {
        return userHoldingStockMap
            .values()
            .stream()
            .map(this::setInvalidAndGet)
            .toList();
    }

    private UserHoldingStock setInvalidAndGet(UserHoldingStock userHoldingStock) {
        userHoldingStock.setStatus(ag.act.model.Status.INACTIVE_BY_USER);
        return userHoldingStock;
    }

    public List<UserHoldingStock> getUserHoldingStocksForInsertUpdate(
        Long userId,
        List<IntermediateUserHoldingStockDto> myDataStocks,
        Map<String, UserHoldingStock> userHoldingStockMap
    ) {
        return myDataStocks.stream()
            .filter(interDto -> interDto.getQuantity() > 0)
            .map(interDto -> this.convertToUserHoldingStockForInsertUpdate(interDto, userId, userHoldingStockMap))
            .toList();
    }

    private UserHoldingStock convertToUserHoldingStockForInsertUpdate(
        IntermediateUserHoldingStockDto stockInfoDto, Long userId,
        Map<String, UserHoldingStock> userHoldingStockMap
    ) {
        UserHoldingStock userHoldingStock = getExistingUserHoldingStockOrNew(stockInfoDto, userHoldingStockMap);

        userHoldingStock.setStockCode(stockInfoDto.getStockCode());
        userHoldingStock.setUserId(userId);
        userHoldingStock.setStatus(ag.act.model.Status.ACTIVE);
        userHoldingStock.setQuantity(stockInfoDto.getQuantity());
        userHoldingStock.setCashQuantity(stockInfoDto.getCashQuantity());
        userHoldingStock.setCreditQuantity(stockInfoDto.getCreditQuantity());
        userHoldingStock.setSecureLoanQuantity(stockInfoDto.getSecureLoanQuantity());
        userHoldingStock.setPurchasePrice(stockInfoDto.getPurchasePrice());

        return userHoldingStock;
    }

    private UserHoldingStock getExistingUserHoldingStockOrNew(
        IntermediateUserHoldingStockDto stockInfoDto,
        Map<String, UserHoldingStock> userHoldingStockMap
    ) {
        return Optional.ofNullable(userHoldingStockMap.remove(stockInfoDto.getStockCode()))
            .orElseGet(() -> {
                UserHoldingStock newUserHoldingStock = new UserHoldingStock();
                newUserHoldingStock.setDisplayOrder(UserHoldingStockBase.INITIAL_DISPLAY_ORDER);
                newUserHoldingStock.setStock(stockService.findByCode(stockInfoDto.getStockCode()).orElse(null));
                return newUserHoldingStock;
            });
    }

}
