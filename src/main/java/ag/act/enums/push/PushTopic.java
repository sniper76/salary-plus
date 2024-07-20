package ag.act.enums.push;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum PushTopic {
    NOTICE("전체 공지");

    private final String displayName;

    PushTopic(String displayName) {
        this.displayName = displayName;
    }

    public static PushTopic fromValue(String pushTopicName) {
        try {
            return PushTopic.valueOf(pushTopicName.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("PushTopic.fromValue error - {}", e.getMessage(), e);
            return PushTopic.NOTICE;
        }
    }

    public String getDisplayName() {
        return this.displayName;
    }
}
