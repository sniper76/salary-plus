package ag.act.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

public enum RoleType {
    SUPER_ADMIN,
    ADMIN,
    USER,
    ACCEPTOR_USER,
    CEO,
    INVESTOR,
    MANAGER,
    SUPERVISOR,
    ;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static RoleType fromValue(String value) {
        for (RoleType b : RoleType.values()) {
            if (b.name().equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("지원하지 않는 RoleType '%s' 타입입니다.".formatted(value));
    }

    public static List<RoleType> getAdminRoleTypes() {
        return List.of(SUPER_ADMIN, ADMIN);
    }

    public static Boolean isAdminRoleType(RoleType roleType) {
        return getAdminRoleTypes().contains(roleType);
    }

    public static Boolean isSuperAdminRoleType(RoleType roleType) {
        return SUPER_ADMIN == roleType;
    }

    public static Boolean isAcceptorUserRoleType(RoleType roleType) {
        return ACCEPTOR_USER == roleType;
    }
}