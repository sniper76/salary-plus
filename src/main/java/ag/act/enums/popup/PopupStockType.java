package ag.act.enums.popup;

import lombok.Getter;

@Getter
public enum PopupStockType {
    STOCK,
    STOCK_ALL,
    STOCK_GROUP;

    public static PopupStockType fromValue(String typeName) {
        try {
            return PopupStockType.valueOf(typeName.toUpperCase());
        } catch (Exception e) {
            return STOCK_ALL;
        }
    }
}
