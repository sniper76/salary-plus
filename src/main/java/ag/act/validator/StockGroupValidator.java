package ag.act.validator;

import ag.act.entity.Stock;
import ag.act.exception.BadRequestException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class StockGroupValidator {

    public void validateStockCodes(List<String> stockCodes, List<Stock> stocks) {

        final List<String> notFoundStockCodes = findNotFoundStockCodes(stockCodes, toMap(stocks));

        if (CollectionUtils.isNotEmpty(notFoundStockCodes)) {
            throw new BadRequestException("해당 종목코드를 찾을 수 없습니다. (%s)".formatted(String.join(",", notFoundStockCodes)));
        }
    }

    private List<String> findNotFoundStockCodes(List<String> stockCodes, Map<String, Stock> stockByCodeMap) {
        return stockCodes.stream()
            .filter(stockCode -> !stockByCodeMap.containsKey(stockCode))
            .toList();
    }

    private Map<String, Stock> toMap(List<Stock> stocks) {
        return stocks.stream()
            .collect(Collectors.toMap(Stock::getCode, Function.identity()));
    }
}
