package ag.act.module.markany.dna;

import ag.act.exception.InternalServerException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ConfigurationProperties(prefix = "external.markany.dna")
@Component
@Getter
@Setter
@Slf4j
public class MarkAnyConfig {
    private static final String HOST_DELIMITER = ";";

    private boolean enabled;
    private String hosts;
    private int port;
    private String watermark;

    private List<String> hostList;
    private boolean isSetHosts;

    public String getHosts() {
        if (hostList.size() == 1) {
            return hostList.get(0);
        }

        List<String> shuffledHosts = new ArrayList<>(hostList);
        Collections.shuffle(shuffledHosts);
        return String.join(HOST_DELIMITER, shuffledHosts);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        setHostList();
    }

    public void setHosts(String hosts) {
        this.isSetHosts = true;
        this.hosts = hosts;
        setHostList();
    }

    private void setHostList() {
        if (!enabled || !isSetHosts) {
            return;
        }

        if (StringUtils.isBlank(hosts)) {
            throw new InternalServerException(getFormattedErrorMessage(hosts));
        }

        this.hostList = Arrays.stream(hosts.split(HOST_DELIMITER)).filter(StringUtils::isNotBlank).toList();

        if (this.hostList.isEmpty()) {
            throw new InternalServerException(getFormattedErrorMessage(hosts));
        }

        log.info("[MarkAny] hostList: {}", this.hostList);
    }

    private String getFormattedErrorMessage(String hosts) {
        return "[MarkAny] hosts must not be empty. hosts=[%s]".formatted(hosts);
    }
}
