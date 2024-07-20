package ag.act.enums.push;

import ag.act.exception.BadRequestException;
import lombok.Getter;

import static ag.act.enums.push.UserPushAgreementGroupType.AUTHOR;
import static ag.act.enums.push.UserPushAgreementGroupType.CMS;

@Getter
public enum UserPushAgreementType {
    ACT_NOTICE(CMS),
    STOCK_NOTICE(CMS),
    RECOMMENDATION(CMS),
    ACT_BEST_ENTER(AUTHOR),
    NEW_COMMENT(AUTHOR);

    private final UserPushAgreementGroupType group;

    UserPushAgreementType(UserPushAgreementGroupType group) {
        this.group = group;
    }

    public static UserPushAgreementType fromValue(String value) {
        try {
            return UserPushAgreementType.valueOf(value.toUpperCase());
        } catch (Exception e) {
            throw new BadRequestException("지원하지 않는 UserPushAgreementType '%s' 입니다.".formatted(value));
        }
    }
}
