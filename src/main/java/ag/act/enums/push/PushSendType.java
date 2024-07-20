package ag.act.enums.push;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public enum PushSendType {
    SCHEDULE("예약발송"),
    IMMEDIATELY("즉시발송");

    private final String displayName;

    PushSendType(String displayName) {
        this.displayName = displayName;
    }

    public static PushSendType fromValue(String sendTypeName) {
        if (StringUtils.isBlank(sendTypeName)) {
            log.error("PushSendType.fromValue error - `{}`", sendTypeName);
            throw new IllegalArgumentException("지원하지 않는 PushSendType '%s' 타입입니다.".formatted(sendTypeName));
        }

        try {
            return PushSendType.valueOf(sendTypeName.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("PushSendType.fromValue error - {}", e.getMessage(), e);
            throw new IllegalArgumentException("지원하지 않는 PushSendType '%s' 타입입니다.".formatted(sendTypeName));
        }
    }

    public String getDisplayName() {
        return this.displayName;
    }
}
