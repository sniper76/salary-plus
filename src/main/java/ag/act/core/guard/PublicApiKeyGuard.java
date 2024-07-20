package ag.act.core.guard;

import ag.act.exception.ActRuntimeException;
import ag.act.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PublicApiKeyGuard implements ActGuard {

    private final String publicApiKey;

    public PublicApiKeyGuard(@Value("${act.public.api-key:publicApiKey}") String publicApiKey) {
        this.publicApiKey = publicApiKey;
    }

    @SuppressWarnings("checkstyle:ParameterName")
    public void canActivate(Map<String, Object> parameterMap) throws ActRuntimeException {
        final String xApiKey = (String) parameterMap.get("xApiKey");

        if (!publicApiKey.equals(xApiKey)) {
            throw new UnauthorizedException("인가되지 않은 접근입니다.");
        }
    }
}
