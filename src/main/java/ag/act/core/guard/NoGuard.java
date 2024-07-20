package ag.act.core.guard;

import ag.act.exception.ActRuntimeException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NoGuard implements ActGuard {

    public void canActivate(Map<String, Object> parameterMap) throws ActRuntimeException {
        // ignore
    }
}
