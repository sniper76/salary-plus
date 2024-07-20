package ag.act.enums.admin;

import lombok.Getter;

@Getter
public enum CampaignSearchType {
    TITLE("제목"),
    STOCK_GROUP_NAME("그룹명")
    ;

    private final String displayName;

    CampaignSearchType(String displayName) {
        this.displayName = displayName;
    }

    public static CampaignSearchType fromValue(String searchTypeName) {
        try {
            return CampaignSearchType.valueOf(searchTypeName.toUpperCase());
        } catch (NullPointerException | IllegalArgumentException e) {
            return TITLE;
        }
    }
}
