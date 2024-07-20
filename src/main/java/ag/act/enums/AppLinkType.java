package ag.act.enums;

import ag.act.exception.BadRequestException;
import lombok.Getter;

@Getter
public enum AppLinkType {
    LINK(1),
    NOTIFICATION(2),
    MAIN_HOME(3),
    NEWS_HOME(4),
    STOCK_HOME(5),
    DIGITAL_DOCUMENT_HOME(6),
    NONE(100);

    private final int sortOrder;

    public int getSortOrder() {
        return sortOrder;
    }

    AppLinkType(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public static AppLinkType fromValue(String targetTypeName) {
        try {
            return AppLinkType.valueOf(targetTypeName.toUpperCase());
        } catch (Exception e) {
            throw new BadRequestException("지원하지 않는 AppLinkType %s 입니다.".formatted(targetTypeName));
        }
    }

}
