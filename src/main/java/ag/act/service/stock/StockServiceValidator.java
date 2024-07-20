package ag.act.service.stock;

import ag.act.entity.Stock;
import ag.act.exception.BadRequestException;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

public interface StockServiceValidator {

    String NOT_FOUND_MESSAGE = "존재하지 않는 종목입니다.";

    default void validateStockCode(String stockCode) {
        if (StringUtils.isBlank(stockCode)) {
            throw new BadRequestException("종목코드를 확인해주세요.");
        }

        findByCode(stockCode)
            .orElseThrow(() -> new BadRequestException(NOT_FOUND_MESSAGE));
    }

    Optional<Stock> findByCode(String stockCode);
}
