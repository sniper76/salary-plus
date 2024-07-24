package ag.act.module.mydata;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Component
@ConfigurationProperties(prefix = "external.mydata")
public class MyDataConfig {
    @Getter
    private String baseUrl;
    @Getter
    private String aes256key;
    private Client client;

    public String getClientId() {
        return client.getId();
    }

    public String getClientSecret() {
        return client.getSecret();
    }

    @Setter
    @Getter
    static class Client {
        private String id;
        private String secret;
    }
}
