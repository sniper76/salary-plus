package ag.act.enums.push;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Slf4j
public enum PushSendStatus {
    READY("준비"),
    PROCESSING("발송중"),
    COMPLETE("발송완료"),
    FAIL("발송실패");

    private final String displayName;

    PushSendStatus(String displayName) {
        this.displayName = displayName;
    }

    public static PushSendStatus fromValue(String sendStatusName) {
        if (StringUtils.isBlank(sendStatusName)) {
            log.error("PushSendStatus.fromValue error - `{}`", sendStatusName);
            throw new IllegalArgumentException("지원하지 않는 PushSendStatus '%s' 타입입니다.".formatted(sendStatusName));
        }

        try {
            return PushSendStatus.valueOf(sendStatusName.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("PushSendStatus.fromValue error - {}", e.getMessage(), e);
            throw new IllegalArgumentException("지원하지 않는 PushSendStatus '%s' 타입입니다.".formatted(sendStatusName));
        }
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public static List<PushSendStatus> notReady() {
        return List.of(PROCESSING, COMPLETE, FAIL);
    }
}
