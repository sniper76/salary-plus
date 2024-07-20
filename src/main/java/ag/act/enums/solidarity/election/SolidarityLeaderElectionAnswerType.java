package ag.act.enums.solidarity.election;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum SolidarityLeaderElectionAnswerType {
    APPROVAL("찬성"),
    REJECTION("반대");

    private final String displayName;

    SolidarityLeaderElectionAnswerType(String displayName) {
        this.displayName = displayName;
    }

    public static SolidarityLeaderElectionAnswerType fromValue(String type) {
        try {
            return SolidarityLeaderElectionAnswerType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("지원하지 않는 SolidarityLeaderElectionAnswerType '%s' 타입입니다.".formatted(type));
        }
    }

    public static SolidarityLeaderElectionAnswerType fromText(String text) {
        return Arrays.stream(values())
            .filter(value -> value.displayName.equals(text))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 SolidarityLeaderElectionAnswerType '%s' 타입입니다.".formatted(text)));
    }

    public boolean isApproval() {
        return this == APPROVAL;
    }

    public boolean isRejection() {
        return this == REJECTION;
    }
}
