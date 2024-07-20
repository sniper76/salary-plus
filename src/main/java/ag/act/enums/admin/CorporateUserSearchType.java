package ag.act.enums.admin;

import lombok.Getter;

@Getter
public enum CorporateUserSearchType {
    CORPORATE_NAME("법인명")
    ;

    private final String displayName;

    CorporateUserSearchType(String displayName) {
        this.displayName = displayName;
    }

    public static CorporateUserSearchType fromValue(String searchTypeName) {
        try {
            return CorporateUserSearchType.valueOf(searchTypeName.toUpperCase());
        } catch (NullPointerException | IllegalArgumentException e) {
            return CORPORATE_NAME;
        }
    }
}
