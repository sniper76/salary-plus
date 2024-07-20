package ag.act.converter.user;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class UserIpConverter {

    private static final String DEFAULT_IP_PREFIX = "192.168";

    public String convert(String userIp) {

        if (StringUtils.isBlank(userIp)) {
            return DEFAULT_IP_PREFIX;
        }

        final String[] ipOctets = userIp.trim().split("\\.");

        if (ipOctets.length != 4) {
            return DEFAULT_IP_PREFIX;
        }

        return ipOctets[0] + "." + ipOctets[1];
    }
}
