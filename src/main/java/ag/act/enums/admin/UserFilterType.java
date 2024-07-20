package ag.act.enums.admin;

import ag.act.enums.RoleType;
import lombok.Getter;

import java.util.Optional;

@Getter
public enum UserFilterType {
    ALL("전체", null),
    ADMIN("관리자", RoleType.ADMIN),
    SOLIDARITY_LEADER("주주대표", null),
    ACCEPTOR_USER("수임인", RoleType.ACCEPTOR_USER);

    private final String displayName;
    private final RoleType roleType;

    UserFilterType(String displayName, RoleType roleType) {
        this.displayName = displayName;
        this.roleType = roleType;
    }

    public static UserFilterType fromValue(String filterTypeName) {
        try {
            return UserFilterType.valueOf(filterTypeName.toUpperCase());
        } catch (Exception e) {
            return ALL;
        }
    }

    public Optional<RoleType> getRoleType() {
        return Optional.ofNullable(this.roleType);
    }
}
