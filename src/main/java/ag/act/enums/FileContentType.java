package ag.act.enums;

import ag.act.exception.BadRequestException;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum FileContentType {

    DEFAULT,
    DEFAULT_PROFILE,
    ;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static FileContentType fromValue(String typeName) {
        for (FileContentType b : FileContentType.values()) {
            if (b.name().equals(typeName)) {
                return b;
            }
        }
        throw new BadRequestException("지원하지 않는 FileContentType '%s' 타입입니다.".formatted(typeName));
    }
}

