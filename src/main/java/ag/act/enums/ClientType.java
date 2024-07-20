package ag.act.enums;

import ag.act.exception.BadRequestException;

public enum ClientType {

    APP,
    CMS,
    WEB;

    public static ClientType fromValue(String value) {
        try {
            return ClientType.valueOf(value.toUpperCase());
        } catch (Exception e) {
            throw new BadRequestException("지원하지 않는 Client Type %s 입니다.".formatted(value));
        }
    }
}
