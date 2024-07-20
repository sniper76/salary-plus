package ag.act.core.guard;

import ag.act.configuration.security.ActUserProvider;
import ag.act.exception.ActRuntimeException;
import ag.act.exception.UnauthorizedException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class IsActiveUserGuard implements ActGuard {

    public void canActivate(Map<String, Object> parameterMap) throws ActRuntimeException {
        if (ActUserProvider.getNoneNull().getStatus() != ag.act.model.Status.ACTIVE) {
            throw new UnauthorizedException("회원 상태를 확인해주세요.");
        }
    }
}
