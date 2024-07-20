package ag.act.enums.popup;

import ag.act.exception.BadRequestException;
import lombok.Getter;

@Getter
public enum PopupDisplayTargetType {
    MAIN_HOME("메인홈"),
    NEWS_HOME("News홈"),
    STOCK_HOME("종목홈");

    private final String displayName;

    PopupDisplayTargetType(String displayName) {
        this.displayName = displayName;
    }

    public static PopupDisplayTargetType fromValue(String targetTypeName) {
        try {
            return PopupDisplayTargetType.valueOf(targetTypeName.toUpperCase());
        } catch (Exception e) {
            throw new BadRequestException("지원하지 않는 PopupTargetType '%s' 타입입니다.".formatted(targetTypeName), e);
        }
    }

}
