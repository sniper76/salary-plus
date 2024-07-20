package ag.act.core.guard;

import ag.act.exception.ActRuntimeException;
import ag.act.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BatchApiKeyGuard implements ActGuard {

    private final String extraApiKey;

    public BatchApiKeyGuard(@Value("${batch.api-key:extraApiKey}") String extraApiKey) {
        this.extraApiKey = extraApiKey;
    }

    @SuppressWarnings("checkstyle:ParameterName")
    public void canActivate(Map<String, Object> parameterMap) throws ActRuntimeException {
        final String xApiKey = (String) parameterMap.get("xApiKey");

        if (!extraApiKey.equals(xApiKey)) {
            throw new UnauthorizedException("인가되지 않은 접근입니다.");
        }
    }
}
