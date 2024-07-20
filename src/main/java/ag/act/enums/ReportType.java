package ag.act.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ReportType {
    POST,
    COMMENT;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ReportType fromValue(String value) {
        try {
            return ReportType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("지원하지 않는 ReportType '%s' 타입입니다.".formatted(value));
        }
    }
}
