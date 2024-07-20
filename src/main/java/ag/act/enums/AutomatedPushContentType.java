package ag.act.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum AutomatedPushContentType {
    POST,
    COMMENT;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static AutomatedPushContentType fromValue(String value) {
        try {
            return AutomatedPushContentType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("지원하지 않는 AutomatedPushContentType '%s' 타입입니다.".formatted(value));
        }
    }
}
