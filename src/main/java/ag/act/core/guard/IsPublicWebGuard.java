package ag.act.core.guard;

import ag.act.core.holder.RequestContextHolder;
import ag.act.exception.ActRuntimeException;
import ag.act.exception.UnauthorizedException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class IsPublicWebGuard implements ActGuard {

    public void canActivate(Map<String, Object> parameterMap) throws ActRuntimeException {
        if (!RequestContextHolder.isWebAppVersion()) {
            throw new UnauthorizedException("인가되지 않은 접근입니다.");
        }
    }
}
