package ag.act.validator.solidarity;

import ag.act.dto.SolidarityLeaderApplicationDto;
import ag.act.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class SolidarityLeaderElectionValidator {

    public void validateSameStockCode(String stockCode, SolidarityLeaderApplicationDto application) {
        if (!isValidApplication(application)
            || !isSameStockCode(stockCode, application)
        ) {
            throw new NotFoundException("해당 종목에 대한 지원 내역이 없습니다.");
        }
    }

    private boolean isSameStockCode(String stockCode, SolidarityLeaderApplicationDto application) {
        return Objects.equals(application.getSimpleStock().getCode(), stockCode);
    }

    private boolean isValidApplication(SolidarityLeaderApplicationDto application) {
        return application != null && application.getSimpleStock() != null;
    }
}
