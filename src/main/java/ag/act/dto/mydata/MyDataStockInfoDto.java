package ag.act.dto.mydata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyDataStockInfoDto {
    private Long pensionPaidAmount;
    private Long loanPrice;
    private List<IntermediateUserHoldingStockDto> intermediateUserHoldingStockDtoList;
    private List<AccountTransactionDto> accountTransactionDtoList;

    public Map<String, IntermediateUserHoldingStockDto> getMyDataStockMap() {
        return intermediateUserHoldingStockDtoList.stream()
            .collect(Collectors.toMap(IntermediateUserHoldingStockDto::getMyDataProdCode, Function.identity()));
    }

    public Map<String, List<AccountTransactionDto>> getAccountTransactionDtoMap() {
        final Map<String, IntermediateUserHoldingStockDto> myDataStockMap = getMyDataStockMap();

        return getAccountTransactionDtoList()
            .stream()
            .filter(accountTransactionDto -> myDataStockMap.containsKey(accountTransactionDto.getProdCode()))
            .sorted(Comparator.comparing(AccountTransactionDto::getTransactionDate).reversed())
            .collect(Collectors.groupingBy(AccountTransactionDto::getProdCode));
    }
}
