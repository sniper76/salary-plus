package ag.act.enums.admin;

import lombok.Getter;

@Getter
public enum BlockedUserFilterType {
    ALL("전체"),
    NORMAL_USER("일반 사용자"),
    SOLIDARITY_LEADER("주주대표");

    private final String displayName;

    BlockedUserFilterType(String displayName) {
        this.displayName = displayName;
    }

    public static BlockedUserFilterType fromValue(String filterTypeName) {
        try {
            return BlockedUserFilterType.valueOf(filterTypeName.toUpperCase());
        } catch (Exception e) {
            return ALL;
        }
    }
}
