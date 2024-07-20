package ag.act.enums;

import lombok.Getter;

@Getter
public enum UserBadgeType {
    TOTAL_ASSET("자산", "isVisibilityTotalAsset"),
    STOCK_QUANTITY("주식수", "isVisibilityStockQuantity");

    private final String label;
    private final String badgeName;

    UserBadgeType(String label, String badgeName) {
        this.label = label;
        this.badgeName = badgeName;
    }

}
