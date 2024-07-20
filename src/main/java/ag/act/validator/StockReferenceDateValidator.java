package ag.act.validator;

import ag.act.dto.StockReferenceDateDto;
import ag.act.entity.StockReferenceDate;
import ag.act.exception.BadRequestException;
import ag.act.service.stock.StockService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class StockReferenceDateValidator {
    private final StockService stockService;

    public void validate(StockReferenceDateDto stockReferenceDateDto) {
        validateStockCode(stockReferenceDateDto.getStockCode());
        validateReferenceDate(stockReferenceDateDto.getReferenceDate());
    }

    private void validateStockCode(String stockCode) {
        if (StringUtils.isBlank(stockCode)) {
            throw new BadRequestException("종목코드를 확인해주세요.");
        }

        stockService.findByCode(stockCode)
            .orElseThrow(() -> new BadRequestException("종목코드를 확인해주세요."));
    }

    private void validateReferenceDate(LocalDate referenceDate) {
        if (referenceDate == null) {
            throw new BadRequestException("기준일을 확인해주세요.");
        }
    }

    public void validateOnlyOneReferenceDate(
        List<StockReferenceDate> referenceDateList, String stockCode, LocalDate searchReferenceDate
    ) {
        final int sizeOfReferenceDateList = getSizeOfReferenceDateList(referenceDateList);

        if (sizeOfReferenceDateList != 1) {
            throw new BadRequestException(
                "%s 종목에 기준일 %s이 %d건 입니다.".formatted(stockCode, searchReferenceDate, sizeOfReferenceDateList)
            );
        }
    }

    private int getSizeOfReferenceDateList(List<StockReferenceDate> referenceDateList) {
        return Optional.ofNullable(referenceDateList)
            .map(CollectionUtils::size)
            .orElse(0);
    }
}
