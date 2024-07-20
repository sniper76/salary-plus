package ag.act.enums.popup;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum PopupSearchType {
    TITLE("제목");

    private final String displayName;

    PopupSearchType(String displayName) {
        this.displayName = displayName;
    }

    public static PopupSearchType fromValue(String searchTypeName) {
        try {
            return PopupSearchType.valueOf(searchTypeName.toUpperCase());
        } catch (NullPointerException | IllegalArgumentException e) {
            return TITLE;
        }
    }

    public String getDisplayName() {
        return this.displayName;
    }
}
