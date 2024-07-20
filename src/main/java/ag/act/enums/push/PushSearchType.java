package ag.act.enums.push;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum PushSearchType {
    PUSH_TITLE("제목"),
    PUSH_CONTENT("내용"),
    AUTHOR_NAME("이름"),
    AUTHOR_NICKNAME("닉네임"),
    STOCK_NAME("종목"),
    STOCK_GROUP_NAME("종목그룹");

    private final String displayName;

    PushSearchType(String displayName) {
        this.displayName = displayName;
    }

    public static PushSearchType fromValue(String searchTypeName) {
        try {
            return PushSearchType.valueOf(searchTypeName.toUpperCase());
        } catch (NullPointerException | IllegalArgumentException e) {
            return PUSH_CONTENT;
        }
    }

    public String getDisplayName() {
        return this.displayName;
    }
}
