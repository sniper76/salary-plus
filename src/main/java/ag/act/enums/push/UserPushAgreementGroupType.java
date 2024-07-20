package ag.act.enums.push;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum UserPushAgreementGroupType {
    CMS("액트공지 / 종목알림 / 추천글"),
    AUTHOR("액트베스트 진입 / 새글");

    private final String title;

    UserPushAgreementGroupType(String title) {
        this.title = title;
    }

    public List<UserPushAgreementType> getAgreementTypes() {
        return Arrays.stream(UserPushAgreementType.values())
            .filter(it -> this == it.getGroup())
            .toList();
    }

}
