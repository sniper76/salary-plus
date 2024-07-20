package ag.act.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileType {

    IMAGE("contents/images", true),
    ATTACHMENT("contents/attaches", true),
    MY_DATA("contents/mydata", false),
    DIGITAL_DOCUMENT("contents/digitaldocument", false),
    CAMPAIGN_DIGITAL_DOCUMENT("contents/campaign", false),
    MATRIX("contents/matrix", false),
    SOLIDARITY_LEADER_CONFIDENTIAL_AGREEMENT_SIGN("contents/solidarity-leader-confidential-agreement-sign", false),
    ;

    private final String pathPrefix;
    private final Boolean isPublic;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static FileType fromValue(String typeName) {
        for (FileType fileType : FileType.values()) {
            if (fileType.name().equals(typeName)) {
                return fileType;
            }
        }
        throw new IllegalArgumentException("지원하지 않는 FileType '%s' 타입입니다.".formatted(typeName));
    }
}
