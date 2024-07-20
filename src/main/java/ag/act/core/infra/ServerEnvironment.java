package ag.act.core.infra;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ServerEnvironment {
    private static final String PROD = "prod";

    @Value("${act.environment:prod}")
    private String actEnvironment;

    public boolean isProd() {
        return PROD.equals(actEnvironment);
    }
}
