package ag.act.util;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class TokenUtil {
    public String parse(String bearerToken) {
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
