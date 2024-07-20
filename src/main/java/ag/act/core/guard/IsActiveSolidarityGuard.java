package ag.act.core.guard;

import ag.act.entity.Solidarity;
import ag.act.exception.ActRuntimeException;
import ag.act.exception.ForbiddenException;
import ag.act.model.Status;
import ag.act.service.solidarity.SolidarityService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class IsActiveSolidarityGuard implements ActGuard {

    private final SolidarityService solidarityService;

    public IsActiveSolidarityGuard(SolidarityService solidarityService) {
        this.solidarityService = solidarityService;
    }

    public void canActivate(Map<String, Object> parameterMap) throws ActRuntimeException {
        final String stockCode = (String) parameterMap.get("stockCode");
        validateSolidarityStatus(stockCode);
    }

    private void validateSolidarityStatus(String stockCode) {
        final Solidarity solidarity = solidarityService.getSolidarityByStockCode(stockCode);
        if (solidarity.getStatus() != Status.ACTIVE) {
            throw new ForbiddenException("아직 준비중인 연대입니다.");
        }
    }
}
