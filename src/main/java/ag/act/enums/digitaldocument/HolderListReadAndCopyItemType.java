package ag.act.enums.digitaldocument;

import ag.act.exception.BadRequestException;
import ag.act.util.DateTimeFormatUtil;
import ag.act.util.KoreanDateTimeUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static ag.act.enums.digitaldocument.HolderListReadAndCopyItemAnswerType.AUTO_FILL;
import static ag.act.enums.digitaldocument.HolderListReadAndCopyItemAnswerType.MIXED;
import static ag.act.enums.digitaldocument.HolderListReadAndCopyItemAnswerType.SUBJECTIVITY;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Getter
@RequiredArgsConstructor
public enum HolderListReadAndCopyItemType {
    LEADER_NAME(AUTO_FILL, "발신인 이름", "TEXT", 1, ""),
    COMPANY_NAME(AUTO_FILL, "발행회사 이름", "TEXT", 2, ""),
    LEADER_ADDRESS(MIXED, "발신인 주소", "TEXT", 3, "주소를 입력해 주세요."),
    CEO_NAME(SUBJECTIVITY, "발행회사 대표 이름", "TEXT", 4, "대표이름을 입력해 주세요."),
    COMPANY_ADDRESS(MIXED, "발행회사 주소", "TEXT", 5, "주소를 입력해 주세요."),
    IR_PHONE_NUMBER(MIXED, "발행회사 IR담당자 전화번호", "TEXT", 6, "IR담당자 전화번호를 입력해 주세요."),
    DEADLINE_DATE_BY_LEADER1(
        SUBJECTIVITY, "발신인이 정하는 기한의 연/월/일/시", "DATETIME", 7, ""
    ) {
        @Override
        public String getFormattedValue(Object value) {
            validate(value);
            final ZonedDateTime dateTime = KoreanDateTimeUtil.toKoreanTime(Instant.parse(value.toString()));
            return DateTimeFormatUtil.yyyy_MM_dd_HH_korean().format(dateTime);
        }

        @Override
        public void validate(Object value) {
            super.validate(value);
            try {
                Instant.parse(value.toString());
            } catch (Exception e) {
                throw new BadRequestException("발신인이 정하는 기한 값이 잘못되었습니다.");
            }
        }

        public String getMaxDatetimeConstraint() {
            return DEADLINE_DATE_BY_LEADER2.name();
        }
    },
    DEADLINE_DATE_BY_LEADER2(
        SUBJECTIVITY, "발신인이 정하는 기한의 연/월/일/시", "DATETIME", 8, ""
    ) {
        @Override
        public String getFormattedValue(Object value) {
            validate(value);
            final ZonedDateTime dateTime = KoreanDateTimeUtil.toKoreanTime(Instant.parse(value.toString()));
            return DateTimeFormatUtil.yyyy_MM_dd_HH_korean().format(dateTime);
        }

        @Override
        public void validate(Object value) {
            super.validate(value);
            try {
                Instant.parse(value.toString());
            } catch (Exception e) {
                throw new BadRequestException("발신인이 정하는 기한 값이 잘못되었습니다.");
            }
        }

        public String getMinDatetimeConstraint() {
            return DEADLINE_DATE_BY_LEADER1.name();
        }
    },
    REFERENCE_DATE_BY_LEADER(
        MIXED, "발신인이 정하는 주주명부 기준일", "TEXT", 9, "기준일을 입력해 주세요."
    ) {
        private static final String regex = "^[ㄱ-ㅎ가-힣0-9 ]*$";

        @Override
        public void validate(Object value) {
            super.validate(value);
            if (!((String) value).matches(regex)) {
                throw new BadRequestException("주주명부 기준일은 한글과 숫자만 입력 가능합니다.");
            }
        }

        @Override
        public String getDescription() {
            return "특정일자(임시주주총회)의 주주명부 필요시 입력";
        }
    },
    LEADER_EMAIL(MIXED, "발신인 메일주소", "TEXT", 10, "메일주소를 입력해 주세요.") {
        private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

        @Override
        public void validate(Object value) {
            super.validate(value);

            if (!(refineValue((String) value)).matches(EMAIL_REGEX)) {
                throw new BadRequestException("이메일 형식이 잘못되었습니다.");
            }
        }
    };

    private static final int DATA_MAX_LENGTH = 100;
    private final HolderListReadAndCopyItemAnswerType holderListReadAndCopyItemAnswerType;
    private final String title;
    private final String dataType;
    private final int displayOrder;
    private final String message;

    public String getFormattedValue(Object value) {
        validate(value);
        return refineValue((String) value);
    }

    protected String refineValue(String value) {
        return value.replaceAll("\\s\\s", " ").trim();
    }

    public void validate(Object value) {
        if (value == null || isBlank(value.toString())) {
            throw new BadRequestException(getDefaultValidationFailMessage());
        }

        if (value.toString().length() > getDataMaxLength()) {
            throw new BadRequestException("입력값은 %s자를 넘을 수 없습니다.".formatted(getDataMaxLength()));
        }
    }

    public String getDefaultValidationFailMessage() {
        return "%s 항목을 입력해주세요.".formatted(title);
    }

    public int getDataMaxLength() {
        return DATA_MAX_LENGTH;
    }

    public String getDescription() {
        return "";
    }

    public String getMaxDatetimeConstraint() {
        return "";
    }

    public String getMinDatetimeConstraint() {
        return "";
    }

    public static HolderListReadAndCopyItemType fromValue(String value) {
        try {
            return HolderListReadAndCopyItemType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("존재하지 않는 답변 타입입니다.", e);
        }
    }

    public static List<HolderListReadAndCopyItemType> getSortedList() {
        return Arrays.stream(HolderListReadAndCopyItemType.values())
            .sorted(Comparator.comparingInt(HolderListReadAndCopyItemType::getDisplayOrder))
            .toList();
    }
}
