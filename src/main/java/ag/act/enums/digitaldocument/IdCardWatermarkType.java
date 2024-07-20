package ag.act.enums.digitaldocument;

public enum IdCardWatermarkType {
    NONE,
    ACT_LOGO,
    ACT_LOGO_WITH_DATE
    ;

    public static IdCardWatermarkType fromValue(String type) {
        try {
            return IdCardWatermarkType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return NONE;
        }
    }
}
