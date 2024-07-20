package ag.act.enums.admin;

import lombok.Getter;

@Getter
public enum CompareTextType {
    COMPARE_MONTH("전월대비"),
    COMPARE_DAY("전일대비");

    private final String displayName;

    CompareTextType(String displayName) {
        this.displayName = displayName;
    }
}
