package ag.act.enums;

import ag.act.util.NumberUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum Nationality {
    KOREAN("L") {
        @Override
        public String getTrimmedName(String name) {
            return name.replaceAll("\\s", "");
        }
    },
    FOREIGNER("F") {
        @Override
        public String getTrimmedName(String name) {
            return name.replaceAll("\\s{2,}", " ").trim();
        }
    };

    private final String type;

    Nationality(String type) {
        this.type = type;
    }

    public static Nationality of(String firstNumberOfIdentification) {
        if (NumberUtil.toInteger(firstNumberOfIdentification, 1) >= 5) {
            return Nationality.FOREIGNER;
        }
        return Nationality.KOREAN;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static Nationality fromValue(String typeName) {
        for (Nationality b : Nationality.values()) {
            if (b.getType().equals(typeName)) {
                return b;
            }
        }
        throw new IllegalArgumentException("지원하지 않는 Nationality '%s' 타입입니다.".formatted(typeName));
    }

    public abstract String getTrimmedName(String name);
}
