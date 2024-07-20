package ag.act.core.guard;

import ag.act.exception.ActRuntimeException;

import java.util.Map;

public interface ActGuard {
    void canActivate(Map<String, Object> parameterMap) throws ActRuntimeException;
}
