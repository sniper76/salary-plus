package ag.act.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

public enum MediaType {
    JPG("image/jpeg"),
    PNG("image/png"),
    GIF("image/gif"),
    BMP("image/bmp"),
    TIFF("image/tiff"),
    ;

    @Getter
    private final String typeName;

    MediaType(String typeName) {
        this.typeName = typeName;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static MediaType fromValue(String typeName) {
        for (MediaType b : MediaType.values()) {
            if (b.getTypeName().equals(typeName)) {
                return b;
            }
        }
        throw new IllegalArgumentException("지원하지 않는 MediaType '%s' 타입입니다.".formatted(typeName));
    }

    public static List<String> getSupportedExtensions() {
        return Arrays.stream(MediaType.values())
            .map(MediaType::name)
            .map(String::toLowerCase)
            .toList();
    }
}
