package ag.act.core.guard;

import ag.act.configuration.security.ActUserProvider;
import ag.act.entity.User;
import ag.act.exception.ActRuntimeException;
import ag.act.exception.BadRequestException;
import ag.act.service.solidarity.election.SolidarityLeaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class IsSolidarityLeaderGuard implements ActGuard {
    private final SolidarityLeaderService solidarityLeaderService;

    public void canActivate(Map<String, Object> parameterMap) throws ActRuntimeException {
        final String stockCode = (String) parameterMap.get("stockCode");
        validateSolidarityLeader(stockCode);
    }

    private void validateSolidarityLeader(String stockCode) {
        final User user = ActUserProvider.getNoneNull();

        if (!solidarityLeaderService.isLeader(user.getId(), stockCode)) {
            throw new BadRequestException("주주대표만 가능한 기능입니다.");
        }
    }
}
