package ag.act.enums;

import lombok.Getter;

@Getter
public enum DigitalAnswerType {
    APPROVAL("찬성"),
    REJECTION("반대"),
    ABSTENTION("기권");

    private final String displayName;

    DigitalAnswerType(String displayName) {
        this.displayName = displayName;
    }

    public static DigitalAnswerType fromValue(String type) {
        try {
            return DigitalAnswerType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("지원하지 않는 DigitalAnswerType '%s' 타입입니다.".formatted(type));
        }
    }
}
