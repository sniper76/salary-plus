package ag.act.enums.digitaldocument;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum HolderListReadAndCopyItemAnswerType {
    AUTO_FILL("자동기입"),
    SUBJECTIVITY("주관식"),
    MIXED("혼합형");

    private final String displayName;
}
