package ag.act.enums.admin;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserSearchType {
    NAME("이름"),
    USER_ID("아이디"),
    NICKNAME("닉네임"),
    PHONE_NUMBER("휴대번호"),
    EMAIL("이메일");

    private final String displayName;

    public static UserSearchType fromValue(String searchTypeName) {
        try {
            return UserSearchType.valueOf(searchTypeName.toUpperCase());
        } catch (Exception e) {
            return NAME;
        }
    }

    public boolean needEncrypt() {
        return this == PHONE_NUMBER;
    }
}
