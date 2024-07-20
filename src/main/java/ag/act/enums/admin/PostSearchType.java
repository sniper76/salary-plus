package ag.act.enums.admin;

import lombok.Getter;

@Getter
public enum PostSearchType {
    TITLE("제목"),
    CONTENT("내용"),
    TITLE_AND_CONTENT("제목+내용"),
    STOCK_CODE("종목코드");

    private final String displayName;

    PostSearchType(String displayName) {
        this.displayName = displayName;
    }

    public static PostSearchType fromValue(String searchTypeName) {
        try {
            return PostSearchType.valueOf(searchTypeName.toUpperCase());
        } catch (NullPointerException | IllegalArgumentException e) {
            return TITLE;
        }
    }
}
