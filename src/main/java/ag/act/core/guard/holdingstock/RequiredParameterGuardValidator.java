package ag.act.core.guard.holdingstock;

import ag.act.exception.InternalServerException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(30)
@RequiredArgsConstructor
@Component
public class RequiredParameterGuardValidator implements HoldingStockGuardValidator {

    @Override
    public boolean validate(HoldingStockGuardParameter parameter) {
        final String stockCode = parameter.stockCode();
        final Long digitalDocumentId = parameter.digitalDocumentId();

        if (stockCode == null && digitalDocumentId == null) {
            throw new InternalServerException("stockCode 혹은 digitalDocumentId 둘중 하나는 필수 항목입니다.");
        }
        return false;
    }
}
