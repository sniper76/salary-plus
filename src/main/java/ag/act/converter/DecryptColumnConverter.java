package ag.act.converter;

import ag.act.configuration.security.CryptoHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DecryptColumnConverter {
    private final CryptoHelper cryptoHelper;

    public DecryptColumnConverter(CryptoHelper cryptoHelper) {
        this.cryptoHelper = cryptoHelper;
    }

    public String convert(String value) {
        try {
            return cryptoHelper.decrypt(value);
        } catch (Exception e) {
            log.warn("Failed to decrypt phone number exception : {}", e.getLocalizedMessage());
            return "";
        }
    }
}
