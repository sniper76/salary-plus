package ag.act.enums.push;

import lombok.Getter;

@Getter
public enum UserPushAgreementItemType {
    ALL("푸시 알림 받기(전체)"),
    SUB(null);

    private final String title;

    UserPushAgreementItemType(String title) {
        this.title = title;
    }
}
